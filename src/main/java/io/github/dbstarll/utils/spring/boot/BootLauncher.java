package io.github.dbstarll.utils.spring.boot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

public abstract class BootLauncher {
    private static final String BOOTSTRAP_POSTFIX = "-bootstrap";

    protected final ConfigurableApplicationContext run(final String groupId, final String artifactId,
                                                       final String... args) throws IOException {
        return builder(builder(groupId, artifactId, getClass())).run(args);
    }

    /**
     * 重载此方法来配置SpringApplicationBuilder.
     *
     * @param builder SpringApplicationBuilder
     * @return 配置后的SpringApplicationBuilder
     */
    protected SpringApplicationBuilder builder(final SpringApplicationBuilder builder) {
        return builder;
    }

    private static SpringApplicationBuilder builder(final String groupId, final String artifactId,
                                                    final Class<?>... sources) throws IOException {
        System.setProperty("spring.config.name", getSpringConfigName(groupId, artifactId));
        System.setProperty("spring.config.location", getSpringConfigLocation(groupId, artifactId));
        System.setProperty("spring.cloud.bootstrap.name", getSpringCloudBootstrapName(groupId, artifactId));
        System.setProperty("spring.cloud.bootstrap.location", getSpringCloudBootstrapLocation(groupId, artifactId));
        return new SpringApplicationBuilder(sources).properties(hostnameProperties()).bannerMode(Mode.OFF);
    }

    private static String getSpringConfigName(final String groupId, final String artifactId) {
        return StringUtils.join(Arrays.asList(groupId, artifactId), ',');
    }

    private static String getSpringConfigLocation(final String groupId, final String artifactId) {
        final List<String> configs = new ArrayList<String>(3);
        configs.add("file:/etc/" + groupId + "/");
        configs.add("file:${user.home}/." + groupId + "/");
        if (System.getProperty(artifactId + ".config") != null) {
            configs.add("${" + artifactId + ".config}");
        }
        return StringUtils.join(configs, ',');
    }

    private static String getSpringCloudBootstrapName(final String groupId, final String artifactId) {
        return StringUtils.join(Arrays.asList(groupId + BOOTSTRAP_POSTFIX, artifactId + BOOTSTRAP_POSTFIX), ',');
    }

    private static String getSpringCloudBootstrapLocation(final String groupId, final String artifactId) {
        final List<String> configs = new ArrayList<String>(3);
        configs.add("file:/etc/" + groupId + "/");
        configs.add("file:${user.home}/." + groupId + "/");
        if (System.getProperty(artifactId + BOOTSTRAP_POSTFIX + ".config") != null) {
            configs.add("${" + artifactId + BOOTSTRAP_POSTFIX + ".config}");
        }
        return StringUtils.join(configs, ',');
    }

    private static Map<String, Object> hostnameProperties() throws UnknownHostException {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("localhost.hostname", Inet4Address.getLocalHost().getHostName());
        return properties;
    }
}
