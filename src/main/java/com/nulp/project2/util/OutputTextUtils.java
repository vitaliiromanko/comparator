package com.nulp.project2.util;

public class OutputTextUtils {
    private static String htmlToString(String str) {
        return str
                .replaceAll("&", "&amp;")
                .replaceAll("< ", "&lt;")
                .replaceAll("<", "&lt;")
                .replaceAll(" >", "&gt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;");
    }

    private static String beforeFormatText(String str) {
        str = str
                .replaceAll("\r\n", " ")
                .replaceAll(" ,", ",")
                .replaceAll(" \\.", ".")
                .replaceAll(" !", "!")
                .replaceAll(" \\?", "?")
                .replaceAll(" :", ":")
                .replaceAll(" ;", ";")
                .replaceAll("\\[ ", "[")
                .replaceAll(" ]", "]")
                .replaceAll("\\{ ", "{")
                .replaceAll(" }", "}");

        while (str.contains("  ")) {
            str = str.replaceAll(" {2}", " ");
        }
        return str.trim();
    }

    public static String validText(String str) {
        return beforeFormatText(htmlToString(str));
    }
}
