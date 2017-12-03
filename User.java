import java.sql.*;

public abstract class User {
    public abstract String getType();
    public abstract void prompt(Connection conn);
}
