package com.dtflys.forest.example.model;

import lombok.Data;

@Data
public class DeepSeekResultChoice {
    
    private Integer index;
    
    private DeepSeekResultDelta delta;
}
