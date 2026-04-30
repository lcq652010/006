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
import com.keywordsearch.util.Logger;
import com.keywordsearch.util.Logger.Level;
import com.keywordsearch.util.LoggerFactory;
import com.keywordsearch.util.LoggerFactory.LogType;

/**
 * 主程序入口类
 * 
 * <p>该类是关键词检索工具的入口点，负责：
 * <ul>
 *   <li>解析命令行参数（包括日志配置参数）</li>
 *   <li>初始化日志系统</li>
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
 *   <li>集成日志系统，支持多种日志级别和输出方式</li>
 * </ul>
 * 
 * <h2>命令行参数格式</h2>
 * <pre>
 * java com.keywordsearch.Main [选项] <文件路径> <关键词1> [关键词2] ...
 * </pre>
 * 
 * <h2>可用选项</h2>
 * <ul>
 *   <li>--log-level=DEBUG|INFO|WARN|ERROR - 设置日志级别（默认 INFO）</li>
 *   <li>--log-type=CONSOLE|FILE - 设置日志输出类型（默认 CONSOLE）</li>
 *   <li>--log-file=<路径> - 设置日志文件路径（默认 keywordsearch.log）</li>
 *   <li>--case-sensitive - 启用大小写敏感匹配</li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>
 * // 默认模式（大小写不敏感，控制台日志）
 * java com.keywordsearch.Main test.txt error warning
 * 
 * // 带空格的关键词需要用引号包裹
 * java com.keywordsearch.Main test.txt "error code"
 * 
 * // 启用 DEBUG 级别日志
 * java com.keywordsearch.Main --log-level=DEBUG test.txt error
 * 
 * // 使用文件日志
 * java com.keywordsearch.Main --log-type=FILE --log-file=app.log test.txt error
 * 
 * // 大小写敏感匹配
 * java com.keywordsearch.Main --case-sensitive test.txt ERROR
 * 
 * // 组合使用
 * java com.keywordsearch.Main --log-level=DEBUG --log-type=FILE test.txt "error code" user@123
 * </pre>
 */
public class Main {
    
    /** 日志实例 */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
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
        logger.debug("创建 Main 实例，大小写敏感: {}", caseSensitive);
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
        logger.debug("创建 Main 实例，使用注入的 FileSearcher");
    }

    /**
     * 主方法 - 程序入口点
     * 
     * <p>执行流程：
     * <ol>
     *   <li>解析命令行选项（日志配置、大小写敏感等）</li>
     *   <li>检查参数数量，如果不足则显示使用说明</li>
     *   <li>初始化日志系统</li>
     *   <li>解析文件路径和关键词列表</li>
     *   <li>验证文件有效性（存在性、格式）</li>
     *   <li>创建主程序实例并执行搜索</li>
     *   <li>处理可能的异常并输出错误信息</li>
     * </ol>
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
        logger.debug("程序启动，开始解析命令行参数");
        
        // 解析选项参数
        boolean caseSensitive = false;
        Logger.Level logLevel = Logger.Level.INFO;
        LogType logType = LogType.CONSOLE;
        String logFile = "keywordsearch.log";
        List<String> remainingArgs = new ArrayList<String>();
        
        for (String arg : args) {
            if (arg.startsWith("--")) {
                // 处理选项参数
                if (arg.equals("--case-sensitive")) {
                    caseSensitive = true;
                    logger.debug("检测到选项: --case-sensitive");
                } else if (arg.startsWith("--log-level=")) {
                    String levelStr = arg.substring("--log-level=".length()).toUpperCase();
                    try {
                        logLevel = Logger.Level.valueOf(levelStr);
                        logger.debug("检测到选项: --log-level={}", levelStr);
                    } catch (IllegalArgumentException e) {
                        logger.warn("无效的日志级别: {}, 使用默认值 INFO", levelStr);
                    }
                } else if (arg.startsWith("--log-type=")) {
                    String typeStr = arg.substring("--log-type=".length()).toUpperCase();
                    try {
                        logType = LogType.valueOf(typeStr);
                        logger.debug("检测到选项: --log-type={}", typeStr);
                    } catch (IllegalArgumentException e) {
                        logger.warn("无效的日志类型: {}, 使用默认值 CONSOLE", typeStr);
                    }
                } else if (arg.startsWith("--log-file=")) {
                    logFile = arg.substring("--log-file=".length());
                    logger.debug("检测到选项: --log-file={}", logFile);
                } else {
                    logger.warn("未知的选项: {}", arg);
                }
            } else {
                remainingArgs.add(arg);
            }
        }
        
        // 配置日志系统
        LoggerFactory.setGlobalLevel(logLevel);
        LoggerFactory.setLogType(logType);
        LoggerFactory.setLogFilePath(logFile);
        logger.info("日志系统初始化完成，级别: {}, 类型: {}, 文件: {}", logLevel, logType, logFile);
        
        // 检查剩余参数数量
        if (remainingArgs.size() < 2) {
            logger.warn("参数不足，显示使用说明");
            ConsolePrinter.printUsage();
            System.exit(1);
        }

        // 解析文件路径和关键词
        String filePath = remainingArgs.get(0);
        List<String> keywords = parseKeywords(remainingArgs);
        logger.info("解析参数完成，文件路径: {}, 关键词数量: {}", filePath, keywords.size());
        logger.debug("关键词列表: {}", keywords);

        // 验证文件有效性
        logger.debug("开始验证文件有效性: {}", filePath);
        ValidationResult validation = FileValidator.validate(filePath);
        if (!validation.isValid()) {
            logger.error("文件验证失败: {}", validation.getErrorMessage());
            ConsolePrinter.printError(validation.getErrorMessage());
            System.exit(1);
        }
        logger.info("文件验证通过: {}", filePath);

        // 执行搜索
        Main app = new Main(caseSensitive);
        try {
            logger.info("开始执行搜索任务");
            app.run(filePath, keywords);
            logger.info("搜索任务执行完成");
        } catch (Exception e) {
            logger.error("执行过程中发生异常", e);
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
    private static List<String> parseKeywords(List<String> args) {
        List<String> keywords = new ArrayList<String>();
        // 从索引1开始，跳过文件路径
        for (int i = 1; i < args.size(); i++) {
            String arg = args.get(i);
            if (arg != null && !arg.trim().isEmpty()) {
                keywords.add(arg);
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
     *   <li>统计搜索结果</li>
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
            logger.error("关键词列表为空");
            throw new IllegalArgumentException("关键词列表不能为空");
        }

        // 输出搜索信息，帮助用户确认参数是否正确解析
        ConsolePrinter.printSearchInfo(filePath, keywords);

        // 执行搜索
        logger.debug("开始搜索文件: {}", filePath);
        long startTime = System.currentTimeMillis();
        
        Map<String, SearchResult> results = fileSearcher.search(filePath, keywords);
        
        long endTime = System.currentTimeMillis();
        logger.info("搜索完成，耗时: {}ms", (endTime - startTime));
        
        // 统计结果
        int totalMatches = 0;
        int keywordsFound = 0;
        for (SearchResult result : results.values()) {
            totalMatches += result.getCount();
            if (result.isFound()) {
                keywordsFound++;
            }
        }
        logger.info("搜索统计: 找到 {} 个关键词，共 {} 次匹配", keywordsFound, totalMatches);

        // 输出结果
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
