package io.github.dbstarll.utils.spring.boot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notBlank;

public abstract class BootLauncher {
    private static final String BOOTSTRAP_POSTFIX = "-bootstrap";
    private static final String CONFIG_NAME = "spring.config.name";
    private static final String ADDITIONAL_LOCATION = "spring.config.additional-location";
    private static final String BOOTSTRAP_NAME = "spring.cloud.bootstrap.name";
    private static final String BOOTSTRAP_ADDITIONAL_LOCATION = "spring.cloud.bootstrap.additional-location";

    protected final ConfigurableApplicationContext run(final String groupId, final String artifactId,
                                                       final String... args) {
        final List<String> argList = config(groupId, artifactId).entrySet().stream()
                .map(e -> String.format("--%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        argList.addAll(Arrays.asList(args));
        return builder(new SpringApplicationBuilder(getClass())).run(argList.toArray(new String[0]));
    }

    /**
     * 重载此方法来配置SpringApplicationBuilder.
     *
     * @param builder SpringApplicationBuilder
     * @return 配置后的SpringApplicationBuilder
     */
    protected SpringApplicationBuilder builder(final SpringApplicationBuilder builder) {
        return builder.bannerMode(Mode.OFF);
    }

    private static Map<String, String> config(final String groupId, final String artifactId) {
        notBlank(artifactId, "artifactId is blank");
        final String finalGroupId = StringUtils.isBlank(groupId) || groupId.equals(artifactId) ? null : groupId;
        return new HashMap<String, String>() {{
            put(CONFIG_NAME, getConfigName(finalGroupId, artifactId));
            put(ADDITIONAL_LOCATION, getConfigLocation(finalGroupId, artifactId + ".config"));
            put(BOOTSTRAP_NAME, getConfigName(bootstrap(finalGroupId), bootstrap(artifactId)));
            put(BOOTSTRAP_ADDITIONAL_LOCATION, getConfigLocation(finalGroupId, bootstrap(artifactId) + ".config"));
        }};
    }

    private static String getConfigName(final String groupId, final String artifactId) {
        return groupId == null ? artifactId : String.format("%s,%s", groupId, artifactId);
    }

    private static String getConfigLocation(final String groupId, final String propertyConfig) {
        final List<String> configs = new ArrayList<>(3);
        if (groupId != null) {
            configs.add(String.format("optional:file:/etc/%s/", groupId));
            configs.add(String.format("optional:file:${user.home}/.%s/", groupId));
        }
        if (System.getProperty(propertyConfig) != null) {
            configs.add(String.format("${%s}", propertyConfig));
        }
        return StringUtils.join(configs, ',');
    }

    private static String bootstrap(final String name) {
        return name == null ? null : (name + BOOTSTRAP_POSTFIX);
    }
}
