package DAO;

import Config.Database;
import Models.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO {

    public void register(User user) throws SQLException {
        String sql = "{CALL sp_user_register(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());

            statement.execute();
        }
    }

    public User login(String userName) throws SQLException {
        String sql = "{CALL sp_user_login(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();

        user.setIdUser(resultSet.getInt("id_user"));
        user.setUserName(resultSet.getString("user_name"));
        user.setPassword(resultSet.getString("password"));
        user.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }
}