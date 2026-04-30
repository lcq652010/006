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

/**
 * 文件搜索服务类
 * 
 * <p>该类是关键词检索的核心服务，负责：
 * <ul>
 *   <li>读取文本文件内容</li>
 *   <li>逐行搜索关键词</li>
 *   <li>收集和汇总搜索结果</li>
 *   <li>管理文件资源的打开和关闭</li>
 * </ul>
 * 
 * <p>设计特点：
 * <ul>
 *   <li>支持依赖注入 KeywordMatcher，便于测试和扩展</li>
 *   <li>使用 LinkedHashMap 保持关键词的原始顺序</li>
 *   <li>使用 try-finally 确保文件资源被正确关闭</li>
 *   <li>提供重载方法，支持 File 对象和文件路径字符串</li>
 * </ul>
 */
public class FileSearcher {
    
    /** 关键词匹配器 */
    private final KeywordMatcher keywordMatcher;

    /**
     * 默认构造函数
     * 
     * <p>使用默认的关键词匹配器（大小写不敏感）
     */
    public FileSearcher() {
        this(new KeywordMatcher());
    }

    /**
     * 构造函数
     * 
     * <p>使用指定的关键词匹配器创建文件搜索服务
     * 
     * @param keywordMatcher 关键词匹配器，支持依赖注入
     */
    public FileSearcher(KeywordMatcher keywordMatcher) {
        this.keywordMatcher = keywordMatcher;
    }

    /**
     * 在文件中搜索多个关键词
     * 
     * <p>该方法会：
     * <ol>
     *   <li>初始化搜索结果集合</li>
     *   <li>逐行读取文件内容</li>
     *   <li>对每一行进行所有关键词的匹配</li>
     *   <li>更新每个关键词的搜索结果（出现次数和行号）</li>
     *   <li>确保文件资源被正确关闭</li>
     * </ol>
     * 
     * <p>注意事项：
     * <ul>
     *   <li>行号从1开始计数</li>
     *   <li>同一行中关键词多次出现会被分别计数</li>
     *   <li>同一行中包含关键词只会记录一次行号</li>
     *   <li>使用 LinkedHashMap 保持关键词的原始顺序</li>
     * </ul>
     * 
     * @param file 要搜索的文件对象
     * @param keywords 要搜索的关键词列表
     * @return 搜索结果映射，键为关键词，值为对应的搜索结果
     * @throws IOException 如果读取文件时发生IO错误
     */
    public Map<String, SearchResult> search(File file, List<String> keywords) throws IOException {
        if (file == null || keywords == null || keywords.isEmpty()) {
            return new LinkedHashMap<String, SearchResult>();
        }

        // 初始化搜索结果，使用 LinkedHashMap 保持关键词顺序
        Map<String, SearchResult> results = initResults(keywords);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;

            // 逐行读取文件
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // 处理当前行，更新所有关键词的搜索结果
                processLine(line, lineNumber, results);
            }
        } finally {
            // 确保文件资源被关闭
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // 忽略关闭时的异常
                }
            }
        }

        return results;
    }

    /**
     * 在文件中搜索多个关键词（便捷方法）
     * 
     * <p>根据文件路径字符串创建 File 对象，然后调用 search(File, List) 方法
     * 
     * @param filePath 文件路径字符串
     * @param keywords 要搜索的关键词列表
     * @return 搜索结果映射
     * @throws IOException 如果读取文件时发生IO错误
     * @see #search(File, List)
     */
    public Map<String, SearchResult> search(String filePath, List<String> keywords) throws IOException {
        return search(new File(filePath), keywords);
    }

    /**
     * 初始化搜索结果集合
     * 
     * <p>为每个关键词创建一个空的 SearchResult 对象，并存储在 LinkedHashMap 中
     * 
     * @param keywords 关键词列表
     * @return 初始化后的搜索结果映射
     */
    private Map<String, SearchResult> initResults(List<String> keywords) {
        Map<String, SearchResult> results = new LinkedHashMap<String, SearchResult>();
        for (String keyword : keywords) {
            results.put(keyword, new SearchResult(keyword));
        }
        return results;
    }

    /**
     * 处理单行文本
     * 
     * <p>对当前行进行所有关键词的匹配，并更新对应的搜索结果：
     * <ul>
     *   <li>如果关键词在该行中出现，增加出现次数</li>
     *   <li>记录包含该关键词的行号</li>
     * </ul>
     * 
     * @param line 当前行的文本内容
     * @param lineNumber 当前行的行号（从1开始）
     * @param results 搜索结果映射，会被修改
     */
    private void processLine(String line, int lineNumber, Map<String, SearchResult> results) {
        for (Map.Entry<String, SearchResult> entry : results.entrySet()) {
            String keyword = entry.getKey();
            SearchResult result = entry.getValue();

            // 统计关键词在当前行中出现的次数
            int count = keywordMatcher.countOccurrences(line, keyword);
            if (count > 0) {
                // 更新出现次数
                result.incrementCount(count);
                // 记录行号
                result.addLineNumber(lineNumber);
            }
        }
    }

    /**
     * 获取关键词匹配器
     * 
     * @return 当前使用的关键词匹配器
     */
    public KeywordMatcher getKeywordMatcher() {
        return keywordMatcher;
    }
}
