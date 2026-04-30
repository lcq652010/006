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
import com.keywordsearch.util.Logger;
import com.keywordsearch.util.LoggerFactory;

/**
 * 文件搜索服务类
 * 
 * <p>该类是关键词检索的核心服务，负责：
 * <ul>
 *   <li>读取文本文件内容</li>
 *   <li>逐行搜索关键词</li>
 *   <li>收集和汇总搜索结果</li>
 *   <li>管理文件资源的打开和关闭</li>
 *   <li>记录详细的执行日志</li>
 * </ul>
 * 
 * <p>设计特点：
 * <ul>
 *   <li>支持依赖注入 KeywordMatcher，便于测试和扩展</li>
 *   <li>使用 LinkedHashMap 保持关键词的原始顺序</li>
 *   <li>使用 try-finally 确保文件资源被正确关闭</li>
 *   <li>提供重载方法，支持 File 对象和文件路径字符串</li>
 *   <li>集成日志系统，追踪执行过程和异常</li>
 * </ul>
 */
public class FileSearcher {
    
    /** 日志实例 */
    private static final Logger logger = LoggerFactory.getLogger(FileSearcher.class);
    
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
        logger.debug("创建 FileSearcher 实例，关键词匹配器: {}, 大小写敏感: {}", 
            keywordMatcher.getClass().getSimpleName(), keywordMatcher.isCaseSensitive());
    }

    /**
     * 在文件中搜索多个关键词
     * 
     * <p>该方法会：
     * <ol>
     *   <li>验证输入参数</li>
     *   <li>初始化搜索结果集合</li>
     *   <li>打开文件并创建读取器</li>
     *   <li>逐行读取文件内容</li>
     *   <li>对每一行进行所有关键词的匹配</li>
     *   <li>更新每个关键词的搜索结果（出现次数和行号）</li>
     *   <li>确保文件资源被正确关闭</li>
     *   <li>记录执行过程的详细日志</li>
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
        // 验证输入参数
        if (file == null) {
            logger.warn("搜索文件为 null，返回空结果");
            return new LinkedHashMap<String, SearchResult>();
        }
        if (keywords == null || keywords.isEmpty()) {
            logger.warn("关键词列表为空，返回空结果");
            return new LinkedHashMap<String, SearchResult>();
        }

        logger.info("开始搜索文件: {}, 关键词数量: {}", file.getAbsolutePath(), keywords.size());
        logger.debug("关键词列表: {}", keywords);

        // 初始化搜索结果，使用 LinkedHashMap 保持关键词顺序
        Map<String, SearchResult> results = initResults(keywords);

        BufferedReader reader = null;
        int totalLines = 0;
        int matchedLines = 0;
        
        try {
            logger.debug("打开文件: {}", file.getAbsolutePath());
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;

            // 逐行读取文件
            logger.debug("开始逐行读取文件...");
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                totalLines++;
                
                if (logger.isDebugEnabled()) {
                    // 只在 DEBUG 级别记录行号，避免大量日志
                    if (lineNumber % 100 == 0) {
                        logger.debug("已读取 {} 行...", lineNumber);
                    }
                }
                
                // 处理当前行，更新所有关键词的搜索结果
                boolean lineHasMatch = processLine(line, lineNumber, results);
                if (lineHasMatch) {
                    matchedLines++;
                }
            }
            
            logger.info("文件读取完成，总行数: {}, 匹配行数: {}", totalLines, matchedLines);
            
        } catch (IOException e) {
            logger.error("读取文件时发生IO异常: {}", file.getAbsolutePath(), e);
            throw e;
        } finally {
            // 确保文件资源被关闭
            if (reader != null) {
                try {
                    logger.debug("关闭文件: {}", file.getAbsolutePath());
                    reader.close();
                } catch (IOException e) {
                    logger.warn("关闭文件时发生异常: {}", e.getMessage());
                    // 忽略关闭时的异常
                }
            }
        }

        // 统计结果
        int keywordsFound = 0;
        int totalMatches = 0;
        for (SearchResult result : results.values()) {
            if (result.isFound()) {
                keywordsFound++;
                totalMatches += result.getCount();
            }
        }
        logger.info("搜索完成，找到 {} 个关键词，共 {} 次匹配", keywordsFound, totalMatches);

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
        logger.debug("使用路径搜索: {}", filePath);
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
        logger.debug("初始化搜索结果，关键词数量: {}", keywords.size());
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
     * <p>在 DEBUG 级别会记录匹配到的关键词和行号。
     * 
     * @param line 当前行的文本内容
     * @param lineNumber 当前行的行号（从1开始）
     * @param results 搜索结果映射，会被修改
     * @return 如果当前行匹配到至少一个关键词则返回 true
     */
    private boolean processLine(String line, int lineNumber, Map<String, SearchResult> results) {
        boolean hasMatch = false;
        
        for (Map.Entry<String, SearchResult> entry : results.entrySet()) {
            String keyword = entry.getKey();
            SearchResult result = entry.getValue();

            // 统计关键词在当前行中出现的次数
            int count = keywordMatcher.countOccurrences(line, keyword);
            if (count > 0) {
                hasMatch = true;
                // 更新出现次数
                result.incrementCount(count);
                // 记录行号
                result.addLineNumber(lineNumber);
                
                if (logger.isDebugEnabled()) {
                    // 记录匹配信息（截断长行，避免日志过大）
                    String preview = line.length() > 50 ? line.substring(0, 50) + "..." : line;
                    logger.debug("行 {} 匹配到关键词 \"{}\" ({}次): {}", lineNumber, keyword, count, preview);
                }
            }
        }
        
        return hasMatch;
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
