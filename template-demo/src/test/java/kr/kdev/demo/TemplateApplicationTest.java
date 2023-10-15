package kr.kdev.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.time.Instant;

@ActiveProfiles(profiles = {"test"})
@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
class TemplateApplicationTest {

    @BeforeTestClass
    void init() {
        log.info("init()");
    }

    @Test
    void contextLoads() {
        log.info("Run as {}", Instant.now());
    }
}
