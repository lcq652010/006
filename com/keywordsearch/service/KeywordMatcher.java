package com.keywordsearch.service;

/**
 * 关键词匹配服务类
 * 
 * <p>该类提供关键词在文本行中的匹配和计数功能，支持：
 * <ul>
 *   <li>统计关键词在一行中出现的次数</li>
 *   <li>判断关键词是否在一行中出现</li>
 *   <li>支持大小写敏感和不敏感两种匹配模式</li>
 * </ul>
 * 
 * <p>匹配特点：
 * <ul>
 *   <li>使用子字符串匹配，支持包含空格和特殊字符的关键词</li>
 *   <li>同一行中多次出现的关键词会被分别计数</li>
 *   <li>默认使用大小写不敏感匹配</li>
 * </ul>
 */
public class KeywordMatcher {
    
    /** 是否大小写敏感 */
    private final boolean caseSensitive;

    /**
     * 默认构造函数
     * 
     * <p>创建大小写不敏感的关键词匹配器
     */
    public KeywordMatcher() {
        this(false);
    }

    /**
     * 构造函数
     * 
     * <p>根据指定的大小写敏感模式创建关键词匹配器
     * 
     * @param caseSensitive 是否大小写敏感
     */
    public KeywordMatcher(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * 统计关键词在一行中出现的次数
     * 
     * <p>该方法会遍历整行文本，统计关键词出现的所有位置。
     * 如果同一行中关键词多次出现，会被分别计数。
     * 
     * <p>匹配规则：
     * <ul>
     *   <li>支持包含空格的关键词（如 "error code"）</li>
     *   <li>支持包含特殊字符的关键词（如 "user@123", "test#123$%^"）</li>
     *   <li>根据 caseSensitive 配置决定是否区分大小写</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>
     * 行内容: "error code and error code"
     * 关键词: "error code"
     * 返回: 2 (出现两次)
     * </pre>
     * 
     * @param line 要搜索的文本行
     * @param keyword 要匹配的关键词
     * @return 关键词出现的次数，如果行或关键词为空则返回0
     */
    public int countOccurrences(String line, String keyword) {
        if (line == null || keyword == null || keyword.isEmpty()) {
            return 0;
        }

        // 根据大小写敏感配置决定是否转换为小写
        String searchLine = caseSensitive ? line : line.toLowerCase();
        String searchKeyword = caseSensitive ? keyword : keyword.toLowerCase();

        int count = 0;
        int index = 0;
        // 循环查找所有出现的位置
        while ((index = searchLine.indexOf(searchKeyword, index)) != -1) {
            count++;
            // 移动到关键词之后，避免重复计数
            index += searchKeyword.length();
        }
        return count;
    }

    /**
     * 判断关键词是否在一行中出现
     * 
     * <p>这是一个便捷方法，等价于 countOccurrences(line, keyword) > 0
     * 
     * @param line 要搜索的文本行
     * @param keyword 要匹配的关键词
     * @return 如果关键词出现至少一次则返回true，否则返回false
     */
    public boolean matches(String line, String keyword) {
        return countOccurrences(line, keyword) > 0;
    }

    /**
     * 检查是否大小写敏感
     * 
     * @return 如果是大小写敏感匹配则返回true，否则返回false
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }
}
