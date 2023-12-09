package com.nulp.project2.service;

import com.nulp.project2.model.user.*;
import com.nulp.project2.repository.user.ActivationTokenRepository;
import com.nulp.project2.repository.user.ResetPasswordTokenRepository;
import com.nulp.project2.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SmtpMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final ActivationTokenRepository activationTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Value("${site.domain}")
    private String siteDomain;

    private void sendActivationCode(User user, String activationToken) {
        String message = String.format(
                "Привіт, %s!\n" +
                        "Вітаємо на сайті Порівнювач.\n" +
                        "Для активації облікового запиту перейдіть за наступним посиланням:\n" +
                        siteDomain + "/activate/%s",
                user.getUsername(),
                activationToken
        );
        mailSender.send(user.getEmail(), "Активація акаунта", message);
    }

    public int addUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent() || user.getUsername().equals("Гість") || user.getUsername().equals("anonymousUser")) {
            return -1;
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return -2;
        }

        user.setStatus(Status.NO_ACTIVATED);
        user.setRole(Role.USER);

        ActivationToken activationToken = ActivationToken.of(user);
        user.getActivationTokens().add(activationToken);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        sendActivationCode(user, activationToken.getToken());

        return 1;
    }

    public boolean activateUser(String code) {
        ActivationToken activationToken = activationTokenRepository.findByToken(code).orElse(null);

        if (activationToken == null) {
            return false;
        }

        User user = activationToken.getUser();

        user.getActivationTokens().clear();
        user.setStatus(Status.ACTIVATED);

        userRepository.save(user);

        return true;
    }

    public List<User> findActivatedUsers() {
        List<User> users = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            if (!user.getStatus().equals(Status.NO_ACTIVATED)) {
                users.add(user);
            }
        }

        return users;
    }

    public void updateUserInfo(User user, Role newRole, Status newStatus) {
        user.setRole(newRole);
        user.setStatus(newStatus);
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public Map<String, String> updateProfile(User user, String firstName, String lastName, String username, String oldPassword, String newPassword) {
        Map<String, String> messageMap = new HashMap<>();
        boolean updateUser = false;

        if (!user.getFirstName().equals(firstName)) {
            user.setFirstName(firstName);
            updateUser = true;
        }

        if (!user.getLastName().equals(lastName)) {
            user.setLastName(lastName);
            updateUser = true;
        }

        if (!user.getUsername().equals(username)) {
            if (userRepository.findByUsername(username).orElse(null) == null && !username.equals("Гість") && !username.equals("anonymousUser")) {
                user.setUsername(username);
                updateUser = true;
            } else {
                messageMap.put("usernameError", "Вказаний логін вже використовується!");
            }
        }

        if (!oldPassword.isEmpty()) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                messageMap.put("oldPasswordError", "Ви некоректно ввели свій пароль!");
            } else if (oldPassword.equals(newPassword)) {
                messageMap.put("newPasswordError", "Ви ввели ідентичні паролі!");
            } else {
                user.setPassword(passwordEncoder.encode(newPassword));
                updateUser = true;
            }
        }

        if (updateUser) {
            userRepository.save(user);
            messageMap.put("message", "Ваші дані оновлено!");
            messageMap.put("messageType", "success");
        }

        return messageMap;
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean sendPasswordRecoveryCode(String username, String email) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && email.equals(user.getEmail())) {
            ResetPasswordToken resetPasswordToken = ResetPasswordToken.of(user);
            user.getResetPasswordTokens().add(resetPasswordToken);

            userRepository.save(user);
            sendPasswordRecoveryLetter(user, resetPasswordToken.getToken());
            return true;
        }
        return false;
    }

    private void sendPasswordRecoveryLetter(User user, String resetPasswordToken) {
        String message = String.format(
                "Привіт, %s!\n" +
                        "Був надісланий запит на відновлення вашого паролю.\n" +
                        "Для цього перейдіть за наступним посиланням:\n" +
                        siteDomain + "/password-recovery/%s\n\n" +
                        "Якщо ви не робили запиту, то проігноруйте дане повідомлення.",
                user.getUsername(),
                resetPasswordToken
        );
        mailSender.send(user.getEmail(), "Відновлення паролю", message);
    }

    public boolean isPasswordRecoveryCodeValid(String code) {
        return resetPasswordTokenRepository.findByToken(code).orElse(null) != null;
    }

    public boolean recoverUserPassword(String passwordRecoveryCode, String newPassword) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(passwordRecoveryCode).orElse(null);

        if (resetPasswordToken == null) {
            return false;
        }

        User user = resetPasswordToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        user.getResetPasswordTokens().clear();
        userRepository.save(user);

        return true;
    }
}
