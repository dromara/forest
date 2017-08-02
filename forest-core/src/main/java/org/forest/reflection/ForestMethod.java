package org.forest.reflection;

import org.apache.commons.collections.CollectionUtils;
import org.forest.Forest;
import org.forest.annotation.Request;
import org.forest.annotation.DataObject;
import org.forest.annotation.DataParam;
import org.forest.annotation.DataVariable;
import org.forest.callback.OnError;
import org.forest.callback.OnSuccess;
import org.forest.config.ForestConfiguration;
import org.forest.config.VariableScope;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.interceptor.Interceptor;
import org.forest.mapping.*;
import org.forest.http.ForestRequest;
import org.forest.proxy.InterfaceProxyHandler;
import org.forest.utils.ForestDataType;
import org.forest.utils.RequestNameValue;
import org.forest.utils.StringUtils;
import org.forest.utils.URLUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 通过代理调用的实际执行的方法对象
 * @author gongjun
 * @since 2016-05-03
 */
public class ForestMethod<T> implements VariableScope {

    private final InterfaceProxyHandler interfaceProxyHandler;
    private final ForestConfiguration configuration;
    private final Method method;
    private Class returnClass;
    private MappingTemplate urlTemplate;
    private MappingTemplate typeTemplate;
    private MappingTemplate dataTypeTemplate;
    private Integer timeout = null;
    private Integer retryNumber = null;
    private MappingTemplate contentTypeTemplate;
    private MappingTemplate[] dataTemplateArray;
    private MappingTemplate[] headerTemplateArray;
    private MappingParameter[] parameterTemplateArray;
    private List<MappingParameter> namedParameters = new ArrayList<MappingParameter>();
    private Map<String, MappingVariable> variables = new HashMap<String, MappingVariable>();
    private MappingParameter onSuccessParameter = null;
    private MappingParameter onErrorParameter = null;
    private List<Interceptor> interceptorList;
    private Class onSuccessClassGenericType = null;
    private boolean async = false;
    private boolean logEnable = true;

    public ForestMethod(InterfaceProxyHandler interfaceProxyHandler, ForestConfiguration configuration, Method method) {
        this.interfaceProxyHandler = interfaceProxyHandler;
        this.configuration = configuration;
        this.method = method;
        processInterfaceMethods();
    }

    public ForestConfiguration getConfiguration() {
        return configuration;
    }


    public Object getVariableValue(String name) {
        Object value = configuration.getVariableValue(name);
        return value;
    }

    private MappingTemplate makeTemplate(String text) {
        return new MappingTemplate(text, this);
    }


    public Class getReturnClass() {
        return returnClass;
    }


    public MappingVariable getVariable(String name) {
        return variables.get(name);
    }

