package org.example.mymedsbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.ArrayList;
import java.util.List;

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

            List<GrantedAuthority> authorities = new ArrayList<>(credentials.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_API")); // so we can differentiate device requests from user

            return new PreAuthenticatedAuthenticationToken(credentials, null, authorities);
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
