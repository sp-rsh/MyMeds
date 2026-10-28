package org.example.mymedsbackend.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class TotpService {

    private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

    public String generateSecret() {
        GoogleAuthenticatorKey key =
                authenticator.createCredentials();

        return key.getKey();
    }

    public boolean isValid(String secret, int code) {
        return authenticator.authorize(secret, code);
    }

    public String createOtpAuthUri(String username, String secret) {
        String label = username + " at mymedz";

        return "otpauth://totp/"
                + urlEncode(label)
                + "?secret="
                + secret
                + "&issuer=MyMeds&algorithm=SHA1&digits=6&period=30";
    }

    private String urlEncode(String value) {
        return java.net.URLEncoder
                .encode(value, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}