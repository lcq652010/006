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

/**
 * 主程序入口类
 * 
 * <p>该类是关键词检索工具的入口点，负责：
 * <ul>
 *   <li>解析命令行参数</li>
 *   <li>验证文件有效性</li>
 *   <li>协调各模块完成搜索任务</li>
 *   <li>处理异常和错误</li>
 * </ul>
 * 
 * <p>设计特点：
 * <ul>
 *   <li>使用依赖注入，支持灵活配置</li>
 *   <li>清晰的职责划分，便于维护和测试</li>
 *   <li>完善的错误处理机制</li>
 *   <li>支持大小写敏感和不敏感两种模式</li>
 * </ul>
 * 
 * <h2>使用方法</h2>
 * <pre>
 * // 默认模式（大小写不敏感）
 * java com.keywordsearch.Main <文件路径> <关键词1> [关键词2] ...
 * 
 * // 带空格的关键词需要用引号包裹
 * java com.keywordsearch.Main test.txt "error code"
 * 
 * // 查看帮助
 * java com.keywordsearch.Main
 * </pre>
 */
public class Main {
    
    /** 文件搜索服务 */
    private final FileSearcher fileSearcher;

    /**
     * 默认构造函数
     * 
     * <p>使用默认配置创建主程序实例：
     * <ul>
     *   <li>大小写不敏感匹配</li>
     *   <li>默认的关键词匹配器</li>
     * </ul>
     */
    public Main() {
        this(false);
    }

    /**
     * 构造函数（指定大小写敏感模式）
     * 
     * <p>根据指定的大小写敏感模式创建主程序实例
     * 
     * @param caseSensitive 是否大小写敏感
     */
    public Main(boolean caseSensitive) {
        KeywordMatcher matcher = new KeywordMatcher(caseSensitive);
        this.fileSearcher = new FileSearcher(matcher);
    }

    /**
     * 构造函数（依赖注入）
     * 
     * <p>使用依赖注入方式创建主程序实例，便于测试和扩展。
     * 可以注入自定义的 FileSearcher 实现。
     * 
     * @param fileSearcher 文件搜索服务实例
     */
    public Main(FileSearcher fileSearcher) {
        this.fileSearcher = fileSearcher;
    }

    /**
     * 主方法 - 程序入口点
     * 
     * <p>执行流程：
     * <ol>
     *   <li>检查命令行参数数量，如果不足则显示使用说明</li>
     *   <li>解析文件路径和关键词列表</li>
     *   <li>验证文件有效性（存在性、格式）</li>
     *   <li>创建主程序实例并执行搜索</li>
     *   <li>处理可能的异常并输出错误信息</li>
     * </ol>
     * 
     * <p>命令行参数格式：
     * <pre>
     * args[0] - 文件路径
     * args[1..n] - 关键词列表
     * </pre>
     * 
     * <p>退出码：
     * <ul>
     *   <li>0 - 正常执行完成</li>
     *   <li>1 - 参数不足或发生错误</li>
     * </ul>
     * 
     * @param args 命令行参数数组
     */
    public static void main(String[] args) {
        // 检查参数数量
        if (args.length < 2) {
            ConsolePrinter.printUsage();
            System.exit(1);
        }

        // 解析文件路径和关键词
        String filePath = args[0];
        List<String> keywords = parseKeywords(args);

        // 验证文件有效性
        ValidationResult validation = FileValidator.validate(filePath);
        if (!validation.isValid()) {
            ConsolePrinter.printError(validation.getErrorMessage());
            System.exit(1);
        }

        // 执行搜索
        Main app = new Main();
        try {
            app.run(filePath, keywords);
        } catch (Exception e) {
            ConsolePrinter.printError("执行过程中发生异常: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 解析命令行参数中的关键词
     * 
     * <p>从命令行参数数组中提取关键词，跳过第一个参数（文件路径）。
     * 会过滤掉 null 和空字符串的参数。
     * 
     * <p>注意：带空格的关键词需要在命令行中用双引号包裹，
     * 否则会被解析为多个独立的关键词。
     * 
     * @param args 命令行参数数组
     * @return 关键词列表
     */
    private static List<String> parseKeywords(String[] args) {
        List<String> keywords = new ArrayList<String>();
        // 从索引1开始，跳过文件路径
        for (int i = 1; i < args.length; i++) {
            if (args[i] != null && !args[i].trim().isEmpty()) {
                keywords.add(args[i]);
            }
        }
        return keywords;
    }

    /**
     * 执行搜索任务
     * 
     * <p>执行完整的搜索流程：
     * <ol>
     *   <li>检查关键词列表是否为空</li>
     *   <li>输出搜索开始前的信息（目标文件、关键词列表）</li>
     *   <li>调用文件搜索服务执行搜索</li>
     *   <li>输出搜索结果</li>
     * </ol>
     * 
     * @param filePath 目标文件路径
     * @param keywords 要搜索的关键词列表
     * @throws Exception 如果搜索过程中发生错误（如IO异常）
     * @throws IllegalArgumentException 如果关键词列表为空
     */
    public void run(String filePath, List<String> keywords) throws Exception {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("关键词列表不能为空");
        }

        // 输出搜索信息，帮助用户确认参数是否正确解析
        ConsolePrinter.printSearchInfo(filePath, keywords);

        // 执行搜索并输出结果
        Map<String, SearchResult> results = fileSearcher.search(filePath, keywords);
        ConsolePrinter.printResults(results);
    }

    /**
     * 获取文件搜索服务
     * 
     * @return 当前使用的文件搜索服务实例
     */
    public FileSearcher getFileSearcher() {
        return fileSearcher;
    }
}
