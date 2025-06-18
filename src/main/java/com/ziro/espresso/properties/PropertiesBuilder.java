package com.ziro.espresso.properties;

import com.google.common.io.Resources;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * A builder class for loading and aggregating properties from various sources.
 * Provides a fluent interface for loading properties from files, URLs, and maps.
 * Failures during property loading are wrapped in runtime exceptions for simplified error handling.
 *
 * <p>Example usage:
 * <pre>{@code
 * Properties props = new PropertiesBuilder()
 *     .load("config.properties")
 *     .loadOptional("optional-config.properties")
 *     .build();
 * }</pre>
 */
public class PropertiesBuilder {

    private final Properties properties;

    /**
     * Creates a new PropertiesBuilder instance with an empty properties collection.
     */
    public PropertiesBuilder() {
        properties = new Properties();
    }

    /**
     * Loads properties from a Map into the builder.
     *
     * @param props the map containing properties to load
     * @return this builder instance for method chaining
     */
    public PropertiesBuilder load(Map<Object, Object> props) {
        properties.putAll(props);
        return this;
    }

    /**
     * Loads properties from a resource file in the classpath.
     *
     * @param propertiesFileName the name of the properties file to load
     * @return this builder instance for method chaining
     * @throws SystemUnhandledException if the file cannot be found or loaded
     */
    public PropertiesBuilder load(String propertiesFileName) {
        URL resourceUrl = Resources.getResource(propertiesFileName);
        return load(resourceUrl);
    }

    /**
     * Loads properties from a File.
     *
     * @param propertiesFile the properties file to load
     * @return this builder instance for method chaining
     * @throws SystemUnhandledException if the file cannot be converted to URL or loaded
     */
    public PropertiesBuilder load(File propertiesFile) {
        return load(toUrl(propertiesFile));
    }

    /**
     * Attempts to load properties from a resource file, ignoring if the file is not found.
     * This method is useful for loading optional configuration files that may or may not exist.
     *
     * @param optionalPropertiesFileName the name of the optional properties file to load
     * @return this builder instance for method chaining
     */
    public PropertiesBuilder loadOptional(String optionalPropertiesFileName) {
        getOptionalResourceByFileName(optionalPropertiesFileName).ifPresent(this::load);
        return this;
    }

    /**
     * Attempts to load properties from a File, ignoring if the file does not exist.
     * This method is useful for loading optional configuration files that may or may not exist.
     *
     * @param optionalPropertiesFile the optional properties file to load
     * @return this builder instance for method chaining
     * @throws SystemUnhandledException if an existing file cannot be converted to URL or loaded
     */
    public PropertiesBuilder loadOptional(File optionalPropertiesFile) {
        if (optionalPropertiesFile.exists()) {
            return load(toUrl(optionalPropertiesFile));
        }
        return this;
    }

    /**
     * Creates a new Properties instance containing all properties loaded into this builder.
     * The returned Properties instance is independent of the builder's internal properties.
     *
     * @return a new Properties instance containing all loaded properties
     */
    public Properties build() {
        Properties newProps = new Properties();
        newProps.putAll(properties);
        return newProps;
    }

    /**
     * Loads properties from a URL resource.
     *
     * @param url the URL of the properties resource to load
     * @return this builder instance for method chaining
     * @throws SystemUnhandledException if the properties cannot be loaded from the URL
     */
    private PropertiesBuilder load(URL url) {
        try (InputStream in = url.openStream()) {
            properties.load(in);
        } catch (Exception e) {
            throw SystemUnhandledException.withCause(e)
                    .message("Something went wrong while trying to load properties from [url=%s]", url)
                    .exception();
        }
        return this;
    }

    /**
     * Attempts to get a resource URL by its file name.
     *
     * @param fileName the name of the resource file
     * @return an Optional containing the resource URL if found, empty Optional otherwise
     */
    private static Optional<URL> getOptionalResourceByFileName(String fileName) {
        try {
            return Optional.of(Resources.getResource(fileName));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Converts a File to a URL.
     *
     * @param file the file to convert
     * @return the URL representation of the file
     * @throws SystemUnhandledException if the file cannot be converted to a URL
     */
    private static URL toUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw SystemUnhandledException.withCause(e)
                    .message("Something went wrong while trying to convert file [name=%s] to URL.", file.getName())
                    .exception();
        }
    }
}
