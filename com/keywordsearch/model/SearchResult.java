package com.keywordsearch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResult {
    private final String keyword;
    private int count;
    private final List<Integer> lineNumbers;

    public SearchResult(String keyword) {
        this.keyword = keyword;
        this.count = 0;
        this.lineNumbers = new ArrayList<Integer>();
    }

    public String getKeyword() {
        return keyword;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getLineNumbers() {
        return Collections.unmodifiableList(lineNumbers);
    }

    public void incrementCount(int amount) {
        if (amount > 0) {
            this.count += amount;
        }
    }

    public void addLineNumber(int lineNumber) {
        if (lineNumber > 0 && !lineNumbers.contains(lineNumber)) {
            this.lineNumbers.add(lineNumber);
        }
    }

    public boolean isFound() {
        return count > 0;
    }
}
