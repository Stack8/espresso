package com.ziro.espresso.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class PropertiesBuilderTest {

    @Test
    void whenLoadingMultiplePropertiesThenOverridesOccurInOrderOfLoad() {
        Properties properties = new PropertiesBuilder()
                .load(Map.of("test.prop.username", "admin", "test.prop.password", "admin"))
                .load("properties-builder-test.properties")
                .load(Map.of("test.prop.username", "arotondo"))
                .build();

        assertThat(properties)
                .isNotNull()
                .isNotEmpty()
                .containsEntry("test.prop.username", "arotondo") // from last load using map
                .containsEntry("test.prop.password", "test-admin-password"); // from load using properties file
    }

    @Test
    void whenLoadingPropertiesFromNonExistentFileThenUsingLoadOptionalIgnoresFileNotFoundError() {
        Properties properties = new PropertiesBuilder()
                .load("properties-builder-test.properties")
                .loadOptional("some-random-non-existent.properties")
                .build();

        assertThat(properties).isNotNull().isNotEmpty()
                .containsEntry("test.prop.username", "test-admin")
                .containsEntry("test.prop.password", "test-admin-password");
    }

    @Test
    void whenLoadingPropertiesFromNonExistentFileThenUsingLoadOptionalResultsInEmptyProperties() {
        Properties properties = new PropertiesBuilder()
                .loadOptional("some-random-non-existent.properties")
                .build();

        assertThat(properties).isNotNull().isEmpty();
    }
}
