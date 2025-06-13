package com.dtflys.forest.http.body;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TemplateUtils;

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

    public StringRequestBody(String content, ForestRequest request) {
        this(content, request, request.arguments());
    }


    public StringRequestBody(String content, VariableScope scope, Object[] arguments) {
        this.content = TemplateUtils.readString(content, scope, arguments, false);
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
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.TEXT;
    }

    @Override
    public List<RequestNameValue> getNameValueList(ForestRequest request) {
        final List<RequestNameValue> nameValueList = new LinkedList<>();
        if (StringUtils.isNotBlank(content)) {
            final String[] items = content.split("&");
            for (String item : items) {
                final String[] pair = item.split("=", 2);
                if (pair.length == 1) {
                    nameValueList.add(new RequestNameValue(pair[0], MappingParameter.TARGET_BODY));
                } else if (pair.length == 2) {
                    nameValueList.add(new RequestNameValue(pair[0], pair[1], MappingParameter.TARGET_BODY));
                }
            }
        }
        return nameValueList;
    }

    @Override
    public StringRequestBody clone() {
        final StringRequestBody newBody = new StringRequestBody(content);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
