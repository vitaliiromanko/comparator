package com.nulp.project2.config;

import com.nulp.project2.model.user.User;
import com.nulp.project2.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userRepository.findByUsername(username).orElse(null);

        String badCredentialsMessage = "Ви ввели некоректні дані!";
        String lockedMessage = "Ваш акаунт був заблокований!";
        String disabledMessage = "Ваш акаунт не активований!";

        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                if (!user.isAccountNonLocked()) {
                    exception = new LockedException(lockedMessage);
                } else if (!user.isEnabled()) {
                    exception = new DisabledException(disabledMessage);
                } else {
                    exception = new BadCredentialsException(badCredentialsMessage);
                }
            } else {
                exception = new BadCredentialsException(badCredentialsMessage);
            }
        } else {
            exception = new BadCredentialsException(badCredentialsMessage);
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
