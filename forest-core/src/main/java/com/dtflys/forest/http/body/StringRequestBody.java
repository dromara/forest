package com.dtflys.forest.http.body;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestVariable;
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

    private ForestVariable content;

    public StringRequestBody(ForestVariable content) {
        this.content = content;
    }



    public String getContent() {
        if (content == null) {
            return null;
        }
        return ForestVariable.getStringValue(content, body.getRequest());
    }

    public void setContent(String content) {
        this.content = ForestVariable.create(content);
    }

    @Override
    public String toString() {
        if (content == null) {
            return "null";
        }
        return String.valueOf(content.getOriginalValue());
    }

    @Override
    public byte[] getByteArray() {
        final String contentStr = getContent();
        if (contentStr == null) {
            return new byte[0];
        }
        return contentStr.getBytes();
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.TEXT;
    }

    @Override
    public List<RequestNameValue> getNameValueList(ForestRequest request) {
        final List<RequestNameValue> nameValueList = new LinkedList<>();
        final String contentStr = getContent();
        if (StringUtils.isNotBlank(contentStr)) {
            final String[] items = contentStr.split("&");
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
