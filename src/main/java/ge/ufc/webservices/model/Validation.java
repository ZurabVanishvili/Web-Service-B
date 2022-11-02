package ge.ufc.webservices.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;


public class Validation {

    private static final Logger lgg = LogManager.getLogger();
    private static final String CONFIG_FILE_LOCATION = "config.properties";
    private volatile static Validation validation = null;
    private static URL url = null;
    protected long lastModified;

    private Agent agent;

    private Validation(URLConnection conn) {
        this.lastModified = conn.getLastModified();
        try {
            Properties props = new Properties();
            props.load(conn.getInputStream());
            lgg.info("configuration reloaded");
            fillSetting(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Validation getConfiguration() {
        try {
            if (url == null) {
                url = Validation.class.getClassLoader().getResource(CONFIG_FILE_LOCATION);
            }
            assert url != null;
            URLConnection connection = url.openConnection();

            if (url == null) {
                lgg.error("Configuration file not found");
                throw new IOException("Configuration file not found");
            }


            long lastModified = connection.getLastModified();
            if (validation == null || lastModified > validation.lastModified) {
                synchronized (CONFIG_FILE_LOCATION) {
                    if (validation == null || lastModified > validation.lastModified) {
                        validation = new Validation(connection);
                    }
                }
            }
        } catch (IOException e) {
            lgg.fatal(e);
        }

        return validation;

    }

    public void fillSetting(Properties props) {
        agent = new Agent();
        agent.setId(props.getProperty("id",""));
        agent.setPassword(props.getProperty("password",""));
        agent.setUrl(props.getProperty("url",""));
        agent.setTimeout(props.getProperty("timeout",""));
    }


    public Agent getAgent() {
        return agent;
    }
}

