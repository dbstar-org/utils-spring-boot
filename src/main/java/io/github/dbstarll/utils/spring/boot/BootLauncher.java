package io.github.dbstarll.utils.spring.boot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BootLauncher {
    private static final String BOOTSTRAP_POSTFIX = "-bootstrap";
    private static final String ADDITIONAL_LOCATION = "spring.config.additional-location";
    private static final String BOOTSTRAP_ADDITIONAL_LOCATION = "spring.cloud.bootstrap.additional-location";

    protected final ConfigurableApplicationContext run(final String groupId, final String artifactId,
                                                       final String... args) {
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
                                                    final Class<?>... sources) {
        System.setProperty("spring.config.name", getSpringConfigName(groupId, artifactId));
        System.setProperty(ADDITIONAL_LOCATION, getSpringConfigLocation(groupId, artifactId));
        System.setProperty("spring.cloud.bootstrap.name", getSpringCloudBootstrapName(groupId, artifactId));
        System.setProperty(BOOTSTRAP_ADDITIONAL_LOCATION, getSpringCloudBootstrapLocation(groupId, artifactId));
        return new SpringApplicationBuilder(sources).bannerMode(Mode.OFF);
    }

    private static String getSpringConfigName(final String groupId, final String artifactId) {
        return StringUtils.join(Arrays.asList(groupId, artifactId), ',');
    }

    private static String getSpringConfigLocation(final String groupId, final String artifactId) {
        final List<String> configs = new ArrayList<>(3);
        configs.add("optional:file:/etc/" + groupId + "/");
        configs.add("optional:file:${user.home}/." + groupId + "/");
        if (System.getProperty(artifactId + ".config") != null) {
            configs.add("${" + artifactId + ".config}");
        }
        return StringUtils.join(configs, ',');
    }

    private static String getSpringCloudBootstrapName(final String groupId, final String artifactId) {
        return StringUtils.join(Arrays.asList(groupId + BOOTSTRAP_POSTFIX, artifactId + BOOTSTRAP_POSTFIX), ',');
    }

    private static String getSpringCloudBootstrapLocation(final String groupId, final String artifactId) {
        final List<String> configs = new ArrayList<>(3);
        configs.add("optional:file:/etc/" + groupId + "/");
        configs.add("optional:file:${user.home}/." + groupId + "/");
        if (System.getProperty(artifactId + BOOTSTRAP_POSTFIX + ".config") != null) {
            configs.add("${" + artifactId + BOOTSTRAP_POSTFIX + ".config}");
        }
        return StringUtils.join(configs, ',');
    }
}
