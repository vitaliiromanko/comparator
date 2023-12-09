package com.nulp.project2.controller;

import com.nulp.project2.model.user.Role;
import com.nulp.project2.model.user.Status;
import com.nulp.project2.model.user.User;
import com.nulp.project2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('edit_user_data')")
    public String showUserListPage(Model model) {
        model.addAttribute("users", userService.findActivatedUsers());
        model.addAttribute("selectedPage", "userListPage");
        return "userListPage";
    }

    @GetMapping("/user/{user}/edit")
    @PreAuthorize("hasAuthority('edit_user_data')")
    public String showUserEditForm(
            @AuthenticationPrincipal User admin,
            @PathVariable User user,
            Model model
    ) {
        if (Objects.equals(admin.getId(), user.getId())) {
            return "redirect:/user";
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", Set.of(Status.ACTIVATED, Status.BANNED));

        model.addAttribute("timeFormat", "yyyy-MM-dd'T'HH:mm:ss");

        model.addAttribute("selectedPage", "userListPage");
        return "userEditPage";
    }

    @PostMapping("/user/{user}/edit")
    @PreAuthorize("hasAuthority('edit_user_data')")
    public String updateUserInfo(
            @RequestParam(value = "role") Role newRole,
            @RequestParam(value = "status") Status newStatus,
            @PathVariable User user,
            RedirectAttributes redirectAttributes
    ) {
        userService.updateUserInfo(user, newRole, newStatus);
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Дані користувача успішно оновлені!");
        return "redirect:/user/" + user.getId() + "/edit";
    }

    @PostMapping("/user/{user}/delete")
    @PreAuthorize("hasAuthority('edit_user_data')")
    public String deleteUser(
            @AuthenticationPrincipal User admin,
            @PathVariable User user
    ) {
        if (Objects.equals(admin.getId(), user.getId())) {
            return "redirect:/user";
        }
        userService.deleteUser(user);
        return "redirect:/user";
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String showProfilePage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        model.addAttribute("selectedPage", "profilePage");
        return "profilePage";
    }

    @PostMapping("/user/profile")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Model model
    ) {
        Map<String, String> messageMap = userService.updateProfile(user, firstName, lastName, username, oldPassword, newPassword);

        if (!messageMap.isEmpty()) {
            model.mergeAttributes(messageMap);
        }

        User userFromDb = userService.findUserById(user.getId());

        model.addAttribute("user", userFromDb);
        model.addAttribute("selectedPage", "profilePage");
        return "profilePage";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("selectedPage", "forgotPasswordPage");
        return "forgotPasswordPage";
    }

    @PostMapping("/forgot-password")
    public String sendPasswordRecoveryLetter(
            @RequestParam String username,
            @RequestParam String email,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (userService.sendPasswordRecoveryCode(username, email)) {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Повідомлення надіслано!");
            return "redirect:/login";
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Ви ввели некоректні дані!");
            model.addAttribute("selectedPage", "forgotPasswordPage");

            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "forgotPasswordPage";
        }
    }

    @GetMapping("/password-recovery/{code}")
    public String showPasswordRecoveryPage(
            @PathVariable(value = "code") String passwordRecoveryCode,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (userService.isPasswordRecoveryCodeValid(passwordRecoveryCode)) {
            model.addAttribute("selectedPage", "passwordRecoveryPage");
            model.addAttribute("code", passwordRecoveryCode);
            return "passwordRecoveryPage";
        } else {
            redirectAttributes.addFlashAttribute("messageType", "danger");
            redirectAttributes.addFlashAttribute("message", "Вказаного коду відновлення паролю не знайдено!");
            return "redirect:/login";
        }
    }

    @PostMapping("/password-recovery/{code}")
    public String recoverUserPassword(
            @PathVariable(value = "code") String passwordRecoveryCode,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        if (userService.recoverUserPassword(passwordRecoveryCode, password)) {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Ваш пароль успішно оновлено!");
        } else {
            redirectAttributes.addFlashAttribute("messageType", "danger");
            redirectAttributes.addFlashAttribute("message", "Вказаного коду відновлення паролю не знайдено!");
        }
        return "redirect:/login";
    }
}
