package org.forest.executors.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.utils.RequestNameValue;
import org.forest.exceptions.ForestNetworkException;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.mapping.MappingTemplate;
import org.forest.utils.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:22
 */
public abstract class AbstractEntityHttpExecutor<T extends HttpEntityEnclosingRequestBase> extends AbstractHttpclientExecutor<T> {

    private static Log log = LogFactory.getLog(HttpclientPostExecutor.class);


    public AbstractEntityHttpExecutor(HttpclientConnectionManager connectionManager, ForestRequest requst) {
        super(connectionManager, requst);
    }

    @Override
    protected String buildUrl() {
        return request.getUrl();
    }

    @Override
    public void setupBody() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String contentType = request.getContentType();

        String[] typeGroup = contentType.split(";");
        String mineType = typeGroup[0];
        String charset = HTTP.UTF_8;
        if (StringUtils.isEmpty(mineType)) {
            mineType = "application/x-www-form-urlencoded";
        }
        if (typeGroup.length > 1) {
            charset = HTTP.UTF_8;
        }

        List<RequestNameValue> nameValueList = request.getDataNameValueList();

        if (mineType.equals("application/x-www-form-urlencoded")) {
            setEntities(httpRequest, contentType, nameValueList, nameValuePairs);
        }
        else if (mineType.equals("application/json")) {
            ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
            String text = null;
            if (request.getRequestBody() != null) {
                text = request.getRequestBody();
            }
            else {
                Map<String, Object> map = convertNameValueListToJSON(nameValueList);
                String json = jsonConverter.convertToJson(map);
                text = json;
            }
            try {
                StringEntity entity = new StringEntity(text, charset);
                entity.setContentType(contentType);
//                        entity.setContentEncoding(HTTP.UTF_8);
                httpRequest.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                throw new ForestRuntimeException(e);
            }
        }
        else {
        }
    }

    @Override
    protected String getLogContentForBody(T httpReq) {
        try {
            InputStream in = httpReq.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer buffer = new StringBuffer();
            String line;
            String body;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + " ");
            }
            body = buffer.toString();
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> convertNameValueListToJSON(List<RequestNameValue> nameValueList) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            if (value instanceof Date) {
                value = MappingTemplate.getParameterValue(jsonConverter, value);
            }
            if (value == null && StringUtils.isNotEmpty(name)) {
                Map nameMap = jsonConverter.convertToJavaObject(name, Map.class);
                if (nameMap != null && nameMap.size() > 0) {
                    map.putAll(nameMap);
                } else {
                    map.put(name, value);
                }
            }
            else {
                map.put(name, value);
            }
        }
        return map;
    }


    private void setEntities(T httpReq, String contentType, List<RequestNameValue> nameValueList, List<NameValuePair> nameValuePairs) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            NameValuePair nameValuePair = new BasicNameValuePair(name, MappingTemplate.getParameterValue(jsonConverter, value));
            nameValuePairs.add(nameValuePair);
        }

        try {
            StringEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            if (StringUtils.isNotEmpty(contentType)) {
                entity.setContentType(contentType);
            }
            httpReq.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private static HttpResponse sendRequest(HttpClient httpclient, HttpUriRequest httpPost) throws IOException {
        HttpResponse httpResponse = null;
        httpResponse = httpclient.execute(httpPost);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode < HttpStatus.SC_OK || statusCode > HttpStatus.SC_MULTI_STATUS) {
            throw new ForestNetworkException(httpResponse.getStatusLine().getReasonPhrase(), statusCode);
        }
        return httpResponse;
    }

    public void execute(int retryCount) {
        try {
            logRequestBegine(retryCount, client, httpRequest);
            httpResponse = sendRequest(client, httpRequest);
            ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
            logResponse(retryCount, client, httpRequest, response);
            setResponse(response);
            if (response.isError()) {
                throw new ForestNetworkException(httpResponse.getStatusLine().getReasonPhrase(), response.getStatusCode());
            }
        } catch (IOException e) {
            if (retryCount >= request.getRetryCount()) {
                httpRequest.abort();
                throw new RuntimeException(e);
            }
            log.error(e.getMessage());
            execute(retryCount + 1);
        } catch (ForestRuntimeException e) {
            httpRequest.abort();
            throw e;
        }
    }
}
