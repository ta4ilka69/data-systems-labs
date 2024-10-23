package itmo.labs.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return PasswordEncoderUtil.encode(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encodedRawPassword = PasswordEncoderUtil.encode(rawPassword.toString());
        return encodedRawPassword.equals(encodedPassword);
    }
}