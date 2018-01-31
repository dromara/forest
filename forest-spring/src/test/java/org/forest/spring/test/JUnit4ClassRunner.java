package org.forest.spring.test;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 15:35
 */
public class JUnit4ClassRunner extends BlockJUnit4ClassRunner {

    static {
        String path = JUnit4ClassRunner.class.getResource("/").getFile();
        File config = new File(path + "log4j2.xml");
        ConfigurationSource source = null;
        try {
            source = new ConfigurationSource(new FileInputStream(config), config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Configurator.initialize(null, source);
    }

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the misc class is malformed.
     */
    public JUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
}
