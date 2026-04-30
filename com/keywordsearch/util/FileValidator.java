package com.keywordsearch.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 文件验证工具类
 * 
 * <p>该类提供了文件相关的验证功能，包括：
 * <ul>
 *   <li>检查文件是否存在</li>
 *   <li>验证文件格式是否受支持（.txt、.log）</li>
 *   <li>综合验证文件的有效性</li>
 * </ul>
 * 
 * <p>设计特点：
 * <ul>
 *   <li>使用私有构造函数，禁止实例化</li>
 *   <li>所有方法都是静态方法，便于直接调用</li>
 *   <li>包含内部类 ValidationResult，用于返回验证结果和错误信息</li>
 * </ul>
 */
public class FileValidator {
    
    /** 支持的文件扩展名列表 */
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".txt", ".log");

    /**
     * 私有构造函数
     * 
     * <p>禁止实例化该工具类
     */
    private FileValidator() {
    }

    /**
     * 检查文件是否存在
     * 
     * <p>验证指定路径的文件是否存在且是一个文件（不是目录）
     * 
     * @param filePath 文件路径
     * @return 如果文件存在且是文件则返回true，否则返回false
     */
    public static boolean exists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 验证文件格式是否受支持
     * 
     * <p>检查文件扩展名是否为 .txt 或 .log（不区分大小写）
     * 
     * @param filePath 文件路径
     * @return 如果文件格式受支持则返回true，否则返回false
     */
    public static boolean isValidFileType(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        String lowerPath = filePath.toLowerCase();
        for (String extension : SUPPORTED_EXTENSIONS) {
            if (lowerPath.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 综合验证文件的有效性
     * 
     * <p>执行完整的文件验证流程：
     * <ol>
     *   <li>检查文件路径是否为空</li>
     *   <li>检查文件是否存在</li>
     *   <li>验证文件格式是否受支持</li>
     * </ol>
     * 
     * @param filePath 文件路径
     * @return 验证结果对象，包含是否有效和错误信息
     */
    public static ValidationResult validate(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return ValidationResult.error("文件路径不能为空");
        }
        if (!exists(filePath)) {
            return ValidationResult.error("文件不存在: " + filePath);
        }
        if (!isValidFileType(filePath)) {
            return ValidationResult.error("不支持的文件格式，支持的格式: " + SUPPORTED_EXTENSIONS);
        }
        return ValidationResult.success();
    }

    /**
     * 验证结果类
     * 
     * <p>封装文件验证的结果，包含：
     * <ul>
     *   <li>是否验证通过</li>
     *   <li>错误信息（如果验证失败）</li>
     * </ul>
     * 
     * <p>使用静态工厂方法创建实例：
     * <ul>
     *   <li>{@link #success()} - 创建成功的验证结果</li>
     *   <li>{@link #error(String)} - 创建失败的验证结果，包含错误信息</li>
     * </ul>
     */
    public static class ValidationResult {
        
        /** 验证是否通过 */
        private final boolean valid;
        
        /** 错误信息（如果验证失败） */
        private final String errorMessage;

        /**
         * 私有构造函数
         * 
         * <p>通过静态工厂方法创建实例
         * 
         * @param valid 是否验证通过
         * @param errorMessage 错误信息
         */
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        /**
         * 创建成功的验证结果
         * 
         * @return 验证结果对象，valid为true，errorMessage为null
         */
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        /**
         * 创建失败的验证结果
         * 
         * @param message 错误信息
         * @return 验证结果对象，valid为false，包含错误信息
         */
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        /**
         * 检查验证是否通过
         * 
         * @return 如果验证通过则返回true，否则返回false
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * 获取错误信息
         * 
         * @return 错误信息字符串，如果验证通过则返回null
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
