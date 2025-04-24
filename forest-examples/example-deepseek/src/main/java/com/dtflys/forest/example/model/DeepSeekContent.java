package com.dtflys.forest.example.model;

import lombok.Data;

@Data
public class DeepSeekContent {
    
    private boolean reasoning = false;
    
    private String content = "";

    public DeepSeekContent() {
    }

    public DeepSeekContent(boolean reasoning, String content) {
        this.reasoning = reasoning;
        this.content = content;
    }
}
