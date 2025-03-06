package com.dtflys.forest.example.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeepSeekResultDelta {
    
    private String content;

    @JSONField(name = "reasoning_content")
    private String reasoningContent;
}
