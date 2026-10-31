package com.sdet.booker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config/{env}.properties from the classpath.
 * Resolution order (highest wins): JVM system property -> environment variable -> properties file.
 * e.g. -DbaseUrl=... or BASE_URL=... overrides base.url.
 */
public final class Config {

    private static final Properties PROPS = load();

    private Config() {
    }

    private static Properties load() {
        String env = System.getProperty("env", "qa");
        String path = "config/" + env + ".properties";
        Properties p = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing config file on classpath: " + path);
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
        return p;
    }

    public static String get(String key) {
        String camel = toCamel(key);
        String sys = System.getProperty(camel);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }
        String envVar = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envVar != null && !envVar.isBlank()) {
            return envVar;
        }
        String value = PROPS.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Config key not found: " + key);
        }
        return value;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String username() {
        return get("auth.username");
    }

    public static String password() {
        return get("auth.password");
    }

    public static int retryCount() {
        return getInt("retry.count");
    }

    private static String toCamel(String key) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : key.toCharArray()) {
            if (c == '.') {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }
}
