import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class DatabaseContainerTest {

  @Container
  private static MySQLContainer<?> sqlContainer = new MySQLContainer().withPassword("bla");

  @Test
  public void creates_database() throws SQLException {
    String dbName = "dbName";
    createDatabase(dbName);

    Connection connection = sqlContainer.createConnection("");
    ResultSet resultSet = connection.createStatement().executeQuery("show databases");
    List<String> databases = new ArrayList<>();
    while(resultSet.next()){
      databases.add(resultSet.getString(1));
    }

    assertThat(databases).contains(dbName);
  }

  private void createDatabase(String dbName) throws SQLException {
    Connection connection = sqlContainer.withUsername("root").createConnection("");
    connection.createStatement().executeUpdate(String.format("CREATE DATABASE %s", dbName));
    connection.createStatement().executeUpdate("GRANT ALL PRIVILEGES on *.* to 'test'@'%'");
  }
}
