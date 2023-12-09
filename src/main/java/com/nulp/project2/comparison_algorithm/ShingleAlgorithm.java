package com.nulp.project2.comparison_algorithm;

import java.util.List;

public interface ShingleAlgorithm {
    double compareTexts(String firstText, String secondText, int shingleLen);
    List<String> formatTexts(String firstText, String secondText, int shingleLen);
}
