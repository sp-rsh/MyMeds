package org.example.mymedsbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

public class DeviceKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    public DeviceKeyAuthenticationFilter(UserRepo userRepo) {
        setAuthenticationManager(authentication -> {
            String apiKey = (String) authentication.getPrincipal();

            if (apiKey == null || apiKey.trim().isEmpty()) {
                return null;
            }

            User user = userRepo.findByDeviceKey(apiKey);

            if (user == null || !user.isActive()) {
                return null;
            }

            SecurityCredentials credentials = new SecurityCredentials(user);
            return new PreAuthenticatedAuthenticationToken(
                    credentials,
                    null,
                    credentials.getAuthorities()
            );
        });
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
