package org.dromara.forest.example;

import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;

public interface ChatGPT {

    @Post(
            url = "https://api.openai.com/v1/engines/${model}/completions",
            contentType = "application/json",
            headers = "Authorization: Bearer ${apiKey}",
            data = "{\"prompt\": \"${prompt}\", \"max_tokens\": ${maxTokens}, \"temperature\": ${temperature}}"
    )
    GPTResponse send(@Var("prompt") String prompt);

}
