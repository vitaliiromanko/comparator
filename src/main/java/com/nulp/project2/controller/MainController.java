package com.nulp.project2.controller;

import com.nulp.project2.exeption.PdfFileIsEncryptedException;
import com.nulp.project2.model.text.HistoryFileComparison;
import com.nulp.project2.model.text.HistoryTextComparison;
import com.nulp.project2.model.user.User;
import com.nulp.project2.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;


    @GetMapping("/main/texts-comparison")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String showTextsComparisonPage(Model model) {
        model.addAttribute("selectedPage", "textsComparisonPage");
        return "textsComparisonPage";
    }

    @PostMapping("/main/texts-comparison")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String compareTexts(
            @AuthenticationPrincipal User user,
            @RequestParam String firstTextTitle,
            @RequestParam String secondTextTitle,
            @RequestParam String firstText,
            @RequestParam String secondText,
            @RequestParam int shingleLength,
            Model model
    ) {
        model.addAttribute("firstTextTitle", firstTextTitle);
        model.addAttribute("secondTextTitle", secondTextTitle);
        model.addAttribute("firstText", firstText);
        model.addAttribute("secondText", secondText);
        model.addAttribute("shingleLength", Integer.toString(shingleLength));
        model.addAttribute("selectedPage", "textsComparisonPage");

        if (mainService.isInvalidTextSize(firstText) || mainService.isInvalidTextSize(secondText)) {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Ви перевищили допустимий розмір текстів!");
            return "textsComparisonPage";
        }

        if (firstTextTitle.trim().isEmpty()) {
            firstTextTitle = "Перший текст";
        }

        if (secondTextTitle.trim().isEmpty()) {
            secondTextTitle = "Другий текст";
        }

        List<String> results = mainService.getCompareTextsResults(firstText, secondText, shingleLength);
        mainService.saveComparisonTexts(user, firstTextTitle.trim(), secondTextTitle.trim(), shingleLength, Double.parseDouble(results.get(0)));

        model.addAttribute("comparisonResult", "<strong>" + results.get(0) + "%" + "</strong>");
        model.addAttribute("resultFirstText", results.get(1));
        model.addAttribute("resultSecondText", results.get(2));
        return "textsComparisonPage";
    }

    @GetMapping("/main/texts-comparison/history")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String showTextsComparisonHistoryPage(
            @AuthenticationPrincipal User user,
            @ModelAttribute(value = "messageType") String messageType,
            @ModelAttribute(value = "message") String message,
            Model model
    ) {
        model.addAttribute("historyList", mainService.findHistoryTextsByUser(user));
        model.addAttribute("selectedPage", "textsComparisonPage");
        return "textsComparisonHistoryPage";
    }

    @GetMapping("/main/files-comparison")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String showFilesComparisonPage(Model model) {
        model.addAttribute("selectedPage", "filesComparisonPage");
        return "filesComparisonPage";
    }

    @PostMapping("/main/files-comparison")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String compareFiles(
            @AuthenticationPrincipal User user,
            @RequestPart(required = false) MultipartFile[] files,
            @RequestParam int shingleLength,
            Model model
    ) {
        model.addAttribute("selectedPage", "filesComparisonPage");
        model.addAttribute("shingleLength", Integer.toString(shingleLength));

        if (files == null || files.length <= 1) {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Ви вказали для порівняння менше, ніж 2 файли!");
            return "filesComparisonPage";
        }
        if (!mainService.isValidFilesExtension(files)) {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Ви завантажили файл некоректного формату!");
            return "filesComparisonPage";
        }
        if (!mainService.isValidFilesSize(files)) {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Сумарний розмір завантажених файлів перевищує допустиме значення в 30МБ!");
            return "filesComparisonPage";
        }

        List<String> comparisonResults;
        try {
            comparisonResults = new ArrayList<>(mainService.getCompareFilesResults(files, shingleLength));
        } catch (IOException e) {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Помилка під час зчитування даних з файлів!");
            return "filesComparisonPage";
        } catch (PdfFileIsEncryptedException e) {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Вказаний PDF-файл зашифрований!");
            return "filesComparisonPage";
        }

        List<String> filenames = Arrays.stream(files)
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toList());

        model.addAttribute("results", mainService.formFilesComparisonResultList(
                mainService.saveComparisonFiles(
                        user,
                        filenames,
                        shingleLength,
                        comparisonResults.stream()
                                .map(Double::parseDouble)
                                .collect(Collectors.toList())
                ).getFileComparisonResults()
        ));
        return "filesComparisonPage";
    }

    @GetMapping("/main/files-comparison/history")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String showFilesComparisonHistoryPage(
            @AuthenticationPrincipal User user,
            @ModelAttribute(value = "messageType") String messageType,
            @ModelAttribute(value = "message") String message,
            Model model
    ) {
        model.addAttribute("historyList", mainService.findHistoryFilesByUser(user));
        model.addAttribute("selectedPage", "filesComparisonPage");
        return "filesComparisonHistoryPage";
    }

    @PostMapping("/main/texts/{history}/sendEmail")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String sendHistoryTextsToUserEmail(
            @AuthenticationPrincipal User user,
            @PathVariable("history") HistoryTextComparison historyTextComparison,
            RedirectAttributes redirectAttributes
    ) {
        mainService.sendTextsHistoryToUser(user, historyTextComparison);
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Повідомлення надіслано!");
        return "redirect:/main/texts-comparison/history";
    }

    @PostMapping("/main/texts/{history}/downloadFile")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public void downloadHistoryTextsFileByUser(
            @PathVariable("history") HistoryTextComparison historyTextComparison,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename*=UTF-8''%d0%9f%d0%be%d1%80%d1%96%d0%b2%d0%bd%d1%8f%d0%bd%d0%bd%d1%8f%5f%d1%82%d0%b5%d0%ba%d1%81%d1%82%d1%96%d0%b2%5f" + historyTextComparison.getId() + ".pdf";
        response.setHeader(headerKey, headerValue);
        mainService.downloadHistoryTextsFileByUser(historyTextComparison, response);
    }

    @PostMapping("/main/files/{history}/sendEmail")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public String sendHistoryFilesToUserEmail(
            @AuthenticationPrincipal User user,
            @PathVariable("history") HistoryFileComparison historyFileComparison,
            RedirectAttributes redirectAttributes
    ) {
        mainService.sendFilesHistoryToUser(user, historyFileComparison);
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Повідомлення надіслано!");
        return "redirect:/main/files-comparison/history";
    }

    @PostMapping("/main/files/{history}/downloadFile")
    @PreAuthorize("hasAuthority('use_basic_functions')")
    public void downloadHistoryFilesFileByUser(
            @PathVariable("history") HistoryFileComparison historyFileComparison,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename*=UTF-8''%d0%9f%d0%be%d1%80%d1%96%d0%b2%d0%bd%d1%8f%d0%bd%d0%bd%d1%8f%5f%d1%84%d0%b0%d0%b9%d0%bb%d1%96%d0%b2%5f" + historyFileComparison.getId() + ".pdf";
        response.setHeader(headerKey, headerValue);
        mainService.downloadHistoryFilesFileByUser(historyFileComparison, response);
    }

    @GetMapping("/feedback")
    public String showFeedbackPage(
            @ModelAttribute(value = "messageType") String messageType,
            @ModelAttribute(value = "message") String message,
            Model model
    ) {
        model.addAttribute("selectedPage", "feedbackPage");
        return "feedbackPage";
    }

    @PostMapping("/feedback")
    public String sendMessageToUsFromUser(
            @RequestParam(value = "firstName") String userFirstName,
            @RequestParam(value = "lastName") String userLastName,
            @RequestParam(value = "sendMessageBody") String userMessage,
            RedirectAttributes redirectAttributes
    ) {
        mainService.sendUserMessage(userFirstName, userLastName, userMessage);
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Повідомлення надіслано!");
        return "redirect:/feedback";
    }
}
