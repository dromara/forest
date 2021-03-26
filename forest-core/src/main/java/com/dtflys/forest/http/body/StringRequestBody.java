package com.dtflys.forest.http.body;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 字符串类型请求体
 * <p>该请求体对象会包装一个字符串, 其字符串会被放置到请求体中</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-22 17:30
 */
public class StringRequestBody extends ForestRequestBody implements SupportFormUrlEncoded {

    private String content;

    public StringRequestBody(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public byte[] getByteArray() {
        return content.getBytes();
    }

    @Override
    public List<RequestNameValue> getNameValueList(ForestConfiguration configuration) {
        List<RequestNameValue> nameValueList = new LinkedList<>();
        if (StringUtils.isNotBlank(content)) {
            String[] items = content.split("&");
            for (String item : items) {
                String[] pair = item.split("=", 2);
                if (pair.length == 1) {
                    nameValueList.add(new RequestNameValue(pair[0], MappingParameter.TARGET_BODY));
                } else if (pair.length == 2) {
                    nameValueList.add(new RequestNameValue(pair[0], pair[1], MappingParameter.TARGET_BODY));
                }
            }
        }
        return nameValueList;
    }
}
