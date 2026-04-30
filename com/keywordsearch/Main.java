package com.keywordsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.keywordsearch.model.SearchResult;
import com.keywordsearch.service.FileSearcher;
import com.keywordsearch.service.KeywordMatcher;
import com.keywordsearch.ui.ConsolePrinter;
import com.keywordsearch.util.FileValidator;
import com.keywordsearch.util.FileValidator.ValidationResult;

public class Main {
    private final FileSearcher fileSearcher;

    public Main() {
        this(false);
    }

    public Main(boolean caseSensitive) {
        KeywordMatcher matcher = new KeywordMatcher(caseSensitive);
        this.fileSearcher = new FileSearcher(matcher);
    }

    public Main(FileSearcher fileSearcher) {
        this.fileSearcher = fileSearcher;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            ConsolePrinter.printUsage();
            System.exit(1);
        }

        String filePath = args[0];
        List<String> keywords = parseKeywords(args);

        ValidationResult validation = FileValidator.validate(filePath);
        if (!validation.isValid()) {
            ConsolePrinter.printError(validation.getErrorMessage());
            System.exit(1);
        }

        Main app = new Main();
        try {
            app.run(filePath, keywords);
        } catch (Exception e) {
            ConsolePrinter.printError("执行过程中发生异常: " + e.getMessage());
            System.exit(1);
        }
    }

    private static List<String> parseKeywords(String[] args) {
        List<String> keywords = new ArrayList<String>();
        for (int i = 1; i < args.length; i++) {
            if (args[i] != null && !args[i].trim().isEmpty()) {
                keywords.add(args[i]);
            }
        }
        return keywords;
    }

    public void run(String filePath, List<String> keywords) throws Exception {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("关键词列表不能为空");
        }

        Map<String, SearchResult> results = fileSearcher.search(filePath, keywords);
        ConsolePrinter.printResults(results);
    }

    public FileSearcher getFileSearcher() {
        return fileSearcher;
    }
}
