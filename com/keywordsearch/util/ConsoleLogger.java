package com.keywordsearch.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 控制台日志实现类
 * 
 * <p>将日志输出到控制台（System.out 和 System.err），支持：
 * <ul>
 *   <li>多种日志级别（DEBUG、INFO、WARN、ERROR）</li>
 *   <li>时间戳显示</li>
 *   <li>线程名显示</li>
 *   <li>日志名称显示</li>
 *   <li>不同级别使用不同颜色（可选）</li>
 *   <li>格式化消息（支持占位符 {}）</li>
 * </ul>
 * 
 * <p>日志格式示例：
 * <pre>
 * 2026-04-30 15:30:45.123 [main] INFO  com.keywordsearch.Main - 开始检索文件: test.txt
 * 2026-04-30 15:30:45.124 [main] DEBUG com.keywordsearch.Main - 解析关键词: [error, warning]
 * 2026-04-30 15:30:45.125 [main] ERROR com.keywordsearch.Main - 文件不存在: notfound.txt
 * </pre>
 */
public class ConsoleLogger implements Logger {
    
    /** 日期格式 */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    /** 日志名称 */
    private final String name;
    
    /** 当前日志级别 */
    private Logger.Level level;
    
    /** 是否启用 ANSI 颜色输出 */
    private boolean ansiEnabled = false;
    
    /**
     * ANSI 颜色代码
     */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    /**
     * 构造函数
     * 
     * @param name 日志名称
     */
    public ConsoleLogger(String name) {
        this(name, Logger.Level.INFO);
    }
    
    /**
     * 构造函数
     * 
     * @param name 日志名称
     * @param level 初始日志级别
     */
    public ConsoleLogger(String name, Logger.Level level) {
        this.name = name;
        this.level = level;
    }
    
    /**
     * 格式化日志消息
     * 
     * <p>使用占位符 {} 替换参数，例如：
     * <pre>
     * format("处理文件: {}, 关键词: {}", "test.txt", "error")
     * 结果: "处理文件: test.txt, 关键词: error"
     * </pre>
     * 
     * @param message 消息模板
     * @param args 参数数组
     * @return 格式化后的消息
     */
    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        
        StringBuilder sb = new StringBuilder();
        int argIndex = 0;
        int i = 0;
        
        while (i < message.length()) {
            if (i < message.length() - 1 && message.charAt(i) == '{' && message.charAt(i + 1) == '}') {
                if (argIndex < args.length) {
                    sb.append(args[argIndex]);
                    argIndex++;
                } else {
                    sb.append("{}");
                }
                i += 2;
            } else {
                sb.append(message.charAt(i));
                i++;
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 构建完整的日志行
     * 
     * @param level 日志级别
     * @param message 日志消息
     * @return 格式化后的完整日志行
     */
    private String buildLogLine(Logger.Level level, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String timestamp = sdf.format(new Date());
        String threadName = Thread.currentThread().getName();
        
        // 级别名称左对齐，最多5个字符
        String levelStr = String.format("%-5s", level.name());
        
        // 简化类名（只显示最后一个点之后的部分）
        String simpleName = name;
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0 && lastDot < name.length() - 1) {
            simpleName = name.substring(lastDot + 1);
        }
        
        return String.format("%s [%s] %s %s - %s", 
            timestamp, threadName, levelStr, simpleName, message);
    }
    
    /**
     * 输出日志到控制台
     * 
     * @param level 日志级别
     * @param message 日志消息
     */
    private void log(Logger.Level level, String message) {
        if (!isEnabled(level)) {
            return;
        }
        
        String logLine = buildLogLine(level, message);
        
        // ERROR 级别输出到 System.err，其他输出到 System.out
        if (level == Logger.Level.ERROR) {
            if (ansiEnabled) {
                System.err.println(ANSI_RED + logLine + ANSI_RESET);
            } else {
                System.err.println(logLine);
            }
        } else {
            if (ansiEnabled) {
                String color = getColorForLevel(level);
                System.out.println(color + logLine + ANSI_RESET);
            } else {
                System.out.println(logLine);
            }
        }
    }
    
    /**
     * 根据日志级别获取 ANSI 颜色
     * 
     * @param level 日志级别
     * @return ANSI 颜色代码
     */
    private String getColorForLevel(Logger.Level level) {
        switch (level) {
            case DEBUG:
                return ANSI_CYAN;
            case INFO:
                return ANSI_GREEN;
            case WARN:
                return ANSI_YELLOW;
            case ERROR:
                return ANSI_RED;
            default:
                return ANSI_WHITE;
        }
    }

    @Override
    public void debug(String message) {
        log(Logger.Level.DEBUG, message);
    }

    @Override
    public void debug(String message, Object... args) {
        if (isDebugEnabled()) {
            log(Logger.Level.DEBUG, formatMessage(message, args));
        }
    }

    @Override
    public void info(String message) {
        log(Logger.Level.INFO, message);
    }

    @Override
    public void info(String message, Object... args) {
        if (isInfoEnabled()) {
            log(Logger.Level.INFO, formatMessage(message, args));
        }
    }

    @Override
    public void warn(String message) {
        log(Logger.Level.WARN, message);
    }

    @Override
    public void warn(String message, Object... args) {
        if (isEnabled(Logger.Level.WARN)) {
            log(Logger.Level.WARN, formatMessage(message, args));
        }
    }

    @Override
    public void error(String message) {
        log(Logger.Level.ERROR, message);
    }

    @Override
    public void error(String message, Object... args) {
        if (isEnabled(Logger.Level.ERROR)) {
            log(Logger.Level.ERROR, formatMessage(message, args));
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (isEnabled(Logger.Level.ERROR)) {
            log(Logger.Level.ERROR, message);
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
        }
    }

    @Override
    public boolean isEnabled(Logger.Level level) {
        return level.getValue() >= this.level.getValue();
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(Logger.Level.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(Logger.Level.INFO);
    }

    @Override
    public Logger.Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Logger.Level level) {
        this.level = level;
    }
    
    /**
     * 设置是否启用 ANSI 颜色输出
     * 
     * @param enabled 是否启用
     */
    public void setAnsiEnabled(boolean enabled) {
        this.ansiEnabled = enabled;
    }
    
    /**
     * 检查是否启用了 ANSI 颜色输出
     * 
     * @return 如果启用则返回 true
     */
    public boolean isAnsiEnabled() {
        return ansiEnabled;
    }
}
