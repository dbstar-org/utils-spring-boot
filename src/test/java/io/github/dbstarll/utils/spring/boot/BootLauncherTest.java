package io.github.dbstarll.utils.spring.boot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BootLauncherTest {
    @AfterEach
    void tearDown() {
        System.clearProperty("test-launcher.config");
        System.clearProperty("test-launcher-bootstrap.config");
    }

    @Test
    void test() {
        try (final ConfigurableApplicationContext ctx = new BootLauncher() {
        }.run("test", "test-launcher")) {
            assertEquals("true", ctx.getEnvironment().getProperty("test.enable"));
            assertEquals("true", ctx.getEnvironment().getProperty("test-launcher.enable"));
            assertNull(ctx.getEnvironment().getProperty("test2.enable"));
            assertNull(ctx.getEnvironment().getProperty("test-launcher2.enable"));
        }
    }

    @Test
    void test2() {
        try (final ConfigurableApplicationContext ctx = new BootLauncher() {
        }.run("test2", "test-launcher2")) {
            assertEquals("true", ctx.getEnvironment().getProperty("test2.enable"));
            assertEquals("true", ctx.getEnvironment().getProperty("test-launcher2.enable"));
            assertNull(ctx.getEnvironment().getProperty("test.enable"));
            assertNull(ctx.getEnvironment().getProperty("test-launcher.enable"));
        }
    }

    @Test
    void nullGroupId() {
        try (final ConfigurableApplicationContext ctx = new BootLauncher() {
        }.run(null, "test-launcher")) {
            assertEquals("true", ctx.getEnvironment().getProperty("test-launcher.enable"));
            assertNull(ctx.getEnvironment().getProperty("test.enable"));
            assertNull(ctx.getEnvironment().getProperty("test2.enable"));
            assertNull(ctx.getEnvironment().getProperty("test-launcher2.enable"));
        }
    }

    @Test
    void same() {
        try (final ConfigurableApplicationContext ctx = new BootLauncher() {
        }.run("test-launcher", "test-launcher")) {
            assertEquals("true", ctx.getEnvironment().getProperty("test-launcher.enable"));
            assertNull(ctx.getEnvironment().getProperty("test.enable"));
            assertNull(ctx.getEnvironment().getProperty("test2.enable"));
            assertNull(ctx.getEnvironment().getProperty("test-launcher2.enable"));
        }
    }

    @Test
    void config() {
        System.setProperty("test-launcher.config", "optional:classpath:/etc/");
        try (final ConfigurableApplicationContext ctx = new BootLauncher() {
        }.run("test", "test-launcher")) {
            assertEquals("false", ctx.getEnvironment().getProperty("test.enable"));
            assertEquals("true", ctx.getEnvironment().getProperty("test-launcher.enable"));
            assertNull(ctx.getEnvironment().getProperty("test2.enable"));
            assertNull(ctx.getEnvironment().getProperty("test-launcher2.enable"));
        }
    }
}