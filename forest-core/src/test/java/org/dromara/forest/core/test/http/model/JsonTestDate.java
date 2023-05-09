package org.dromara.forest.core.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

public class JsonTestDate implements Serializable {

//    @JSONField(format = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
