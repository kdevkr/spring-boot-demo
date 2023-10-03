package kr.kdev.demo.i18n;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class XmlResourceBundleMessageSource extends ResourceBundleMessageSource {
    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle(basename, locale, XmlResourceBundle.Control.INSTANCE);
    }

    public static class XmlResourceBundle extends ResourceBundle {

        private final Properties properties;

        public XmlResourceBundle(InputStream stream) throws IOException {
            properties = new Properties();
            properties.loadFromXML(stream);
        }

        @Override
        protected Object handleGetObject(String key) {
            return properties.getProperty(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            Set<String> handleKeys = properties.stringPropertyNames();
            return Collections.enumeration(handleKeys);
        }

        public static class Control extends ResourceBundle.Control {
            public static final XmlResourceBundle.Control INSTANCE = new XmlResourceBundle.Control();

            private Control() {
            }

            @Override
            public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
                if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
                    throw new IllegalArgumentException("baseName, locale, format and loader cannot be null");
                }
                if (!"xml".equals(format)) {
                    throw new IllegalArgumentException("format must be xml");
                }

                final String bundleName = toBundleName(baseName, locale);
                final String resourceName = toResourceName(bundleName, format);
                final URL url = loader.getResource(resourceName);
                if (url == null) {
                    return null;
                }

                final URLConnection urlconnection = url.openConnection();
                if (urlconnection == null) {
                    return null;
                }

                if (reload) {
                    urlconnection.setUseCaches(false);
                }

                try (final InputStream stream = urlconnection.getInputStream();
                     final BufferedInputStream bis = new BufferedInputStream(stream)) {
                    return new XmlResourceBundle(bis);
                }
            }

            @Override
            public List<String> getFormats(String baseName) {
                return List.of("xml");
            }
        }
    }
}
