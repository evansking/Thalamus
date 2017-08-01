package utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.dbcp.BasicDataSource;
import annotations.DatabasePassword;

@Singleton
public final class ProductionConnectionProvider extends PooledConnectionProvider {
    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dbHost = "localhost";
    private static final String dbUser = "root";

    private final String dbPassword;

    @Inject
    public ProductionConnectionProvider(@DatabasePassword String dbPassword) {
        super();
        this.dbPassword = dbPassword;
        configure();
    }

    @Override
    protected void configure(BasicDataSource dataSource) {
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setUrl(String.format("jdbc:mysql://%s/%s", dbHost, Statements.DB_NAME));

        dataSource.setMinIdle(5);
    }
}
