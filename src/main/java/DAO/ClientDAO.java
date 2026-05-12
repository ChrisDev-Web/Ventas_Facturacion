package DAO;

import Config.Database;
import Models.Client;
import Models.SelectOption;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public void create(Client client) throws SQLException {
        String sql = "{CALL sp_client_create(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillCreateUpdateStatement(statement, client, false);
            statement.execute();
        }
    }

    public void update(Client client) throws SQLException {
        String sql = "{CALL sp_client_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, client.getIdClient());
            fillCreateUpdateStatement(statement, client, true);
            statement.execute();
        }
    }

    private void fillCreateUpdateStatement(CallableStatement statement, Client client, boolean update)
            throws SQLException {

        int index = update ? 2 : 1;

        statement.setString(index++, client.getName());
        statement.setString(index++, client.getLastNamePaternal());
        statement.setString(index++, client.getLastNameMaternal());

        setNullableInt(statement, index++, client.getIdDocumentType());
        statement.setString(index++, client.getDocumentNumber());
        statement.setString(index++, client.getPhone());
        statement.setString(index++, client.getEmail());
        statement.setString(index++, client.getAddress());

        setNullableInt(statement, index++, client.getIdRegions());
        setNullableInt(statement, index++, client.getIdProvinces());
        setNullableInt(statement, index, client.getIdDistrict());
    }

    public Client findById(int idClient) throws SQLException {
        String sql = "{CALL sp_client_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idClient);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapClient(resultSet);
                }
            }
        }

        return null;
    }

    public Client findActiveByDocument(Integer idDocumentType, String documentNumber) throws SQLException {
        String sql = """
                SELECT
                    c.id_client,
                    c.name,
                    c.last_name_paternal,
                    c.last_name_maternal,
                    c.id_document_type,
                    dt.name AS document_type_name,
                    c.document_number,
                    c.phone,
                    c.email,
                    c.address,
                    c.id_regions,
                    r.name AS region_name,
                    c.id_provinces,
                    p.name AS province_name,
                    c.id_district,
                    d.name AS district_name,
                    c.status,
                    c.created_at,
                    c.updated_at,
                    c.deleted_at
                FROM clients c
                LEFT JOIN document_types dt ON dt.id_document_type = c.id_document_type
                LEFT JOIN regions r ON r.id_region = c.id_regions
                LEFT JOIN provinces p ON p.id_province = c.id_provinces
                LEFT JOIN districts d ON d.id_district = c.id_district
                WHERE c.status = 1
                  AND c.deleted_at IS NULL
                  AND c.id_document_type = ?
                  AND c.document_number = ?
                LIMIT 1
                """;

        try (
            Connection connection = Database.getConnection();
            java.sql.PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, idDocumentType == null ? 0 : idDocumentType);
            statement.setString(2, documentNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapClient(resultSet);
                }
            }
        }

        return null;
    }

    public List<Client> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_client_list_active(?, ?, ?)}";
        List<Client> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapClient(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_client_count_active(?)}";

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

    public List<Client> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_client_list_inactive(?, ?, ?)}";
        List<Client> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapClient(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_client_count_inactive(?)}";

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

    public void deleteLogical(int idClient) throws SQLException {
        String sql = "{CALL sp_client_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idClient);
            statement.execute();
        }
    }

    public void restore(int idClient) throws SQLException {
        String sql = "{CALL sp_client_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idClient);
            statement.execute();
        }
    }

    public void deletePhysical(int idClient) throws SQLException {
        String sql = "{CALL sp_client_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idClient);
            statement.execute();
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws SQLException {
        return listOptions("{CALL sp_client_document_type_options()}");
    }

    public List<SelectOption> listRegionOptions() throws SQLException {
        return listOptions("{CALL sp_client_region_options()}");
    }

    public List<SelectOption> listProvinceOptions(int idRegion) throws SQLException {
        String sql = "{CALL sp_client_province_options(?)}";
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idRegion);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapOption(resultSet));
                }
            }
        }

        return list;
    }

    public List<SelectOption> listDistrictOptions(int idProvince) throws SQLException {
        String sql = "{CALL sp_client_district_options(?)}";
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idProvince);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapOption(resultSet));
                }
            }
        }

        return list;
    }

    private List<SelectOption> listOptions(String sql) throws SQLException {
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                list.add(mapOption(resultSet));
            }
        }

        return list;
    }

    private SelectOption mapOption(ResultSet resultSet) throws SQLException {
        return new SelectOption(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    private Client mapClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();

        client.setIdClient(resultSet.getInt("id_client"));
        client.setName(resultSet.getString("name"));
        client.setLastNamePaternal(resultSet.getString("last_name_paternal"));
        client.setLastNameMaternal(resultSet.getString("last_name_maternal"));

        client.setIdDocumentType(getNullableInt(resultSet, "id_document_type"));
        client.setDocumentTypeName(resultSet.getString("document_type_name"));
        client.setDocumentNumber(resultSet.getString("document_number"));

        client.setPhone(resultSet.getString("phone"));
        client.setEmail(resultSet.getString("email"));
        client.setAddress(resultSet.getString("address"));

        client.setIdRegions(getNullableInt(resultSet, "id_regions"));
        client.setRegionName(resultSet.getString("region_name"));

        client.setIdProvinces(getNullableInt(resultSet, "id_provinces"));
        client.setProvinceName(resultSet.getString("province_name"));

        client.setIdDistrict(getNullableInt(resultSet, "id_district"));
        client.setDistrictName(resultSet.getString("district_name"));

        client.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            client.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            client.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            client.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return client;
    }

    private Integer getNullableInt(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);

        if (resultSet.wasNull()) {
            return null;
        }

        return value;
    }

    private void setNullableInt(CallableStatement statement, int index, Integer value) throws SQLException {
        if (value == null || value <= 0) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}
