package com.dtflys.forest.example.client;


import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestSSE;

@BaseRequest(baseURL = "{baseUrl}")
public interface DeepSeek {
    
    @Post(
            url = "/chat/completions",
            contentType = "application/json",
            headers = "Authorization: Bearer {apiKey}",
            data = "{\"messages\":[{\"content\":\"{content}\",\"role\":\"user\"}],\"model\":\"{model}\",\"stream\":true}")
    ForestSSE completions(@Var("content") String content);
}
