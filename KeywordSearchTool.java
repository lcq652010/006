import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordSearchTool {
    
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }
        
        String filePath = args[0];
        List<String> keywords = new ArrayList<String>();
        for (int i = 1; i < args.length; i++) {
            keywords.add(args[i]);
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("错误: 文件不存在: " + filePath);
            System.exit(1);
        }
        
        if (!isValidFileType(filePath)) {
            System.err.println("错误: 不支持的文件格式，请使用 .txt 或 .log 文件");
            System.exit(1);
        }
        
        try {
            searchKeywords(file, keywords);
        } catch (IOException e) {
            System.err.println("错误: 读取文件时发生异常: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static boolean isValidFileType(String filePath) {
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".txt") || lowerPath.endsWith(".log");
    }
    
    private static void searchKeywords(File file, List<String> keywords) throws IOException {
        Map<String, Integer> keywordCounts = new HashMap<String, Integer>();
        Map<String, List<Integer>> keywordLineNumbers = new HashMap<String, List<Integer>>();
        
        for (String keyword : keywords) {
            keywordCounts.put(keyword, 0);
            keywordLineNumbers.put(keyword, new ArrayList<Integer>());
        }
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                for (String keyword : keywords) {
                    int count = countKeywordOccurrences(line, keyword);
                    if (count > 0) {
                        int currentCount = keywordCounts.get(keyword);
                        keywordCounts.put(keyword, currentCount + count);
                        
                        List<Integer> lines = keywordLineNumbers.get(keyword);
                        if (!lines.contains(lineNumber)) {
                            lines.add(lineNumber);
                        }
                    }
                }
            }
            
            printResults(keywordCounts, keywordLineNumbers);
            
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
    
    private static int countKeywordOccurrences(String line, String keyword) {
        int count = 0;
        int index = 0;
        while ((index = line.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }
    
    private static void printResults(Map<String, Integer> keywordCounts, 
                                      Map<String, List<Integer>> keywordLineNumbers) {
        System.out.println("========================================");
        System.out.println("关键词检索结果");
        System.out.println("========================================");
        
        for (Map.Entry<String, Integer> entry : keywordCounts.entrySet()) {
            String keyword = entry.getKey();
            int count = entry.getValue();
            List<Integer> lines = keywordLineNumbers.get(keyword);
            
            System.out.println("\n关键词: \"" + keyword + "\"");
            System.out.println("  出现次数: " + count);
            if (lines.isEmpty()) {
                System.out.println("  包含该关键词的行: 无");
            } else {
                System.out.print("  包含该关键词的行号: ");
                for (int i = 0; i < lines.size(); i++) {
                    if (i > 0) {
                        System.out.print(", ");
                    }
                    System.out.print(lines.get(i));
                }
                System.out.println();
            }
        }
        
        System.out.println("\n========================================");
    }
    
    private static void printUsage() {
        System.out.println("========================================");
        System.out.println("文本文件关键词检索工具 - Java8");
        System.out.println("========================================");
        System.out.println("\n用法:");
        System.out.println("  java KeywordSearchTool <文件路径> <关键词1> [关键词2] [关键词3] ...");
        System.out.println("\n说明:");
        System.out.println("  - 支持 .txt 和 .log 格式的文本文件");
        System.out.println("  - 可指定单个或多个关键词进行检索");
        System.out.println("  - 统计每个关键词出现的总次数");
        System.out.println("  - 列出包含关键词的所有行号");
        System.out.println("\n示例:");
        System.out.println("  java KeywordSearchTool test.txt error");
        System.out.println("  java KeywordSearchTool app.log error warning info");
        System.out.println();
    }
}