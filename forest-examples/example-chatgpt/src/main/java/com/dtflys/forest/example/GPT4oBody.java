package com.dtflys.forest.example;

import java.util.List;

public class GPT4oBody {
    
    private String model;
    
    private List<GPT4oMessage> messages;
    
    private String stream;
    
    private Float temperature;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<GPT4oMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<GPT4oMessage> messages) {
        this.messages = messages;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }
}
