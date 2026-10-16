package qa.ap.rest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private TestConfig() {
    }

    public static String baseUri() {
        return System.getProperty(
                "base.uri",
                System.getenv().getOrDefault("BASE_URI", PROPERTIES.getProperty("base.uri"))
        );
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load configuration file: " + CONFIG_FILE, exception);
        }
    }
}
