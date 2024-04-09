import static org.assertj.core.api.Assertions.assertThat;

import com.pdemuinck.ParallelTest;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ParallelTest
@Testcontainers
public class DatabaseParallelTest extends BaseDatabaseTest {

  @Container
  private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>();

  @BeforeEach
  public void beforeEach() throws SQLException {
    dropAllCreatedDatabases(mySQLContainer);
  }

  @ParameterizedTest
  @ValueSource(strings = {"firstDb", "secondDb"})
  public void creates_database(String dbName) throws SQLException {
    createDatabase(mySQLContainer, dbName);
    List<String> databases = showAllDatabases(mySQLContainer);
    assertThat(databases).contains(dbName);
  }
}
