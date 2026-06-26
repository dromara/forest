package com.dtflys.forest.springboot3.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.dtflys.forest.springboot.annotation.ForestScannerRegister;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertFalse;

public class ForestAutoProxyWarningTest extends BaseSpringBootTest {

    @Test
    public void testNoBeanPostProcessorCheckerWarning() {
        Logger logger = (Logger) LoggerFactory.getLogger(
                "org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker");
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        Level level = logger.getLevel();
        logger.setLevel(Level.INFO);

        try (ConfigurableApplicationContext ignored = new SpringApplicationBuilder(TestApplication.class)
                .web(WebApplicationType.NONE)
                .listeners(event -> {
                    if (event instanceof ApplicationFailedEvent) {
                        ForestScannerRegister.cleanBackPackages();
                    }
                })
                .run("--spring.main.banner-mode=off")) {
            assertFalse(appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .anyMatch(message -> message.contains("not eligible for getting processed by all BeanPostProcessors")
                            && message.contains("forestBeanProcessor")));
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(level);
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @AutoConfigurationPackage
    static class TestApplication {
    }
}
