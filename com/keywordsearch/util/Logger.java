package com.keywordsearch.util;

/**
 * 日志接口
 * 
 * <p>定义了标准的日志操作接口，支持多种日志级别：
 * <ul>
 *   <li>DEBUG - 调试信息，用于开发和调试</li>
 *   <li>INFO - 一般信息，用于记录正常的程序执行</li>
 *   <li>WARN - 警告信息，用于记录潜在的问题</li>
 *   <li>ERROR - 错误信息，用于记录异常和错误</li>
 * </ul>
 * 
 * <p>设计特点：
 * <ul>
 *   <li>使用接口抽象，支持多种日志实现</li>
 *   <li>包含级别检查方法，避免不必要的字符串拼接</li>
 *   <li>支持格式化消息，使用占位符 {}</li>
 * </ul>
 */
public interface Logger {
    
    /**
     * 日志级别枚举
     */
    enum Level {
        /** 调试级别，最详细的日志 */
        DEBUG(0),
        /** 信息级别，记录一般信息 */
        INFO(1),
        /** 警告级别，记录潜在问题 */
        WARN(2),
        /** 错误级别，记录异常和错误 */
        ERROR(3);
        
        private final int value;
        
        Level(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    /**
     * 输出 DEBUG 级别日志
     * 
     * @param message 日志消息
     */
    void debug(String message);
    
    /**
     * 输出 DEBUG 级别日志（带参数）
     * 
     * <p>使用占位符 {} 表示参数位置，例如：
     * <pre>
     * logger.debug("处理文件: {}, 关键词: {}", filePath, keyword);
     * </pre>
     * 
     * @param message 日志消息（包含占位符 {}）
     * @param args 参数数组
     */
    void debug(String message, Object... args);
    
    /**
     * 输出 INFO 级别日志
     * 
     * @param message 日志消息
     */
    void info(String message);
    
    /**
     * 输出 INFO 级别日志（带参数）
     * 
     * @param message 日志消息（包含占位符 {}）
     * @param args 参数数组
     */
    void info(String message, Object... args);
    
    /**
     * 输出 WARN 级别日志
     * 
     * @param message 日志消息
     */
    void warn(String message);
    
    /**
     * 输出 WARN 级别日志（带参数）
     * 
     * @param message 日志消息（包含占位符 {}）
     * @param args 参数数组
     */
    void warn(String message, Object... args);
    
    /**
     * 输出 ERROR 级别日志
     * 
     * @param message 日志消息
     */
    void error(String message);
    
    /**
     * 输出 ERROR 级别日志（带参数）
     * 
     * @param message 日志消息（包含占位符 {}）
     * @param args 参数数组
     */
    void error(String message, Object... args);
    
    /**
     * 输出 ERROR 级别日志（带异常）
     * 
     * @param message 日志消息
     * @param throwable 异常对象
     */
    void error(String message, Throwable throwable);
    
    /**
     * 检查是否启用了指定的日志级别
     * 
     * <p>用于避免不必要的字符串拼接，例如：
     * <pre>
     * if (logger.isDebugEnabled()) {
     *     logger.debug("复杂的调试信息: " + computeExpensiveInfo());
     * }
     * </pre>
     * 
     * @param level 日志级别
     * @return 如果启用则返回 true
     */
    boolean isEnabled(Level level);
    
    /**
     * 检查是否启用了 DEBUG 级别
     * 
     * @return 如果启用则返回 true
     */
    boolean isDebugEnabled();
    
    /**
     * 检查是否启用了 INFO 级别
     * 
     * @return 如果启用则返回 true
     */
    boolean isInfoEnabled();
    
    /**
     * 获取当前日志级别
     * 
     * @return 当前日志级别
     */
    Level getLevel();
    
    /**
     * 设置日志级别
     * 
     * @param level 新的日志级别
     */
    void setLevel(Level level);
}
