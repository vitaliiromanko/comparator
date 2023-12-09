package com.nulp.project2.comparison_algorithm;

import org.apache.commons.math3.util.Precision;

import java.util.*;

public class ShingleAlgorithmImpl implements ShingleAlgorithm {
    private static final String[] STOP_SYMBOLS = {".", ",", "!", "?", ":", ";", "-", "\\", "/", "*", "(", ")", "+", "@",
            "#", "$", "%", "^", "&", "=", "'", "\"", "[", "]", "{", "}", "|"};
    private static final String[] STOP_WORDS_UA = {"а", "аби", "або", "ага", "аж", "ай", "але", "ау", "ах", "б", "ба",
            "би", "в", "ви", "во", "г", "га", "ги", "д", "да", "е", "еге", "ей", "ж", "же", "з", "за", "й", "на", "не",
            "но", "ну", "от", "ох", "та", "це", "я", "і"};

    private static String canonize(String str) {
        for (String stopSymbol : STOP_SYMBOLS) {
            str = str.replace(stopSymbol, "");
        }
        for (String stopWord : STOP_WORDS_UA) {
            str = str.replace(" " + stopWord + " ", " ");
        }
        return str;
    }

    private static Set<Integer> genShingle(String strNew, int shingleLen) {
        Set<Integer> shingles = new LinkedHashSet<>();
        String str = canonize(strNew.toLowerCase());
        String[] words = str.split(" ");
        int shinglesNumber = words.length - shingleLen;

        for (int i = 0; i <= shinglesNumber; i++) {
            StringBuilder shingle = new StringBuilder();

            for (int j = 0; j < shingleLen; j++) {
                shingle.append(words[i + j]).append(" ");
            }
            shingles.add(shingle.toString().hashCode());
        }
        return shingles;
    }

    @Override
    public double compareTexts(String firstText, String secondText, int shingleLen) {
        Set<Integer> firstTextShingles = genShingle(firstText, shingleLen);
        Set<Integer> secondTextShingles = genShingle(secondText, shingleLen);

        if (firstTextShingles.isEmpty() || secondTextShingles.isEmpty()) {
            return 0.0;
        }


        Set<Integer> uniqueShingles = new LinkedHashSet<>(firstTextShingles);
        uniqueShingles.addAll(secondTextShingles);
        double shinglesNumber = uniqueShingles.size();

        double similarShinglesNumber = 0;

        for (Integer firstTextShingle : firstTextShingles) {
            for (Integer secondTextShingle : secondTextShingles) {
                if (firstTextShingle.equals(secondTextShingle)) {
                    similarShinglesNumber++;
                }
            }
        }

        double result = (similarShinglesNumber / shinglesNumber) * 100;
        return Precision.round(result, 2);
    }

    private static List<String> genShingleWithoutCanonize(String str, int shingleLen) {
        List<String> shingles = new ArrayList<>();
        String[] words = str.split(" ");
        int shinglesNumber = words.length - shingleLen;

        for (int i = 0; i <= shinglesNumber; i++) {
            StringBuilder shingle = new StringBuilder();

            for (int j = 0; j < shingleLen; j++) {
                shingle.append(words[i + j]).append(" ");
            }
            shingles.add(shingle.toString());
        }
        return shingles;
    }

    @Override
    public List<String> formatTexts(String firstText, String secondText, int shingleLen) {
        List<String> stringFirstTextShingles = genShingleWithoutCanonize(firstText, shingleLen);
        List<String> stringSecondTextShingles = genShingleWithoutCanonize(secondText, shingleLen);

        if (stringFirstTextShingles.isEmpty() || stringSecondTextShingles.isEmpty()) {
            return List.of(firstText, secondText);
        }

        List<Integer> firstTextShingles = new ArrayList<>();
        for (String shingle : stringFirstTextShingles) {
            firstTextShingles.add(shingle.hashCode());
        }

        List<Integer> secondTextShingles = new ArrayList<>();
        for (String shingle : stringSecondTextShingles) {
            secondTextShingles.add(shingle.hashCode());
        }

        List<String> similarShinglesForFirstText = getSimilarShinglesForText(
                firstTextShingles,
                secondTextShingles,
                stringFirstTextShingles
        );
        List<String> similarShinglesForSecondText = getSimilarShinglesForText(
                secondTextShingles,
                firstTextShingles,
                stringSecondTextShingles
        );

        return List.of(
                formatSimilarText(firstText, similarShinglesForFirstText, shingleLen),
                formatSimilarText(secondText, similarShinglesForSecondText, shingleLen)
        );
    }

    private static List<String> getSimilarShinglesForText(List<Integer> generalListOfShingles, List<Integer> comparableListOfShingles, List<String> stringGeneralListOfShingles) {
        Map<Integer, String> textShinglesSimilar = new HashMap<>();
        for (int i = 0; i < generalListOfShingles.size(); i++) {
            for (Integer comparableShingle : comparableListOfShingles) {
                if (generalListOfShingles.get(i).equals(comparableShingle)) {
                    textShinglesSimilar.put(i, stringGeneralListOfShingles.get(i));
                }
            }
        }
        return new ArrayList<>(textShinglesSimilar.values());
    }

    private static String formatSimilarText(String text, List<String> shingles, int shingleLen) {
        text = " " + text + " ";

        if (shingleLen == 1) {
            Set<String> shingleSet = new LinkedHashSet<>(shingles);
            for (String shingle : shingleSet) {
                text = text.replace(" " + shingle, " <span style=\"background:yellow;\">" + shingle.trim() + "</span> ");
            }
        } else {
            List<String> shingleList = new ArrayList<>();
            String secondWordInPreviousShingle = "";

            for (int i = 0; i < shingles.size(); i++) {
                if (i == 0) {
                    shingleList.add(shingles.get(i));
                    secondWordInPreviousShingle = shingles.get(i).split(" ")[1];
                } else {
                    String[] wordsCurrentShingle = shingles.get(i).split(" ");
                    if (secondWordInPreviousShingle.equals(wordsCurrentShingle[0])) {
                        shingleList.set(
                                shingleList.size() - 1,
                                shingleList.get(shingleList.size() - 1) + wordsCurrentShingle[wordsCurrentShingle.length - 1] + " "
                        );
                    } else {
                        shingleList.add(shingles.get(i));
                    }
                    secondWordInPreviousShingle = wordsCurrentShingle[1];
                }
            }

            Set<String> shingleSet = new LinkedHashSet<>(shingleList);
            for (String shingle : shingleSet) {
                text = text.replace(" " + shingle, " <span style=\"background:yellow;\">" + shingle.trim() + "</span> ");
            }
        }

        return text.trim();
    }
}
