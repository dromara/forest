package org.dromara.forest.http.body;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.RequestNameValue;
import org.dromara.forest.utils.StringUtils;

import java.nio.charset.Charset;
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

    public byte[] getByteArray(Charset charset) {
        return content.getBytes(charset);
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
