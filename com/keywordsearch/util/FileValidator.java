package com.keywordsearch.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileValidator {
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".txt", ".log");

    private FileValidator() {
    }

    public static boolean exists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

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

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
