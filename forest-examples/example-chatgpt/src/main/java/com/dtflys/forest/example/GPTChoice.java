package com.dtflys.forest.example;

import com.alibaba.fastjson.annotation.JSONField;

public class GPTChoice {

    private String text;

    private Integer index;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
