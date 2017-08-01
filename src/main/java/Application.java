import annotations.DatabasePassword;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import config.DatabaseConfig;
import controllers.AuthenticationController;
import controllers.IndexController;
import utils.ConnectionProvider;
import utils.IOUtils;
import utils.ProductionConnectionProvider;

import java.io.IOException;
import java.util.Properties;

import static spark.Spark.port;
import static spark.Spark.staticFiles;

/**
 * Created by EvanKing on 7/19/17.
 */
public class Application {
    public static boolean run(String propertiesFilePath) {

        if (!handleArgs(propertiesFilePath)) {
            return false;
        }

        Injector injector = Guice.createInjector(new Module());
        injector.getInstance(IndexController.class).init();
        injector.getInstance(AuthenticationController.class).init();
        injector.getInstance(DatabaseConfig.class).dbInit();

        return true;
    }

    private static boolean handleArgs(String propertiesFilePath){
        Properties serverProp = IOUtils.getPropertyFileObject(propertiesFilePath);

        int serverPort;
        try {
            serverPort = Integer.parseInt(IOUtils.getPropertyFromPropertiesFile(serverProp, "serverPort"));
            Module.DB_PASSWORD = IOUtils.getPropertyFromPropertiesFile(serverProp, "databasePassword");
        } catch (IOException | NumberFormatException e) {
            System.err.printf(String.format("Error: %s", e.getMessage()));
            return false;
        }

        configureSpark(serverPort);
        return true;
    }

    private static void configureSpark(int serverPort){
        // Configure Spark on port `port`
        port(serverPort);
        // Static files location
        staticFiles.location("/public");
        // Caching of static files lifetime
        staticFiles.expireTime(600L);
    }

    private static class Module extends AbstractModule {
        private static String DB_PASSWORD = null;

        @Override
        protected void configure() {
            bind(ConnectionProvider.class).to(ProductionConnectionProvider.class);

            if (DB_PASSWORD == null) {
                throw new IllegalStateException("DB_PASSWORD not initialized");
            }
            bind(String.class).annotatedWith(DatabasePassword.class).toInstance(DB_PASSWORD);
        }
    }
}
