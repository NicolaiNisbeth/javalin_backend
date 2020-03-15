package Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;

public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource hikari;

    static {
        //config.setJdbcUrl( "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185031?" );
        config.setJdbcUrl("jdbc:mysql://db.diplomportal.dk/s185031?");
        config.setUsername("s185031");
        config.setPassword("UfudYEA2p7RmipWZXxT2R");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari = new HikariDataSource(config);
    }

    private DataSource() {
    }

    public static HikariDataSource getHikari() throws SQLException {
        return hikari;
    }


}
