import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.testcontainers.containers.JdbcDatabaseContainer;

public abstract class BaseDatabaseTest {

  public static void createDatabase(JdbcDatabaseContainer<?> dbContainer, String dbName) throws SQLException {
    Connection connection = dbContainer.withUsername("root").createConnection("");
    connection.createStatement().executeUpdate(String.format("CREATE DATABASE %s", dbName));
    connection.createStatement().executeUpdate("GRANT ALL PRIVILEGES on *.* to 'test'@'%'");
  }

  public static List<String> showAllDatabases(JdbcDatabaseContainer<?> dbContainer) throws SQLException {
    Connection connection = dbContainer.createConnection("");
    ResultSet resultSet = connection.createStatement().executeQuery("show databases");
    List<String> databases = new ArrayList<>();
    while (resultSet.next()) {
      databases.add(resultSet.getString(1));
    }
    return databases;
  }

  public static void dropAllCreatedDatabases(JdbcDatabaseContainer<?> dbContainer)
      throws SQLException {
    List<String> databases = showAllDatabases(dbContainer);
    List<String> dropStatements =
        databases.stream().filter(db -> !db.toUpperCase().equals("INFORMATION_SCHEMA") &&
                !db.toUpperCase().equals("MYSQL") && !db.toUpperCase().equals("PERFORMANCE_SCHEMA")
                && !db.toUpperCase().equals("SYS") && !db.toUpperCase().equals("TEST"))
            .map(db -> String.format("DROP DATABASE %s", db))
            .collect(Collectors.toList());

    Statement statement = dbContainer.createConnection("").createStatement();
    dropStatements.forEach(st -> {
      try {
        statement.executeUpdate(st);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
  }

}
