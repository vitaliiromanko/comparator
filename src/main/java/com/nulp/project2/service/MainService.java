package com.nulp.project2.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.nulp.project2.comparison_algorithm.ShingleAlgorithmHelper;
import com.nulp.project2.exeption.PdfFileIsEncryptedException;
import com.nulp.project2.model.text.FileComparisonResult;
import com.nulp.project2.model.text.HistoryFileComparison;
import com.nulp.project2.model.text.HistoryTextComparison;
import com.nulp.project2.model.user.User;
import com.nulp.project2.repository.text.HistoryFileComparisonRepository;
import com.nulp.project2.repository.text.HistoryTextComparisonRepository;
import com.nulp.project2.util.OutputTextUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final SmtpMailSender mailSender;
    private final HistoryTextComparisonRepository historyTextComparisonRepository;
    private final HistoryFileComparisonRepository historyFileComparisonRepository;

    @Value("${mail-for-feedback}")
    private String mailForFeedback;


    public List<String> getCompareTextsResults(String firstText, String secondText, int shingleLength) {
        List<String> results = new ArrayList<>();

        firstText = OutputTextUtils.validText(firstText);
        secondText = OutputTextUtils.validText(secondText);

        results.add(Double.toString(
                ShingleAlgorithmHelper.getShingleAlgorithm().compareTexts(firstText, secondText, shingleLength))
        );

        List<String> formatText = ShingleAlgorithmHelper.getShingleAlgorithm().formatTexts(
                firstText,
                secondText,
                shingleLength
        );

        results.add(formatText.get(0));
        results.add(formatText.get(1));
        return results;
    }

    public boolean isInvalidTextSize(String text) {
        return text.chars().count() > 10000L;
    }

    public void saveComparisonTexts(User user, String firstTextTitle, String secondTextTitle, int shingleLength, double result) {
        HistoryTextComparison historyTextComparison = new HistoryTextComparison();
        historyTextComparison.setTitleFirstText(firstTextTitle);
        historyTextComparison.setTitleSecondText(secondTextTitle);
        historyTextComparison.setShingleLength(shingleLength);
        historyTextComparison.setComparisonResult(result);
        historyTextComparison.setUser(user);
        historyTextComparisonRepository.save(historyTextComparison);
    }

    public List<HistoryTextComparison> findHistoryTextsByUser(User user) {
        List<HistoryTextComparison> historyTextComparisons = historyTextComparisonRepository.findAllByUserId(user.getId())
                .orElse(Collections.emptyList());

        if (!historyTextComparisons.isEmpty()) {
            Collections.sort(historyTextComparisons);
            Collections.reverse(historyTextComparisons);
        }

        return historyTextComparisons;
    }

    public boolean isValidFilesExtension(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (fileExtension != null) {
                if (!fileExtension.equals("txt") && !fileExtension.equals("docx") && !fileExtension.equals("pdf")) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean isValidFilesSize(MultipartFile[] files) {
        double filesSumSize = 0.0;
        for (MultipartFile file : files) {
            filesSumSize += convertFileSizeToMb(file.getSize());
        }
        return !(filesSumSize > 30);
    }

    private Double convertFileSizeToMb(Long size) {
        return (double) size / 1024 / 1024;
    }

    public List<String> getCompareFilesResults(MultipartFile[] files, int shingleLength) throws IOException, PdfFileIsEncryptedException {
        List<String> results = new ArrayList<>();

        List<String> textFromFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                switch (Objects.requireNonNull(FilenameUtils.getExtension(file.getOriginalFilename()))) {
                    case ("txt"):
                        textFromFiles.add(OutputTextUtils.validText(getTextFromTxtFile(file)));
                        break;

                    case ("docx"):
                        textFromFiles.add(OutputTextUtils.validText(getTextFromDocxFile(file)));
                        break;

                    case ("pdf"):
                        textFromFiles.add(OutputTextUtils.validText(getTextFromPdfFile(file)));
                        break;
                }
            } else {
                textFromFiles.add("");
            }
        }

        for (int i = 0; i < textFromFiles.size() - 1; i++) {
            for (int j = i + 1; j < textFromFiles.size(); j++) {
                results.add(Double.toString(
                        ShingleAlgorithmHelper.getShingleAlgorithm().compareTexts(textFromFiles.get(i), textFromFiles.get(j), shingleLength))
                );
            }
        }

        return results;
    }

    private String getTextFromPdfFile(MultipartFile file) throws IOException, PdfFileIsEncryptedException {
        String text;
        PDDocument document = PDDocument.load(file.getInputStream());
        if (!document.isEncrypted()) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } else {
            throw new PdfFileIsEncryptedException();
        }
        document.close();
        return text;
    }

    private String getTextFromDocxFile(MultipartFile file) throws IOException {
        XWPFDocument document = new XWPFDocument(file.getInputStream());
        String text = document.getParagraphs()
                .stream()
                .map(XWPFParagraph::getText)
                .collect(Collectors.joining(" "));
        document.close();
        return text;
    }

    private String getTextFromTxtFile(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String text = reader.lines().collect(Collectors.joining(" "));
        reader.close();
        return text;
    }

    public HistoryFileComparison saveComparisonFiles(User user, List<String> filenames, int shingleLength, List<Double> comparisonResults) {
        HistoryFileComparison historyFileComparison = new HistoryFileComparison();
        historyFileComparison.setFileComparisonResults(convertToFileComparisonResults(
                historyFileComparison,
                filenames,
                comparisonResults
        ));
        historyFileComparison.setShingleLength(shingleLength);
        historyFileComparison.setUser(user);
        return historyFileComparisonRepository.save(historyFileComparison);
    }

    public List<HistoryFileComparison> findHistoryFilesByUser(User user) {
        List<HistoryFileComparison> historyFileComparisons = historyFileComparisonRepository.findAllByUserId(user.getId())
                .orElse(Collections.emptyList());

        if (!historyFileComparisons.isEmpty()) {
            Collections.sort(historyFileComparisons);
            Collections.reverse(historyFileComparisons);
        }

        return historyFileComparisons;
    }

    public void sendTextsHistoryToUser(User user, HistoryTextComparison historyTextComparison) {
        String message = String.format(
                "Послуга: Порівняння текстів\n" +
                        "Порівнювані тексти: %s ↔ %s\n" +
                        "Використана довжина шингла: %s\n" +
                        "Результат порівняння: %s\n" +
                        "Дата проведення порівнняння: %s",
                historyTextComparison.getTitleFirstText(),
                historyTextComparison.getTitleSecondText(),
                historyTextComparison.getShingleLength(),
                historyTextComparison.getComparisonResult(),
                historyTextComparison.getComparisonDate()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))

        );
        mailSender.send(user.getEmail(), "Порівняння текстів № " + historyTextComparison.getId(), message);
    }

    public void downloadHistoryTextsFileByUser(HistoryTextComparison historyTextComparison, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        Font fontNormal = new Font(Font.HELVETICA, 14, Font.NORMAL);

        document.open();

        document.add(new Paragraph(new Chunk(
                "Послуга: " + "Порівняння текстів",
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Користувач: " +
                        historyTextComparison.getUser().getUsername(),
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Порівнювані тексти: " +
                        historyTextComparison.getTitleFirstText() + " ↔ " + historyTextComparison.getTitleSecondText(),
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Використана довжина шингла: " +
                        historyTextComparison.getShingleLength(),
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Результат порівняння: " +
                        historyTextComparison.getComparisonResult(),
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Дата проведення порівнняння: " +
                        historyTextComparison.getComparisonDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                fontNormal
        )));

        document.close();
    }

    public void sendFilesHistoryToUser(User user, HistoryFileComparison historyFileComparison) {
        List<String> results = formFilesComparisonResultList(historyFileComparison.getFileComparisonResults());

        StringBuilder message = new StringBuilder();
        message.append("Послуга: Порівняння файлів\n");
        message.append("Результати порівняння вказаних файлів:\n");

        for (String result : results) {
            message.append(result).append("\n");
        }

        message.append("Використана довжина шингла: ").append(historyFileComparison.getShingleLength()).append("\n");
        message.append("Дата проведення порівнняння: ").append(
                historyFileComparison.getComparisonDate()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
        );

        mailSender.send(user.getEmail(), "Порівняння файлів № " + historyFileComparison.getId(), message.toString());
    }

    public void downloadHistoryFilesFileByUser(HistoryFileComparison historyFileComparison, HttpServletResponse response) throws IOException {
        List<String> results = formFilesComparisonResultList(historyFileComparison.getFileComparisonResults());

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        Font fontNormal = new Font(Font.HELVETICA, 14, Font.NORMAL);

        document.open();

        document.add(new Paragraph(new Chunk(
                "Послуга: " + "Порівняння файлів",
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Користувач: " +
                        historyFileComparison.getUser().getUsername(),
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Результати порівняння вказаних файлів:",
                fontNormal
        )));

        for (String result : results) {
            document.add(new Paragraph(new Chunk(result, fontNormal)));
        }

        document.add(new Paragraph(new Chunk(
                "Використана довжина шингла: " + historyFileComparison.getShingleLength(),
                fontNormal
        )));

        document.add(new Paragraph(new Chunk(
                "Дата проведення порівняння: " +
                        historyFileComparison.getComparisonDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                fontNormal
        )));

        document.close();
    }

    public List<String> formFilesComparisonResultList(Set<FileComparisonResult> fileComparisonResults) {
        List<String> results = new ArrayList<>();

        for (FileComparisonResult fileComparisonResult : fileComparisonResults) {
            results.add(fileComparisonResult.getNameFirstFile() + " ↔ " + fileComparisonResult.getNameSecondFile() +
                    " : " + fileComparisonResult.getComparisonResult() + "%");
        }

        return results;
    }

    public void sendUserMessage(String userFirstName, String userLastName, String userMessage) {
        mailSender.send(mailForFeedback, "Повідомлення від користувача " + userFirstName + " " + userLastName, userMessage);
    }

    private Set<FileComparisonResult> convertToFileComparisonResults(
            HistoryFileComparison historyFileComparison,
            List<String> filenames,
            List<Double> comparisonResults
    ) {
        Set<FileComparisonResult> fileComparisonResults = new LinkedHashSet<>();

        int k = 0;
        for (int i = 0; i < filenames.size() - 1; i++) {
            for (int j = i + 1; j < filenames.size(); j++) {
                fileComparisonResults.add(
                        FileComparisonResult.of(
                                historyFileComparison,
                                filenames.get(i),
                                filenames.get(j),
                                comparisonResults.get(k)
                        )
                );
                k++;
            }
        }

        return fileComparisonResults;
    }
}
