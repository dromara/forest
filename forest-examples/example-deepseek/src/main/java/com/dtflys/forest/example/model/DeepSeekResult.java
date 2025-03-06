package com.dtflys.forest.example.model;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.dtflys.forest.utils.StringUtils;
import lombok.Data;

import java.util.List;

@Data
public class DeepSeekResult {

    // {"id":"5627d18f-f399-4180-b9cb-aa9624936935","object":"chat.completion.chunk","created":1741256471,"model":"deepseek-chat","system_fingerprint":"fp_3a5770e1b4_prod0225","choices":[{"index":0,"delta":{"content":"我会"},"logprobs":null,"finish_reason":null}]}

    private String id;

    private String object;

    private Integer created;

    private String model;

    @JSONField(name = "system_fingerprint")
    private String systemFingerprint;

    private List<JSONObject> choices;
    

    public DeepSeekContent content() {
        List<JSONObject> choices = getChoices();
        if (CollectionUtil.isNotEmpty(choices)) {
            JSONObject chooseJson = choices.get(0);
            DeepSeekResultChoice choose = chooseJson.toJavaObject(DeepSeekResultChoice.class);
            String reasoningContent = choose.getDelta().getReasoningContent();
            if (StringUtils.isNotEmpty(reasoningContent)) {
                return new DeepSeekContent(true, reasoningContent);
            }
            return new DeepSeekContent(false, choose.getDelta().getContent());
        }
        return new DeepSeekContent();
    }


}
