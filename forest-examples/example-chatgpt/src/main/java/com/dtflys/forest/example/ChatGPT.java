package com.dtflys.forest.example;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;

public interface ChatGPT {

    @Post(
            url = "https://api.openai.com/v1/engines/${model}/completions",
            contentType = "application/json",
            headers = "Authorization: Bearer ${apiKey}",
            data = "{\"prompt\": \"${prompt}\", \"max_tokens\": ${maxTokens}, \"temperature\": ${temperature}}"
    )
    GPTResponse send(@Var("prompt") String prompt);


    @Post(
            url = "https://api.openai.com/v1/chat/completions",
            contentType = "application/json",
            headers = "Authorization: Bearer ${apiKey}"
    )
    String send4o(@Body GPT4oBody body);

}
