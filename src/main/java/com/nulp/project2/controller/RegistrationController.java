package com.nulp.project2.controller;

import com.nulp.project2.model.user.User;
import com.nulp.project2.service.UserService;
import com.nulp.project2.util.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;


    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("selectedPage", "registrationPage");
        return "registrationPage";
    }

    @PostMapping("/registration")
    public String addNewUser(
            @Valid User user,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            model.addAttribute("selectedPage", "registrationPage");
            return "registrationPage";
        }

        int userServiceResult = userService.addUser(user);

        if (userServiceResult == -1) {
            model.addAttribute("usernameError", "Вказаний логін вже використовується!");
            model.addAttribute("selectedPage", "registrationPage");
            return "registrationPage";
        } else if (userServiceResult == -2) {
            model.addAttribute("emailError", "Вказана пошта вже використовується!");
            model.addAttribute("selectedPage", "registrationPage");
            return "registrationPage";
        } else {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Ви зареєструвалися, активуйте акаунт!");
            return "redirect:/login";
        }
    }

    @GetMapping("/activate/{code}")
    public String activateUser(
            @PathVariable(value = "code") String activationCode,
            RedirectAttributes redirectAttributes
    ) {
        boolean isActivated = userService.activateUser(activationCode);

        if (isActivated) {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Користувач успішно активований!");
        } else {
            redirectAttributes.addFlashAttribute("messageType", "danger");
            redirectAttributes.addFlashAttribute("message", "Код активації не знайдено!");
        }

        return "redirect:/login";
    }
}
