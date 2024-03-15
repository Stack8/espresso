package com.ziro.espresso.properties;

import com.google.common.io.Resources;
import com.ziro.espresso.fluent.exceptions.SystemUnhandledException;
import com.ziro.espresso.javax.annotation.extensions.NonNullByDefault;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Loads properties quietly throwing runtime exceptions on failure.
 */
@NonNullByDefault
public class PropertiesBuilder {

    private final Properties properties;

    private PropertiesBuilder() {
        properties = new Properties();
    }

    public static PropertiesBuilder newBuilder() {
        return new PropertiesBuilder();
    }

    public PropertiesBuilder load(Map<Object, Object> props) {
        properties.putAll(props);
        return this;
    }

    public PropertiesBuilder load(URL url) {
        try (InputStream in = url.openStream()) {
            properties.load(in);
        } catch (Exception e) {
            throw SystemUnhandledException.fluent()
                    .message("Something went wrong while trying to load properties from [url=%s]", url)
                    .cause(e)
                    .exception();
        }
        return this;
    }

    public PropertiesBuilder load(String propertiesFileName) {
        load(toUrl(propertiesFileName));
        return this;
    }

    public PropertiesBuilder loadOptional(String optionalPropertiesFileName) {
        URL url;
        try {
            url = toUrl(optionalPropertiesFileName);
        } catch (Exception e) {
            // The file is optional, so if we fail to load it, just ignore the failure and continue.
            return this;
        }

        load(url);
        return this;
    }

    public Properties build() {
        Properties newProps = new Properties();
        newProps.putAll(properties);
        return newProps;
    }

    private static URL toUrl(String propertiesFileName) {
        try {
            //noinspection UnstableApiUsage
            return Resources.getResource(propertiesFileName);
        } catch (Exception e) {
            throw SystemUnhandledException.fluent()
                    .message(
                            "Something went wrong while trying to load resource with [fileName=%s] to a URL",
                            propertiesFileName
                    )
                    .cause(e)
                    .exception();
        }
    }
}
