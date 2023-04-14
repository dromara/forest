package org.dromara.forest.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

public class Cause {

    @JSONField(ordinal = 1)
    private Integer id;

    @JSONField(ordinal = 2)
    private Integer score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
