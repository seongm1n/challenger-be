package academy.challenger;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Fail.fail;

public class MysqlTest {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConnection() {

        try(Connection con =
                    DriverManager.getConnection(
                            "jdbc:mysql://127.0.0.1:3306/challenger?serverTimezone=Asia/Seoul",
                            "root",
                            "tjdals398")){

            System.out.println(con);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}
