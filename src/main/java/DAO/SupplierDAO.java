package DAO;

import Config.Database;
import Models.SelectOption;
import Models.Supplier;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public void create(Supplier supplier) throws SQLException {
        String sql = "{CALL sp_supplier_create(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillCreateUpdateStatement(statement, supplier, false);
            statement.execute();
        }
    }

    public void update(Supplier supplier) throws SQLException {
        String sql = "{CALL sp_supplier_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, supplier.getIdSupplier());
            fillCreateUpdateStatement(statement, supplier, true);
            statement.execute();
        }
    }

    private void fillCreateUpdateStatement(CallableStatement statement, Supplier supplier, boolean update)
            throws SQLException {

        int index = update ? 2 : 1;

        statement.setString(index++, supplier.getBusinessName());
        statement.setString(index++, supplier.getTradeName());
        statement.setInt(index++, supplier.getIdDocumentType());
        statement.setString(index++, supplier.getDocumentNumber());
        statement.setString(index++, supplier.getPhone());
        statement.setString(index++, supplier.getEmail());
        statement.setString(index++, supplier.getAddress());

        setNullableInt(statement, index++, supplier.getIdRegion());
        setNullableInt(statement, index++, supplier.getIdProvince());
        setNullableInt(statement, index, supplier.getIdDistrict());
    }

    public Supplier findById(int idSupplier) throws SQLException {
        String sql = "{CALL sp_supplier_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idSupplier);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSupplier(resultSet);
                }
            }
        }

        return null;
    }

    public List<Supplier> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_supplier_list_active(?, ?, ?)}";
        List<Supplier> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapSupplier(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_supplier_count_active(?)}";

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

    public List<Supplier> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_supplier_list_inactive(?, ?, ?)}";
        List<Supplier> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapSupplier(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_supplier_count_inactive(?)}";

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

    public void deleteLogical(int idSupplier) throws SQLException {
        String sql = "{CALL sp_supplier_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idSupplier);
            statement.execute();
        }
    }

    public void restore(int idSupplier) throws SQLException {
        String sql = "{CALL sp_supplier_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idSupplier);
            statement.execute();
        }
    }

    public void deletePhysical(int idSupplier) throws SQLException {
        String sql = "{CALL sp_supplier_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idSupplier);
            statement.execute();
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws SQLException {
        return listOptions("{CALL sp_supplier_document_type_options()}");
    }

    public List<SelectOption> listRegionOptions() throws SQLException {
        return listOptions("{CALL sp_supplier_region_options()}");
    }

    public List<SelectOption> listProvinceOptions(int idRegion) throws SQLException {
        String sql = "{CALL sp_supplier_province_options(?)}";
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
        String sql = "{CALL sp_supplier_district_options(?)}";
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

    private Supplier mapSupplier(ResultSet resultSet) throws SQLException {
        Supplier supplier = new Supplier();

        supplier.setIdSupplier(resultSet.getInt("id_supplier"));
        supplier.setBusinessName(resultSet.getString("business_name"));
        supplier.setTradeName(resultSet.getString("trade_name"));

        supplier.setIdDocumentType(resultSet.getInt("id_document_type"));
        supplier.setDocumentTypeName(resultSet.getString("document_type_name"));
        supplier.setDocumentNumber(resultSet.getString("document_number"));

        supplier.setPhone(resultSet.getString("phone"));
        supplier.setEmail(resultSet.getString("email"));
        supplier.setAddress(resultSet.getString("address"));

        supplier.setIdRegion(getNullableInt(resultSet, "id_region"));
        supplier.setRegionName(resultSet.getString("region_name"));

        supplier.setIdProvince(getNullableInt(resultSet, "id_province"));
        supplier.setProvinceName(resultSet.getString("province_name"));

        supplier.setIdDistrict(getNullableInt(resultSet, "id_district"));
        supplier.setDistrictName(resultSet.getString("district_name"));

        supplier.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            supplier.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            supplier.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            supplier.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return supplier;
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