    /**
     * 处理接口中定义的方法
     */
    private void processInterfaceMethods() {
        Annotation[] annotations = method.getAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            Annotation ann = annotations[i];
            if (ann instanceof Request) {
                Request reqAnn = (Request) ann;
                urlTemplate = makeTemplate(reqAnn.url());
                typeTemplate = makeTemplate(reqAnn.type());
                dataTypeTemplate = makeTemplate(reqAnn.dataType());
                contentTypeTemplate = makeTemplate(reqAnn.contentType());
                async = reqAnn.async();
                String[] dataArray = reqAnn.data();
                String[] headerArray = reqAnn.headers();
                int tout = reqAnn.timeout();
                if (tout > 0) {
                    timeout = tout;
                }
                int rtnum = reqAnn.retryCount();
                if (rtnum > 0) {
                    retryNumber = rtnum;
                }
                logEnable = reqAnn.logEnable();
                Class[] paramTypes = method.getParameterTypes();
                Type[] genericParamTypes = method.getGenericParameterTypes();
                TypeVariable<Method>[] typeVariables = method.getTypeParameters();
                for (TypeVariable<Method> typeVariable : typeVariables) {
                    System.out.println(typeVariable.getName());
                }
                Annotation[][] paramAnns = method.getParameterAnnotations();
                parameterTemplateArray = new MappingParameter[paramTypes.length];
                processParameters(paramTypes, genericParamTypes, paramAnns);

                dataTemplateArray = new MappingTemplate[dataArray.length];
                for (int j = 0; j < dataArray.length; j++) {
                    String data = dataArray[j];
                    MappingTemplate dataTemplate = makeTemplate(data);
                    dataTemplateArray[j] = dataTemplate;
                }

                headerTemplateArray = new MappingTemplate[headerArray.length];
                for (int j = 0; j < headerArray.length; j++) {
                    String header = headerArray[j];
                    MappingTemplate headerTemplate = makeTemplate(header);
                    headerTemplateArray[j] = headerTemplate;
                }

                Class[] interceptorClasses = reqAnn.interceptor();
                if (interceptorClasses != null && interceptorClasses.length > 0) {
                    interceptorList = new LinkedList<>();
                    for (int cidx = 0, len = interceptorClasses.length; cidx < len; cidx++) {
                        Class clazz = interceptorClasses[cidx];
                        if (!Interceptor.class.isAssignableFrom(clazz) || clazz.isInterface()) {
                            throw new ForestRuntimeException("Class [" + clazz.getName() + "] is not a implement of [" +
                                    Interceptor.class.getName() + "] interface.");
                        }
                        Interceptor interceptor = Forest.getInterceptor(clazz);
                        interceptorList.add(interceptor);
                    }
                }
            }
        }
        returnClass = method.getReturnType();
    }


    /**
     * 处理参数列表
     * @param paramTypes
     * @param genericParamTypes
     * @param paramAnns
     */
    private void processParameters(Class[] paramTypes, Type[] genericParamTypes, Annotation[][] paramAnns) {

        for (int i = 0; i < paramTypes.length; i++) {
            Class paramType = paramTypes[i];
            Annotation[] anns = paramAnns[i];
            MappingParameter parameter = new MappingParameter();
            parameter.setIndex(i);
            parameterTemplateArray[i] = parameter;
            if (OnSuccess.class.isAssignableFrom(paramType)) {
                onSuccessParameter = parameter;
                Type genType = genericParamTypes[i];
                onSuccessClassGenericType = getGenericClass(genType, 0);
            }
            else if (OnError.class.isAssignableFrom(paramType)) {
                onErrorParameter = parameter;
            }
            processParameterAnnotation(parameter, paramType, anns, i);
        }
    }

    /**
     * 处理参数的注解
     * @param parameter
     * @param paramType
     * @param anns
     */
    private void processParameterAnnotation(MappingParameter parameter, Class paramType, Annotation[] anns, int paramIndex) {
        for (int i = 0; i < anns.length; i++) {
            Annotation ann = anns[i];
            if (ann instanceof DataParam) {
                DataParam dataAnn = (DataParam) ann;
                String name = dataAnn.value();
                parameter.setName(name);
                namedParameters.add(parameter);
                MappingVariable variable = new MappingVariable(name, paramType);
                variable.setIndex(paramIndex);
                variables.put(dataAnn.value(), variable);
            }
            else if (ann instanceof DataVariable) {
                DataVariable dataAnn = (DataVariable) ann;
                String name = dataAnn.value();
                MappingVariable variable = new MappingVariable(name, paramType);
                variable.setIndex(paramIndex);
                variables.put(name, variable);
            }
            else if (ann instanceof DataObject) {
//                DataObject dataAnn = (DataObject) ann;
                String jsonParamName = ((DataObject) ann).jsonParam();
                boolean isJsonParam = StringUtils.isNotEmpty(jsonParamName);
                parameter.setObjectProperties(true);
                parameter.setJsonParam(isJsonParam);
                if (isJsonParam) {
                    parameter.setJsonParamName(jsonParamName);
                }
                namedParameters.add(parameter);
            }
        }
    }



    /**
     * 创建请求
     * @param args
     * @return
     */
    private ForestRequest makeRequest(Object[] args) {
        String renderedUrl = urlTemplate.render(args);
        String renderedType = typeTemplate.render(args);
        String renderedContentType = contentTypeTemplate.render(args).trim();
        String newUrl = "";
        List<RequestNameValue> nameValueList = new ArrayList<RequestNameValue>();
        String baseUrl = interfaceProxyHandler.getBaseURL();
        renderedUrl = URLUtils.getValidURL(baseUrl, renderedUrl);
        String query = "";
        try {
            URL u = new URL(renderedUrl);
            query = u.getQuery();
            if (StringUtils.isNotEmpty(query)) {
                String[] params = query.split("&");
                StringBuilder queryBuilder = new StringBuilder();
                if (params.length > 0) {
                    queryBuilder.append("?");
                }
                for (int i = 0; i < params.length; i++) {
                    String p = params[i];
                    String[] nameValue = p.split("=");
                    String name = nameValue[0];
                    queryBuilder.append(name);
                    RequestNameValue requestNameValue = new RequestNameValue(name, true);
                    nameValueList.add(requestNameValue);
                    if (nameValue.length > 1) {
                        String value = nameValue[1];
                        queryBuilder.append("=");
                        queryBuilder.append(value);
                        requestNameValue.setValue(value);
                    }
                    if (i < params.length - 1) {
                        queryBuilder.append("&");
                    }
                }
            }
            String protocol = u.getProtocol();
            int port = u.getPort();
            newUrl = protocol + "://" + u.getHost();
            if (port != 80 && port > -1) {
                newUrl += ":" + port;
            }
            String path = u.getPath();
            if (StringUtils.isNotEmpty(path)) {
                newUrl += path;
            }

        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        }

        for (int i = 0; i < namedParameters.size(); i++) {
            MappingParameter parameter = namedParameters.get(i);
            if (parameter.isObjectProperties()) {
                Object obj = args[parameter.getIndex()];
                if (parameter.isJsonParam()) {
                    String  json = "";
                    if (obj != null) {
                        ForestJsonConverter jsonConverter = configuration.getJsonCoverter();
                        json = jsonConverter.convertToJson(obj);
                    }
                    nameValueList.add(new RequestNameValue(parameter.getJsonParamName(), json, false));
                }
                else {
                    try {
                        List<RequestNameValue> list = getNameValueListFromObject(obj);
                        nameValueList.addAll(list);
                    } catch (InvocationTargetException e) {
                        throw new ForestRuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new ForestRuntimeException(e);
                    }
                }
            }
            else if (parameter.getIndex() != null) {
                RequestNameValue nameValue = new RequestNameValue(parameter.getName(), false);
                Object val = args[parameter.getIndex()];
                if (val != null) {
                    nameValue.setValue(String.valueOf(val));
                    nameValueList.add(nameValue);
                }
            }
        }

        // create and initialize http instance
        ForestRequest<T> request = new ForestRequest(configuration);
        request.setUrl(newUrl)
                .setQuery(query)
                .setType(renderedType)
                .setContentType(renderedContentType)
                .setArguments(args)
                .setLogEnable(logEnable)
                .setAsync(async);
        if (configuration.getDefaultParameters() != null) {
            request.addData(configuration.getDefaultParameters());
        }
        if (configuration.getDefaultHeaders() != null) {
            request.addHeaders(configuration.getDefaultHeaders());
        }

        List<RequestNameValue> dataNameValueList = new ArrayList<>();
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 0; i < dataTemplateArray.length; i++) {
            MappingTemplate dataTemplate = dataTemplateArray[i];
            String data = dataTemplate.render(args);
            bodyBuilder.append(data);
            if (i < dataTemplateArray.length - 1) {
                bodyBuilder.append("&");
            }
            String[] paramArray = data.split("&");
            for (int j = 0; j < paramArray.length; j++) {
                String dataParam = paramArray[j];
                String[] dataNameValue = dataParam.split("=");
                if (dataNameValue.length > 0) {
                    String name = dataNameValue[0].trim();
                    RequestNameValue nameValue = new RequestNameValue(name, false);
                    if (dataNameValue.length == 2) {
                        nameValue.setValue(dataNameValue[1].trim());
                    }
                    nameValueList.add(nameValue);
                    dataNameValueList.add(nameValue);
                }
            }
        }
        request.addData(nameValueList);
        if (bodyBuilder.length() > 0) {
            String requestBody = bodyBuilder.toString();
            request.setRequestBody(requestBody);
        }

        for (int i = 0; i < headerTemplateArray.length; i++) {
            MappingTemplate headerTemplate = headerTemplateArray[i];
            String header = headerTemplate.render(args);
            String[] headNameValue = header.split(":");
            if (headNameValue.length > 0) {
                String name = headNameValue[0].trim();
                RequestNameValue nameValue = new RequestNameValue(name, false);
                if (headNameValue.length == 2) {
                    nameValue.setValue(headNameValue[1].trim());
                }
                request.addHeader(nameValue);
            }
        }


        if (timeout != null) {
            request.setTimeout(timeout);
        }
        else if (configuration.getTimeout() != null) {
            request.setTimeout(configuration.getTimeout());
        }

        if (retryNumber != null) {
            request.setRetryCount(retryNumber);
        }
        else if (configuration.getRetryCount() != null) {
            request.setRetryCount(configuration.getRetryCount());
        }

        if (onSuccessParameter != null) {
            OnSuccess<?> onSuccessCallback = (OnSuccess<?>) args[onSuccessParameter.getIndex()];
            request.setOnSuccess(onSuccessCallback);
        }
        if (onErrorParameter != null) {
            OnError onErrorCallback = (OnError) args[onErrorParameter.getIndex()];
            request.setOnError(onErrorCallback);
        }

        String dataType = dataTypeTemplate.render(args);
        if (StringUtils.isEmpty(dataType)) {
            request.setDataType(ForestDataType.TEXT);
        }
        else {
            dataType = dataType.toUpperCase();
            ForestDataType forestDataType = ForestDataType.valueOf(dataType);
            request.setDataType(forestDataType);
        }

        if (interceptorList != null && interceptorList.size() > 0) {
            for (Interceptor item : interceptorList) {
                request.addInterceptor(item);
            }
        }

        return request;
    }



    /**
     * 从对象中获取键值对列表
     * @param obj
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private List<RequestNameValue> getNameValueListFromObject(Object obj) throws InvocationTargetException, IllegalAccessException {
        Class clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        List<RequestNameValue> nameValueList = new ArrayList<RequestNameValue>();
        for (int i = 0; i < methods.length; i++) {
            Method mtd = methods[i];
            String getterName = StringUtils.getGetterName(mtd);
            if (getterName == null) {
                continue;
            }
            Method getter = mtd;
            Object value = getter.invoke(obj);
            if (value != null) {
                RequestNameValue nameValue = new RequestNameValue(getterName, value, false);
                nameValueList.add(nameValue);
            }

        }
        return nameValueList;
    }




    /**
     * 调用方法
     * @param args
     * @return
     */
    public Object invoke(Object[] args) {
        ForestRequest request = makeRequest(args);
        MethodResponseHandler<T> responseHandler = new MethodResponseHandler<>(
                this, onSuccessClassGenericType);
        request.execute(configuration.getExecutorFactory(), responseHandler);
        return responseHandler.getResultData();
    }


    /**
     * 获取泛型类型
     * @param genType
     * @param index
     * @return
     */
    private static Class getGenericClass(Type genType, final int index) {

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }


    public Type getReturnType() {
        Type type = method.getGenericReturnType();
        return type;
    }


}
