package kr.kdev.demo.velocity;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.spring.VelocityEngineFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class VelocityConfig {

    static {
        Velocity.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
    }

    @Bean
    public VelocityEngineFactoryBean velocityEngineFactoryBean() {
        Properties properties = new Properties();
        properties.put("resource.loader.string.class", StringResourceLoader.class.getName());
        properties.put(RuntimeConstants.RESOURCE_LOADERS, "string");

        VelocityEngineFactoryBean factoryBean = new VelocityEngineFactoryBean();
        factoryBean.setVelocityProperties(properties);
        return factoryBean;
    }
}
