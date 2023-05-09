package org.dromara.forest.solon.integration;

import org.dromara.forest.Forest;
import org.dromara.forest.annotation.BindingVar;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.config.SolonForestProperties;
import org.dromara.forest.file.SolonMultipartFile;
import org.dromara.forest.http.body.SolonUploadRequestBodyBuilder;
import org.dromara.forest.http.body.RequestBodyBuilder;
import org.dromara.forest.interceptor.SolonInterceptorFactory;
import org.dromara.forest.multipart.ForestMultipartFactory;
import org.dromara.forest.reflection.SolonObjectFactory;
import org.dromara.forest.solon.ForestBeanBuilder;
import org.dromara.forest.solon.SolonForestVariableValue;
import org.dromara.forest.solon.SolonUpstreamInterceptor;
import org.dromara.forest.solon.properties.ForestConfigurationProperties;
import org.dromara.forest.utils.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.UploadedFile;

import java.util.Arrays;

/**
 * @author 夜の孤城
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //1.初始 ForestConfiguration
        ForestConfiguration configuration = configBeanInit(context);

        //2.注册上传类型
        ForestMultipartFactory.registerFactory(UploadedFile.class, SolonMultipartFile.class);
        RequestBodyBuilder.registerBodyBuilder(UploadedFile.class, new SolonUploadRequestBodyBuilder());

        //3.添加 ForestClient 注解支持
        context.beanBuilderAdd(ForestClient.class, (clz, wrap, anno) -> {
            Object client = configuration.client(clz);
            wrap.context().wrapAndPut(clz, client);
        });

        //4.添加 BindingVar 注解支持
        context.beanExtractorAdd(BindingVar.class, (bw, method, anno) -> {
            String confId = anno.configuration();
            ForestConfiguration config = null;

            if (StringUtils.isNotBlank(confId)) {
                config = Forest.config(confId);
            } else {
                config = configuration;
            }

            String varName = anno.value();
            SolonForestVariableValue variableValue = new SolonForestVariableValue(bw.get(), method);
            config.setVariableValue(varName, variableValue);
        });


    }

    private ForestConfiguration configBeanInit(AopContext context) {
        Props forestProps = context.cfg().getProp("forest");
        ForestConfigurationProperties configurationProperties = new ForestConfigurationProperties();
        Utils.injectProperties(configurationProperties, forestProps);

        ForestBeanBuilder forestBeanBuilder = new ForestBeanBuilder(
                configurationProperties,
                new SolonForestProperties(context),
                new SolonObjectFactory(context),
                new SolonInterceptorFactory(context));

        //1.构建配转走
        ForestConfiguration config = forestBeanBuilder.build();

        //2.添加必要拦截器
        if (config.getInterceptors() != null) {
            config.getInterceptors().add(SolonUpstreamInterceptor.class);
        } else {
            config.setInterceptors(Arrays.asList(SolonUpstreamInterceptor.class));
        }

        //3.注册到容器
        String beanId = configurationProperties.getBeanId();
        if (Utils.isEmpty(beanId)) {
            beanId = "forestConfiguration";
        }

        BeanWrap beanWrap = context.wrap(beanId, config);
        context.putWrap(ForestConfiguration.class, beanWrap);
        context.putWrap(beanId, beanWrap);

        return config;
    }
}
