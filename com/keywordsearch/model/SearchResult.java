package com.keywordsearch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 搜索结果数据模型类
 * 
 * <p>该类封装了单个关键词的搜索结果，包含：
 * <ul>
 *   <li>关键词本身</li>
 *   <li>该关键词在文件中出现的总次数</li>
 *   <li>包含该关键词的所有行号列表</li>
 * </ul>
 * 
 * <p>设计特点：
 * <ul>
 *   <li>使用不可变集合返回行号列表，保证数据安全</li>
 *   <li>提供便捷的方法来更新和获取搜索结果</li>
 *   <li>支持判断关键词是否被找到</li>
 * </ul>
 */
public class SearchResult {
    
    /** 关键词，一旦初始化后不可修改 */
    private final String keyword;
    
    /** 关键词出现的总次数 */
    private int count;
    
    /** 包含该关键词的行号列表 */
    private final List<Integer> lineNumbers;

    /**
     * 构造函数
     * 
     * <p>创建一个新的搜索结果对象，初始次数为0，行号列表为空
     * 
     * @param keyword 要搜索的关键词
     */
    public SearchResult(String keyword) {
        this.keyword = keyword;
        this.count = 0;
        this.lineNumbers = new ArrayList<Integer>();
    }

    /**
     * 获取关键词
     * 
     * @return 关键词字符串
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * 获取关键词出现的总次数
     * 
     * @return 出现次数
     */
    public int getCount() {
        return count;
    }

    /**
     * 获取包含该关键词的行号列表
     * 
     * <p>返回的是不可修改的视图，以保护内部数据不被外部修改
     * 
     * @return 行号列表的不可修改视图
     */
    public List<Integer> getLineNumbers() {
        return Collections.unmodifiableList(lineNumbers);
    }

    /**
     * 增加关键词出现的次数
     * 
     * <p>只有当增量为正数时才会增加次数
     * 
     * @param amount 要增加的次数
     */
    public void incrementCount(int amount) {
        if (amount > 0) {
            this.count += amount;
        }
    }

    /**
     * 添加包含该关键词的行号
     * 
     * <p>只有当行号为正数且未在列表中时才会添加，避免重复
     * 
     * @param lineNumber 行号（从1开始）
     */
    public void addLineNumber(int lineNumber) {
        if (lineNumber > 0 && !lineNumbers.contains(lineNumber)) {
            this.lineNumbers.add(lineNumber);
        }
    }

    /**
     * 判断该关键词是否在文件中被找到
     * 
     * @return 如果出现次数大于0则返回true，否则返回false
     */
    public boolean isFound() {
        return count > 0;
    }
}
