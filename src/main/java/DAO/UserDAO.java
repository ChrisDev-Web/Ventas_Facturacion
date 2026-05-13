package DAO;

import Config.Database;
import Models.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    public void create(User user) throws SQLException {
        String sql = "{CALL sp_user_management_create(?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getProfileImagePath());
            statement.setString(6, user.getPassword());
            statement.setInt(7, user.getStatus());
            statement.execute();
        }
    }

    public void update(User user) throws SQLException {
        String sql = "{CALL sp_user_management_update(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, user.getIdUser());
            statement.setString(2, user.getUserName());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getProfileImagePath());
            statement.setString(7, user.getPassword());
            statement.setInt(8, user.getStatus());
            statement.execute();
        }
    }

    public List<User> list(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_user_management_list(?, ?, ?)}";
        List<User> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapUser(resultSet));
                }
            }
        }

        return list;
    }

    public int count(String search) throws SQLException {
        String sql = "{CALL sp_user_management_count(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
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

    public User findById(int idUser) throws SQLException {
        String sql = "{CALL sp_user_profile_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idUser);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    public User findManagementById(int idUser) throws SQLException {
        String sql = "{CALL sp_user_management_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idUser);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    public void updateProfile(User user) throws SQLException {
        String sql = "{CALL sp_user_profile_update(?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, user.getIdUser());
            statement.setString(2, user.getUserName());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getProfileImagePath());
            statement.setString(7, user.getPassword());
            statement.execute();
        }
    }

    public void deletePhysical(int idUser) throws SQLException {
        String sql = "{CALL sp_user_management_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idUser);
            statement.execute();
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();

        user.setIdUser(resultSet.getInt("id_user"));
        user.setUserName(resultSet.getString("user_name"));
        user.setPassword(readString(resultSet, "password"));
        user.setFullName(readString(resultSet, "full_name"));
        user.setEmail(readString(resultSet, "email"));
        user.setPhone(readString(resultSet, "phone"));
        user.setProfileImagePath(readString(resultSet, "profile_image_path"));
        user.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = readTimestamp(resultSet, "created_at");
        Timestamp updatedAt = readTimestamp(resultSet, "updated_at");

        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }

    private String readString(ResultSet resultSet, String columnName) throws SQLException {
        return hasColumn(resultSet, columnName) ? resultSet.getString(columnName) : null;
    }

    private Timestamp readTimestamp(ResultSet resultSet, String columnName) throws SQLException {
        return hasColumn(resultSet, columnName) ? resultSet.getTimestamp(columnName) : null;
    }

    private boolean hasColumn(ResultSet resultSet, String columnName) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))
                    || columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                return true;
            }
        }

        return false;
    }
}
