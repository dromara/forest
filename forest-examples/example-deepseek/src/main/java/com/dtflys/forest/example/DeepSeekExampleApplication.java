package com.dtflys.forest.example;

import cn.hutool.core.lang.Opt;
import com.dtflys.forest.example.client.DeepSeek;
import com.dtflys.forest.example.model.DeepSeekContent;
import com.dtflys.forest.example.model.DeepSeekResult;
import com.dtflys.forest.sse.SSELinesMode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
public class DeepSeekExampleApplication implements CommandLineRunner {

    @Resource
    private DeepSeek deepSeek;

    private void completions(String message) {
        deepSeek.completions(message)
                .setOnMessage(event -> {
                    try {
                        DeepSeekResult result = event.value(DeepSeekResult.class);
                        System.out.print(Opt.ofBlankAble(result.content().getContent()).orElse(""));
                    } catch (Exception e) {
                    }
                })
                .listen(SSELinesMode.SINGLE_LINE);
    }


    /**
     * 带有思维链的聊天
     * 
     * @param message 发送给DeepSeek的消息
     */
    private void completionsWithReasoning(String message) {
        AtomicBoolean isFirstReasoning = new AtomicBoolean(false);
        deepSeek.completions(message)
                .setOnMessage(event -> {
                    try {
                        DeepSeekResult result = event.value(DeepSeekResult.class);
                        DeepSeekContent content = result.content();
                        if (content.isReasoning() && isFirstReasoning.compareAndSet(false, true)) {
                            System.out.println("<思维链>");
                            System.out.print(content.getContent());
                        } else if (!content.isReasoning() && isFirstReasoning.compareAndSet(true, false)) {
                            System.out.print(content.getContent());
                            System.out.println("\n</思维链>\n");
                        } else {
                            System.out.print(Opt.ofBlankAble(content.getContent()).orElse(""));
                        }
                    } catch (Exception e) {
                    }
                })
                .listen(SSELinesMode.SINGLE_LINE);
    }

    @Override
    public void run(String... args) {
        completionsWithReasoning("1+1等于几？");
    }

    public static void main(String[] args) {
        try {
            SpringApplication.run(DeepSeekExampleApplication.class, args);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}
