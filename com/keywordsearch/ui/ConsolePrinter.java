package com.keywordsearch.ui;

import java.util.List;
import java.util.Map;

import com.keywordsearch.model.SearchResult;

/**
 * 控制台输出组件类
 * 
 * <p>该类负责将搜索结果和使用说明输出到控制台，提供：
 * <ul>
 *   <li>搜索结果的格式化输出</li>
 *   <li>使用说明的显示</li>
 *   <li>搜索开始前的信息显示</li>
 *   <li>错误信息的输出</li>
 * </ul>
 * 
 * <p>设计特点：
 * <ul>
 *   <li>使用私有构造函数，禁止实例化</li>
 *   <li>所有方法都是静态方法，便于直接调用</li>
 *   <li>统一的分隔符样式，保持输出一致性</li>
 *   <li>清晰的层级结构，便于阅读</li>
 * </ul>
 */
public class ConsolePrinter {
    
    /** 输出分隔符，用于美化控制台输出 */
    private static final String SEPARATOR = "========================================";

    /**
     * 私有构造函数
     * 
     * <p>禁止实例化该工具类
     */
    private ConsolePrinter() {
    }

    /**
     * 输出搜索结果
     * 
     * <p>格式化输出所有关键词的搜索结果，包括：
     * <ul>
     *   <li>标题分隔符</li>
     *   <li>每个关键词的详细结果（出现次数、行号列表）</li>
     *   <li>结尾分隔符</li>
     * </ul>
     * 
     * @param results 搜索结果映射，键为关键词，值为对应的搜索结果
     */
    public static void printResults(Map<String, SearchResult> results) {
        System.out.println(SEPARATOR);
        System.out.println("关键词检索结果");
        System.out.println(SEPARATOR);

        // 遍历输出每个关键词的结果
        for (Map.Entry<String, SearchResult> entry : results.entrySet()) {
            SearchResult result = entry.getValue();
            printSingleResult(result);
        }

        System.out.println();
        System.out.println(SEPARATOR);
    }

    /**
     * 输出单个关键词的搜索结果
     * 
     * <p>格式化输出单个关键词的详细信息：
     * <ul>
     *   <li>关键词名称（带引号）</li>
     *   <li>出现次数</li>
     *   <li>包含该关键词的行号列表（如果未找到则显示"无"）</li>
     * </ul>
     * 
     * @param result 单个关键词的搜索结果
     */
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

    /**
     * 输出行号列表
     * 
     * <p>将行号列表格式化为逗号分隔的字符串输出
     * 
     * @param lineNumbers 行号列表
     */
    private static void printLineNumbers(List<Integer> lineNumbers) {
        for (int i = 0; i < lineNumbers.size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(lineNumbers.get(i));
        }
    }

    /**
     * 输出使用说明
     * 
     * <p>显示完整的使用帮助信息，包括：
     * <ul>
     *   <li>工具名称和版本</li>
     *   <li>命令行用法（包括选项参数）</li>
     *   <li>功能说明</li>
     *   <li>日志配置选项</li>
     *   <li>重要提示（关于带空格关键词的使用方法）</li>
     *   <li>使用示例</li>
     * </ul>
     */
    public static void printUsage() {
        System.out.println(SEPARATOR);
        System.out.println("文本文件关键词检索工具 - Java8");
        System.out.println(SEPARATOR);
        System.out.println();
        System.out.println("用法:");
        System.out.println("  java com.keywordsearch.Main [选项] <文件路径> <关键词1> [关键词2] [关键词3] ...");
        System.out.println();
        System.out.println("选项说明:");
        System.out.println("  --log-level=DEBUG|INFO|WARN|ERROR");
        System.out.println("        设置日志级别（默认: INFO）");
        System.out.println("        DEBUG: 最详细的调试信息");
        System.out.println("        INFO: 一般运行信息");
        System.out.println("        WARN: 警告信息");
        System.out.println("        ERROR: 错误信息");
        System.out.println();
        System.out.println("  --log-type=CONSOLE|FILE");
        System.out.println("        设置日志输出类型（默认: CONSOLE）");
        System.out.println("        CONSOLE: 输出到控制台");
        System.out.println("        FILE: 输出到文件");
        System.out.println();
        System.out.println("  --log-file=<路径>");
        System.out.println("        设置日志文件路径（默认: keywordsearch.log）");
        System.out.println("        仅当 --log-type=FILE 时有效");
        System.out.println();
        System.out.println("  --case-sensitive");
        System.out.println("        启用大小写敏感匹配（默认: 不敏感）");
        System.out.println();
        System.out.println("说明:");
        System.out.println("  - 支持 .txt 和 .log 格式的文本文件");
        System.out.println("  - 可指定单个或多个关键词进行检索");
        System.out.println("  - 统计每个关键词出现的总次数");
        System.out.println("  - 列出包含关键词的所有行号");
        System.out.println("  - 集成日志系统，可追踪执行过程和异常");
        System.out.println();
        System.out.println("重要提示:");
        System.out.println("  - 如果关键词包含空格，请用双引号将其包裹");
        System.out.println("  - 特殊字符（如 @、#、$、% 等）会被正确处理");
        System.out.println("  - 选项参数必须放在文件路径和关键词之前");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  # 基本用法");
        System.out.println("  java com.keywordsearch.Main test.txt error");
        System.out.println("  java com.keywordsearch.Main app.log error warning info");
        System.out.println();
        System.out.println("  # 带空格的关键词（必须用引号包裹）");
        System.out.println("  java com.keywordsearch.Main special_test.txt \"error code\"");
        System.out.println("  java com.keywordsearch.Main special_test.txt user@123 \"error code\"");
        System.out.println();
        System.out.println("  # 使用 DEBUG 级别日志（查看详细执行过程）");
        System.out.println("  java com.keywordsearch.Main --log-level=DEBUG test.txt error");
        System.out.println();
        System.out.println("  # 使用文件日志");
        System.out.println("  java com.keywordsearch.Main --log-type=FILE --log-file=search.log test.txt error");
        System.out.println();
        System.out.println("  # 大小写敏感匹配");
        System.out.println("  java com.keywordsearch.Main --case-sensitive app.log ERROR WARNING");
        System.out.println();
        System.out.println("  # 组合使用多个选项");
        System.out.println("  java com.keywordsearch.Main --log-level=DEBUG --case-sensitive test.txt \"error code\"");
        System.out.println();
    }

    /**
     * 输出搜索开始前的信息
     * 
     * <p>在搜索开始前显示目标文件和检索关键词列表，帮助用户：
     * <ul>
     *   <li>确认搜索的目标文件是否正确</li>
     *   <li>确认程序解析的关键词是否符合预期</li>
     *   <li>诊断参数传递问题（如空格被拆分）</li>
     * </ul>
     * 
     * <p>关键词会以编号列表的形式显示，并用引号包裹，便于用户查看是否被正确解析。
     * 
     * @param filePath 目标文件路径
     * @param keywords 检索关键词列表
     */
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

    /**
     * 输出错误信息
     * 
     * <p>将错误信息输出到标准错误流（System.err）
     * 
     * @param message 错误信息
     */
    public static void printError(String message) {
        System.err.println("错误: " + message);
    }
}
