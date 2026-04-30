package com.keywordsearch.ui;

import java.util.List;
import java.util.Map;

import com.keywordsearch.model.SearchResult;

public class ConsolePrinter {
    private static final String SEPARATOR = "========================================";

    private ConsolePrinter() {
    }

    public static void printResults(Map<String, SearchResult> results) {
        System.out.println(SEPARATOR);
        System.out.println("关键词检索结果");
        System.out.println(SEPARATOR);

        for (Map.Entry<String, SearchResult> entry : results.entrySet()) {
            SearchResult result = entry.getValue();
            printSingleResult(result);
        }

        System.out.println();
        System.out.println(SEPARATOR);
    }

    public static void printSingleResult(SearchResult result) {
        System.out.println();
        System.out.println("关键词: \"" + result.getKeyword() + "\"");
        System.out.println("  出现次数: " + result.getCount());

        List<Integer> lineNumbers = result.getLineNumbers();
        if (lineNumbers.isEmpty()) {
            System.out.println("  包含该关键词的行: 无");
        } else {
            System.out.print("  包含该关键词的行号: ");
            printLineNumbers(lineNumbers);
            System.out.println();
        }
    }

    private static void printLineNumbers(List<Integer> lineNumbers) {
        for (int i = 0; i < lineNumbers.size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(lineNumbers.get(i));
        }
    }

    public static void printUsage() {
        System.out.println(SEPARATOR);
        System.out.println("文本文件关键词检索工具 - Java8");
        System.out.println(SEPARATOR);
        System.out.println();
        System.out.println("用法:");
        System.out.println("  java com.keywordsearch.Main <文件路径> <关键词1> [关键词2] [关键词3] ...");
        System.out.println();
        System.out.println("说明:");
        System.out.println("  - 支持 .txt 和 .log 格式的文本文件");
        System.out.println("  - 可指定单个或多个关键词进行检索");
        System.out.println("  - 统计每个关键词出现的总次数");
        System.out.println("  - 列出包含关键词的所有行号");
        System.out.println();
        System.out.println("重要提示:");
        System.out.println("  - 如果关键词包含空格，请用双引号将其包裹");
        System.out.println("  - 特殊字符（如 @、#、$、% 等）会被正确处理");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java com.keywordsearch.Main test.txt error");
        System.out.println("  java com.keywordsearch.Main app.log error warning info");
        System.out.println("  java com.keywordsearch.Main special_test.txt \"error code\"");
        System.out.println("  java com.keywordsearch.Main special_test.txt user@123 \"error code\"");
        System.out.println();
    }

    public static void printSearchInfo(String filePath, List<String> keywords) {
        System.out.println(SEPARATOR);
        System.out.println("开始检索");
        System.out.println(SEPARATOR);
        System.out.println("目标文件: " + filePath);
        System.out.println("检索关键词:");
        for (int i = 0; i < keywords.size(); i++) {
            System.out.println("  " + (i + 1) + ". \"" + keywords.get(i) + "\"");
        }
        System.out.println();
    }

    public static void printError(String message) {
        System.err.println("错误: " + message);
    }
}
