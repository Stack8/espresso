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
 * Loads properties quietly throwing runtime exceptions on failure.
 */
public class PropertiesBuilder {

    private final Properties properties;

    public PropertiesBuilder() {
        properties = new Properties();
    }

    public PropertiesBuilder load(Map<Object, Object> props) {
        properties.putAll(props);
        return this;
    }

    public PropertiesBuilder load(String propertiesFileName) {
        URL resourceUrl = Resources.getResource(propertiesFileName);
        return load(resourceUrl);
    }

    public PropertiesBuilder load(File propertiesFile) {
        return load(toUrl(propertiesFile));
    }

    /**
     * Loads specified optionalPropertiesFileName. If the provided optionalPropertiesFileName is not found, then
     * nothing is loaded and no exception is thrown.
     * @param optionalPropertiesFileName the optional properties file name to load
     * @return {@link PropertiesBuilder}
     */
    public PropertiesBuilder loadOptional(String optionalPropertiesFileName) {
        getOptionalResourceByFileName(optionalPropertiesFileName).ifPresent(this::load);
        return this;
    }

    public PropertiesBuilder loadOptional(File optionalPropertiesFile) {
        if (optionalPropertiesFile.exists()) {
            return load(toUrl(optionalPropertiesFile));
        }
        return this;
    }

    public Properties build() {
        Properties newProps = new Properties();
        newProps.putAll(properties);
        return newProps;
    }

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

    private static Optional<URL> getOptionalResourceByFileName(String fileName) {
        try {
            return Optional.of(Resources.getResource(fileName));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

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
