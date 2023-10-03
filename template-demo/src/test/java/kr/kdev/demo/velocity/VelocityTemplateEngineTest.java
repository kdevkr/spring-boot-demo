package kr.kdev.demo.velocity;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.spring.VelocityEngineFactoryBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.StringWriter;

@Slf4j
@DisplayName("Velocity Template Engine Test")
@TestPropertySource(properties = {
        "logging.level.org.apache.velocity=DEBUG"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class VelocityTemplateEngineTest {

    @Autowired
    VelocityEngineFactoryBean factoryBean;

    @Autowired
    VelocityEngine velocityEngine;

    @DisplayName("Hello World")
    @Test
    void TestHelloWorld() {
        StringResourceRepository repository = StringResourceLoader.getRepository();
        repository.putStringResource("simple", "Hello $w");

        VelocityContext context = new VelocityContext();
        context.put("w", "World!");

        Template template = velocityEngine.getTemplate("simple");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        String text = writer.toString();
        Assertions.assertEquals("Hello World!", text);
    }
}
