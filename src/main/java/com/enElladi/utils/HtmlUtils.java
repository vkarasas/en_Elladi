package com.enElladi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

    private static final Pattern HTTPS_URL =
            Pattern.compile("https://[^\\s<>\"']+");

    public static String linkifyHttps(String content) {

        if (content == null || content.isBlank()) {
            return "";
        }

        Matcher matcher = HTTPS_URL.matcher(content);

        StringBuilder result = new StringBuilder();

        int lastEnd = 0;

        while (matcher.find()) {

            // Normal text before URL
            String before = content.substring(lastEnd, matcher.start());

            result.append(escapeHtml(before));

            // HTTPS URL
            String url = matcher.group();

            String escapedUrl = escapeHtml(url);

            result.append("<a href=\"")
                    .append(escapedUrl)
                    .append("\" target=\"_blank\" rel=\"noopener noreferrer\">")
                    .append(escapedUrl)
                    .append("</a>");

            lastEnd = matcher.end();
        }

        // Remaining normal text
        result.append(
                escapeHtml(content.substring(lastEnd))
        );

        return result.toString();
    }

    private static String escapeHtml(String text) {

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
