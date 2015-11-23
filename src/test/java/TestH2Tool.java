import com.epiphyllum.h2tool.H2Tool;
import org.skife.jdbi.v2.Handle;

import java.sql.SQLException;

/**
 * Created by hary on 15/11/19.
 */
public class TestH2Tool {
    public static void main(String[] args) throws SQLException {
        H2Tool h2Tool = new H2Tool("phonebook", "db/migration");
        h2Tool.backup("/Users/hary/phonebook.zip");

        H2Tool restore = new H2Tool("/Users/hary/phonebook.zip");
        restore.testRestore();
    }
}
