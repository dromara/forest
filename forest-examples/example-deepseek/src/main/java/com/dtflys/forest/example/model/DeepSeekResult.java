package com.dtflys.forest.example.model;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.dtflys.forest.utils.StringUtils;
import lombok.Data;

import java.util.List;

@Data
public class DeepSeekResult {

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
            DeepSeekResultChoice choice = chooseJson.toJavaObject(DeepSeekResultChoice.class);
            String reasoningContent = choice.getDelta().getReasoningContent();
            if (StringUtils.isNotEmpty(reasoningContent)) {
                return new DeepSeekContent(true, reasoningContent);
            }
            return new DeepSeekContent(false, choice.getDelta().getContent());
        }
        return new DeepSeekContent();
    }


}
