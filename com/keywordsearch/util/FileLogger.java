package com.keywordsearch.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件日志实现类
 * 
 * <p>将日志输出到文件，支持：
 * <ul>
 *   <li>多种日志级别（DEBUG、INFO、WARN、ERROR）</li>
 *   <li>时间戳显示</li>
 *   <li>线程名显示</li>
 *   <li>日志名称显示</li>
 *   <li>追加模式写入文件</li>
 *   <li>自动创建目录和文件</li>
 *   <li>异常堆栈信息输出</li>
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
public class FileLogger implements Logger {
    
    /** 日期格式 */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    /** 日志名称 */
    private final String name;
    
    /** 当前日志级别 */
    private Logger.Level level;
    
    /** 日志文件路径 */
    private final String logFilePath;
    
    /** 锁对象，用于同步写入 */
    private final Object lock = new Object();

    /**
     * 构造函数
     * 
     * @param name 日志名称
     * @param logFilePath 日志文件路径
     */
    public FileLogger(String name, String logFilePath) {
        this(name, logFilePath, Logger.Level.INFO);
    }
    
    /**
     * 构造函数
     * 
     * @param name 日志名称
     * @param logFilePath 日志文件路径
     * @param level 初始日志级别
     */
    public FileLogger(String name, String logFilePath, Logger.Level level) {
        this.name = name;
        this.logFilePath = logFilePath;
        this.level = level;
        ensureLogFileExists();
    }
    
    /**
     * 确保日志文件存在
     * 
     * <p>如果日志文件不存在则创建，包括父目录不存在则创建父目录。
     */
    private void ensureLogFileExists() {
        try {
            File logFile = new File(logFilePath);
            File parentDir = logFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("无法创建日志文件: " + e.getMessage());
        }
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
        
        return String.format("%s [%s] %s %s - %s", 
            timestamp, threadName, levelStr, name, message);
    }
    
    /**
     * 输出日志到文件
     * 
     * <p>使用同步锁确保线程安全，以追加模式写入文件。
     * 
     * @param level 日志级别
     * @param message 日志消息
     */
    private void log(Logger.Level level, String message) {
        if (!isEnabled(level)) {
            return;
        }
        
        String logLine = buildLogLine(level, message);
        
        synchronized (lock) {
            PrintWriter writer = null;
            try {
                ensureLogFileExists();
                writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)));
                writer.println(logLine);
                writer.flush();
            } catch (IOException e) {
                System.err.println("写入日志文件失败: " + e.getMessage());
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
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
                synchronized (lock) {
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)));
                        throwable.printStackTrace(writer);
                        writer.flush();
                    } catch (IOException e) {
                        System.err.println("写入异常堆栈失败: " + e.getMessage());
                    } finally {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                }
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
     * 获取日志文件路径
     * 
     * @return 日志文件路径
     */
    public String getLogFilePath() {
        return logFilePath;
    }
}
