package com.epiphyllum.h2tool;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

/**
 * Created by hary on 15/11/17.
 */
public class H2Tool {
    private DBI dbi;
    private Flyway flyway;

    /**
     * @param dbNameOrJdbcUrl  : 如果给的是数据库名称， 那么默认用h2 memdb
     * @param migrationDir
     */
    private void H2Tool(String dbNameOrJdbcUrl, String migrationDir) {

        String url;
        String[] parts = dbNameOrJdbcUrl.split(":");
        if (parts[0].equals("jdbc")) {
            url = dbNameOrJdbcUrl;
        } else {
            url = "jdbc:h2:mem:" + dbNameOrJdbcUrl + ";DB_CLOSE_DELAY=-1";
        }
        JdbcDataSource dataSource = createDataSource(url);

        // 实时数据迁移
        migrate(dataSource, migrationDir);

        dbi = new DBI(dataSource);
    }

    /**
     *
     */

    /**
     * 拿到DAO
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T create(Class<? extends T> clazz) {
        return dbi.onDemand(clazz);
    }

    /**
     *
     */
    public void clean() {
        flyway.clean();
    }

    // h2内存数据库URL  -- "jdbc:h2:mem:phonebook;DB_CLOSE_DELAY=-1"

    /**
     *
     * @param url - URL
     *            - "jdbc:h2:mem:phonebook;DB_CLOSE_DELAY=-1"  : 内存数据库
     *            - "jdbc:h2:file:~/test"                      : 文件数据库
     *            - "jdbc:h2:tcp://localhost/~/test            :
     * @return
     */
    private JdbcDataSource createDataSource(String url) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(url);
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    /**
     *
     * @param dataSource
     * @param classpathMigrationDir
     *        目录中包含了一堆的V1_1_2__descripton_of_your_migration.sql
     *        migration将安装版本号不断执行
     */
    private void migrate(DataSource dataSource, String classpathMigrationDir) {
        flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations(classpathMigrationDir);
        flyway.migrate();
    }
}
