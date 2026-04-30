package com.keywordsearch.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.keywordsearch.model.SearchResult;

public class FileSearcher {
    private final KeywordMatcher keywordMatcher;

    public FileSearcher() {
        this(new KeywordMatcher());
    }

    public FileSearcher(KeywordMatcher keywordMatcher) {
        this.keywordMatcher = keywordMatcher;
    }

    public Map<String, SearchResult> search(File file, List<String> keywords) throws IOException {
        if (file == null || keywords == null || keywords.isEmpty()) {
            return new LinkedHashMap<String, SearchResult>();
        }

        Map<String, SearchResult> results = initResults(keywords);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                processLine(line, lineNumber, results);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        return results;
    }

    public Map<String, SearchResult> search(String filePath, List<String> keywords) throws IOException {
        return search(new File(filePath), keywords);
    }

    private Map<String, SearchResult> initResults(List<String> keywords) {
        Map<String, SearchResult> results = new LinkedHashMap<String, SearchResult>();
        for (String keyword : keywords) {
            results.put(keyword, new SearchResult(keyword));
        }
        return results;
    }

    private void processLine(String line, int lineNumber, Map<String, SearchResult> results) {
        for (Map.Entry<String, SearchResult> entry : results.entrySet()) {
            String keyword = entry.getKey();
            SearchResult result = entry.getValue();

            int count = keywordMatcher.countOccurrences(line, keyword);
            if (count > 0) {
                result.incrementCount(count);
                result.addLineNumber(lineNumber);
            }
        }
    }

    public KeywordMatcher getKeywordMatcher() {
        return keywordMatcher;
    }
}
