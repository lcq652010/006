package com.keywordsearch.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志工厂类
 * 
 * <p>负责创建和管理日志实例，使用单例模式确保每个类只有一个日志实例。
 * 
 * <p>功能特点：
 * <ul>
 *   <li>根据类名获取日志实例</li>
 *   <li>支持多种日志实现（控制台、文件等）</li>
 *   <li>统一管理日志级别</li>
 *   <li>支持全局配置日志输出目标</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>
 * // 获取默认日志（控制台输出）
 * Logger logger = LoggerFactory.getLogger(MyClass.class);
 * 
 * // 设置日志级别
 * LoggerFactory.setGlobalLevel(Logger.Level.DEBUG);
 * 
 * // 使用文件日志
 * LoggerFactory.setLogType(LoggerFactory.LogType.FILE);
 * </pre>
 */
public class LoggerFactory {
    
    /**
     * 日志类型枚举
     */
    public enum LogType {
        /** 控制台日志 */
        CONSOLE,
        /** 文件日志 */
        FILE
    }
    
    /** 日志实例缓存，key为类名，value为日志实例 */
    private static final Map<String, Logger> loggers = new HashMap<String, Logger>();
    
    /** 当前日志类型 */
    private static LogType currentLogType = LogType.CONSOLE;
    
    /** 全局日志级别 */
    private static Logger.Level globalLevel = Logger.Level.INFO;
    
    /** 文件日志输出路径 */
    private static String logFilePath = "keywordsearch.log";
    
    /**
     * 私有构造函数
     * 
     * <p>禁止实例化该工具类
     */
    private LoggerFactory() {
    }
    
    /**
     * 根据类获取日志实例
     * 
     * <p>如果该类已有日志实例，则返回缓存的实例；否则创建新实例。
     * 
     * @param clazz 类对象
     * @return 日志实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }
    
    /**
     * 根据名称获取日志实例
     * 
     * <p>如果该名称已有日志实例，则返回缓存的实例；否则创建新实例。
     * 
     * @param name 日志名称
     * @return 日志实例
     */
    public static synchronized Logger getLogger(String name) {
        if (loggers.containsKey(name)) {
            return loggers.get(name);
        }
        
        Logger logger = createLogger(name);
        loggers.put(name, logger);
        return logger;
    }
    
    /**
     * 创建日志实例
     * 
     * <p>根据当前配置的日志类型创建对应的日志实例。
     * 
     * @param name 日志名称
     * @return 日志实例
     */
    private static Logger createLogger(String name) {
        switch (currentLogType) {
            case FILE:
                return new FileLogger(name, logFilePath, globalLevel);
            case CONSOLE:
            default:
                return new ConsoleLogger(name, globalLevel);
        }
    }
    
    /**
     * 设置全局日志级别
     * 
     * <p>新创建的日志实例将使用该级别。
     * 已存在的日志实例级别不会自动更新。
     * 
     * @param level 日志级别
     */
    public static void setGlobalLevel(Logger.Level level) {
        globalLevel = level;
    }
    
    /**
     * 获取全局日志级别
     * 
     * @return 当前全局日志级别
     */
    public static Logger.Level getGlobalLevel() {
        return globalLevel;
    }
    
    /**
     * 设置日志类型
     * 
     * <p>新创建的日志实例将使用该类型。
     * 已存在的日志实例类型不会自动更新。
     * 
     * @param logType 日志类型
     */
    public static void setLogType(LogType logType) {
        currentLogType = logType;
    }
    
    /**
     * 获取当前日志类型
     * 
     * @return 当前日志类型
     */
    public static LogType getLogType() {
        return currentLogType;
    }
    
    /**
     * 设置文件日志路径
     * 
     * <p>仅当使用 FILE 类型日志时有效。
     * 
     * @param path 文件路径
     */
    public static void setLogFilePath(String path) {
        logFilePath = path;
    }
    
    /**
     * 获取文件日志路径
     * 
     * @return 当前文件日志路径
     */
    public static String getLogFilePath() {
        return logFilePath;
    }
    
    /**
     * 重新初始化所有日志实例
     * 
     * <p>当更改了日志类型或路径后，调用此方法重新创建所有日志实例。
     * 注意：这会清除已缓存的日志实例。
     */
    public static synchronized void reinitialize() {
        loggers.clear();
    }
}
