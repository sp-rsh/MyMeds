package org.example.mymedsbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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


    public record LoginRequest(String username, String password) {
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Username and password are required.");
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            UsernamePasswordAuthenticationToken.unauthenticated(
                                    request.username(),
                                    request.password()
                            )
                    );


            User user = userRepo.findByUsername(authentication.getName());
            if (user == null || !user.isActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            user.setLastLoginAt(LocalDateTime.now());
            userRepo.save(user);

            securityLog.record("Login", user.getUsername(), "Login", "Success", "Successful authentication");

            if (user.isTotpActive()) {
                return ResponseEntity.ok(Map.of(
                        "authenticated", false,
                        "totpRequired", true
                ));
            }

            httpRequest.getSession(true);
            httpRequest.changeSessionId();

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(
                    context,
                    httpRequest,
                    httpResponse
            );



            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "totpRequired", false
            ));


        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password.");
        }
    }

}
