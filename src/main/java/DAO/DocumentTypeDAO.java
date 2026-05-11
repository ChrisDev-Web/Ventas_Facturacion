package DAO;

import Config.Database;
import Models.DocumentType;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DocumentTypeDAO {

    public void create(DocumentType documentType) throws SQLException {
        String sql = "{CALL sp_document_type_create(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, documentType.getName());
            statement.setString(2, documentType.getDescription());
            statement.execute();
        }
    }

    public void update(DocumentType documentType) throws SQLException {
        String sql = "{CALL sp_document_type_update(?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, documentType.getIdDocumentType());
            statement.setString(2, documentType.getName());
            statement.setString(3, documentType.getDescription());
            statement.execute();
        }
    }

    public DocumentType findById(int idDocumentType) throws SQLException {
        String sql = "{CALL sp_document_type_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idDocumentType);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDocumentType(resultSet);
                }
            }
        }

        return null;
    }

    public List<DocumentType> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_document_type_list_active(?, ?, ?)}";
        List<DocumentType> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapDocumentType(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_document_type_count_active(?)}";

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

    public List<DocumentType> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_document_type_list_inactive(?, ?, ?)}";
        List<DocumentType> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapDocumentType(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_document_type_count_inactive(?)}";

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

    public void deleteLogical(int idDocumentType) throws SQLException {
        String sql = "{CALL sp_document_type_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idDocumentType);
            statement.execute();
        }
    }

    public void restore(int idDocumentType) throws SQLException {
        String sql = "{CALL sp_document_type_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idDocumentType);
            statement.execute();
        }
    }

    public void deletePhysical(int idDocumentType) throws SQLException {
        String sql = "{CALL sp_document_type_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idDocumentType);
            statement.execute();
        }
    }

    private DocumentType mapDocumentType(ResultSet resultSet) throws SQLException {
        DocumentType documentType = new DocumentType();

        documentType.setIdDocumentType(resultSet.getInt("id_document_type"));
        documentType.setName(resultSet.getString("name"));
        documentType.setDescription(resultSet.getString("description"));
        documentType.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            documentType.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            documentType.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            documentType.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return documentType;
    }
}