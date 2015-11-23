package com.epiphyllum.h2tool;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by hary on 15/11/17.
 */
public class H2Tool {
    private DBI dbi;
    private Flyway flyway;

    /**
     * @param dbNameOrJdbcUrl : 如果给的是数据库名称， 那么默认用h2 memdb
     * @param migrationDir
     */
    public H2Tool(String dbNameOrJdbcUrl, String migrationDir) {

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
     * 从文件恢复数据库
     * @param zipPath 备份好的数据库文件
     */
    public H2Tool(String zipPath) {
        String url = "jdbc:h2:mem:epiphyllum;DB_CLOSE_DELAY=-1";
        JdbcDataSource dataSource = createDataSource(url);
        dbi = new DBI(dataSource);
        Handle handle = dbi.open();
        Update update = handle.createStatement(String.format("RUNSCRIPT FROM '%s' COMPRESSION GZIP", zipPath));
        update.execute();
    }


    public void testRestore() {

        class Contact {
            public String firstName;
            public String lastName;
            public String phone;

            @Override
            public String toString() {
                return "Contact{" +
                        "firstName='" + firstName + '\'' +
                        ", lastName='" + lastName + '\'' +
                        ", phone='" + phone + '\'' +
                        '}';
            }

            public Contact(String firstName, String lastName, String phone) {
                this.firstName = firstName;
                this.lastName = lastName;
                this.phone = phone;
            }
        }

        Handle handle = dbi.open();

        Iterator<Contact> it = handle.createQuery("select * from contact").map(new ResultSetMapper<Contact>() {
            @Override
            public Contact map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                return new Contact(
                        r.getString("firstName"),
                        r.getString("lastName"),
                        r.getString("phone")
                        );
            }

        }).iterator();

        while (it.hasNext()) {
            Contact c = it.next();
            System.out.println("get contact - " + c);
        }
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
    public <T> T getDAO(Class<? extends T> clazz) {
        return dbi.onDemand(clazz);
    }

    public DBI getDBI() {
        return dbi;
    }

    /**
     *
     */
    public void clean() {
        flyway.clean();
    }

    // h2内存数据库URL  -- "jdbc:h2:mem:phonebook;DB_CLOSE_DELAY=-1"

    /**
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
     * @param dataSource
     * @param classpathMigrationDir 目录中包含了一堆的V1_1_2__descripton_of_your_migration.sql
     *                              migration将安装版本号不断执行
     */
    private void migrate(DataSource dataSource, String classpathMigrationDir) {
        flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations(classpathMigrationDir);
        flyway.migrate();
    }

    /**
     * h2 db的备份与恢复脚本:
     * <table>
     *  <tr>
     *     <td>备份</td>
     *     <td>SCRIPT TO 'file.gz' COMPRESSION GZIP;</td>
     *  </tr>
     *  <tr>
     *     <td>还原</td>
     *     <td>SCRIPT FROM 'file.gz' COMPRESSION GZIP;</td>
     *  </tr>
     * </table>
     * </p>
     *
     * @param zipPath 生成的备份文件的名称
     * @throws SQLException
     */
    public void backup(String zipPath) throws SQLException {
        Handle handle = dbi.open();
        Update update = handle.createStatement(String.format("SCRIPT TO '%s' COMPRESSION GZIP", zipPath));
        update.execute();
    }


}

