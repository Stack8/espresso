package com.ziro.espresso.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.io.Resources;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import org.assertj.core.api.Assertions;
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
    void whenLoadingFromMultiplePropertiesResourcesThenNonExistingOptionalResourcesAreIgnored() {
        File propertiesBuilderTestPropertiesFile =
                new File(Resources.getResource("properties-builder-test.properties").getFile());

        Properties properties = new PropertiesBuilder()
                .load(propertiesBuilderTestPropertiesFile)
                .loadOptional("some-random-non-existent.properties")
                .build();

        assertThat(properties)
                .isNotNull()
                .isNotEmpty()
                .containsEntry("test.prop.username", "test-admin")
                .containsEntry("test.prop.password", "test-admin-password");
    }

    @Test
    void whenLoadingFromOptionalNonExistingResourceThenNothingIsLoaded() {
        Properties properties = new PropertiesBuilder()
                .loadOptional(new File("some-random-non-existent.properties"))
                .build();

        assertThat(properties).isNotNull().isEmpty();
    }

    @Test
    void whenLoadingFromNonOptionalNonExistingResourceThenErrorIsThrown() {
        PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
        assertThatThrownBy(() -> propertiesBuilder.load("some-random-non-existent.properties"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("resource some-random-non-existent.properties not found.");
    }
}
