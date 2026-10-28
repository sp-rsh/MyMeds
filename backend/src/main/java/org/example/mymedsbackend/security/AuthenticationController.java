package org.example.mymedsbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final UserRepo userRepo;
    private final SecurityLogService securityLog;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final TotpService totpService;


    public record LoginRequest(String username, String password) {
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        if (request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()) {

            return ResponseEntity.badRequest().body("Username and password are required.");
        }

        try {
            // god bless spring boot with their fucked up ways of doing auth
            Authentication authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));


            User user = userRepo.findByUsername(authentication.getName());
            if (user == null || !user.isActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }


            HttpSession session = httpRequest.getSession(true);
            httpRequest.changeSessionId();

            if (user.isTotpActive()) {
                session.setAttribute("totp_user", user.getUsername()); // make sure the 2fa token actually belongs to our guy

                return ResponseEntity.ok(Map.of("authenticated", false, "totpRequired", true));
            }

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            user.setLastLoginAt(LocalDateTime.now());
            userRepo.save(user);

            securityLog.record("Login", user.getUsername(), "Login", "Success", "Successful authentication");

            return ResponseEntity.ok(Map.of("authenticated", true, "totpRequired", false));


        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    public record TotpLoginRequest(String code) {
    }

    @PostMapping("/login/totp")
    public ResponseEntity<?> loginWithTotp(@RequestBody TotpLoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "No pending login found."));
        }

        String username = (String) session.getAttribute("totp_user");

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are doing somthing you're not supposed to");
        }

        if (request.code() == null) {
            return ResponseEntity.badRequest().body("No code >:(");
        }

        User user = userRepo.findByUsername(username);

        if (user == null || !user.isActive() || !user.isTotpActive() || user.getTotpSecret() == null) {
            session.invalidate();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("How did we get here?");
        }

        int code = Integer.parseInt(request.code());

        if (!totpService.isValid(user.getTotpSecret(), code)) {
            securityLog.record("Failed TOTP login", username, "Login", "Failed", "Invalid TOTP code");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect totp (try again?)");
        }

        SecurityCredentials credzz = new SecurityCredentials(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(credzz, null, credzz.getAuthorities());
        httpRequest.changeSessionId();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
        session.removeAttribute("totp_user");

        securityLog.record("Login", username, "Login", "Success", "Successful password and TOTP authentication");

        return ResponseEntity.ok(Map.of("authenticated", true, "totpRequired", false));
    }


    @PostMapping("/totp/setup")
    public ResponseEntity<?> setup(
            @AuthenticationPrincipal UserDetails principal,
            HttpSession session) {

        User user = userRepo.findByUsername(principal.getUsername());

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.isTotpActive()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("It's already on bro");
        }

        String secret = totpService.generateSecret();
        session.setAttribute("totp_secret", secret); // keep it session only for now :0

        String otpAuthUri = totpService.createOtpAuthUri(
                user.getUsername(),
                secret
        );

        return ResponseEntity.ok(Map.of(
                "secret", secret,
                "otpauthUri", otpAuthUri
        ));
    }

    @PostMapping("/totp/verify-setup")
    public ResponseEntity<?> verifySetup(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody VerifyTotpRequest request,
            HttpSession session) {

        User user = userRepo.findByUsername(principal.getUsername());

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.isTotpActive()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Is already on bro");
        }

        String pendingSecret = (String) session.getAttribute("totp_secret");

        if (pendingSecret == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message",
                            "Start TOTP setup before verifying a code."
                    ));
        }

        if (request.code() == null
                || !request.code().matches("\\d{6}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "TOTP code must contain six digits."
                    ));
        }

        int code = Integer.parseInt(request.code());

        if (!totpService.isValid(pendingSecret, code)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect code");
        }

        user.setTotpSecret(pendingSecret);
        user.setTotpActive(true);
        userRepo.save(user);

        session.removeAttribute("totp_secret");

        return ResponseEntity.ok(Map.of(
                "totpEnabled", true
        ));
    }

    public record VerifyTotpRequest(String code) {
    }
}
