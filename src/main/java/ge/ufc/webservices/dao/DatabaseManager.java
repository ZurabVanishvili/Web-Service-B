package ge.ufc.webservices.dao;

import ge.ufc.webservices.DatabaseException;
import ge.ufc.webservices.DatabaseException_Exception;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger();

    private static final String MOVIE_TOMCAT_DS = "java:comp/env/jdbc/agentDs";

    public static Connection getDatabaseConnection() throws ge.ufc.webservices.dao.DatabaseException {
        return getConnection();
    }


    private static Connection getConnection() throws  ge.ufc.webservices.dao.DatabaseException {
        try {
            DataSource ds = getDataSource(MOVIE_TOMCAT_DS);
            return ds.getConnection();
        } catch (NamingException e) {
            throw new ge.ufc.webservices.dao.DatabaseException("Database exception",e);
        } catch (SQLException e) {
            throw new ge.ufc.webservices.dao.DatabaseException("Unable to connect database",e);
        }
    }

    private static DataSource getDataSource(String jndiName) throws NamingException {
        Context initCtx = new InitialContext();
        return (DataSource) initCtx.lookup(jndiName);
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.warn("Unable to close connection", e);
            }
        }
    }
}
