package com.keywordsearch.service;

public class KeywordMatcher {
    private final boolean caseSensitive;

    public KeywordMatcher() {
        this(false);
    }

    public KeywordMatcher(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public int countOccurrences(String line, String keyword) {
        if (line == null || keyword == null || keyword.isEmpty()) {
            return 0;
        }

        String searchLine = caseSensitive ? line : line.toLowerCase();
        String searchKeyword = caseSensitive ? keyword : keyword.toLowerCase();

        int count = 0;
        int index = 0;
        while ((index = searchLine.indexOf(searchKeyword, index)) != -1) {
            count++;
            index += searchKeyword.length();
        }
        return count;
    }

    public boolean matches(String line, String keyword) {
        return countOccurrences(line, keyword) > 0;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }
}
