package Controllers;

import DAO.SupplierDAO;
import Models.SelectOption;
import Models.Supplier;
import Models.SunatCompany;
import Services.DecolectaService;
import java.sql.SQLException;
import java.util.List;

public class SupplierController {

    private final SupplierDAO supplierDAO;
    private final DecolectaService decolectaService;

    public SupplierController() {
        this.supplierDAO = new SupplierDAO();
        this.decolectaService = new DecolectaService();
    }

    public void create(Supplier supplier) throws Exception {
        validateSupplier(supplier);

        try {
            normalizeSupplier(supplier);
            supplierDAO.create(supplier);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(Supplier supplier) throws Exception {
        if (supplier.getIdSupplier() <= 0) {
            throw new Exception("Seleccione un proveedor valido.");
        }

        validateSupplier(supplier);

        try {
            normalizeSupplier(supplier);
            supplierDAO.update(supplier);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Supplier findById(int idSupplier) throws Exception {
        if (idSupplier <= 0) {
            throw new Exception("Seleccione un proveedor valido.");
        }

        try {
            Supplier supplier = supplierDAO.findById(idSupplier);

            if (supplier == null) {
                throw new Exception("No se encontro el proveedor.");
            }

            return supplier;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Supplier> listActive(String search, int page, int limit) throws Exception {
        try {
            return supplierDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return supplierDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Supplier> listInactive(String search, int page, int limit) throws Exception {
        try {
            return supplierDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return supplierDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idSupplier) throws Exception {
        if (idSupplier <= 0) {
            throw new Exception("Seleccione un proveedor valido.");
        }

        try {
            supplierDAO.deleteLogical(idSupplier);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idSupplier) throws Exception {
        if (idSupplier <= 0) {
            throw new Exception("Seleccione un proveedor valido.");
        }

        try {
            supplierDAO.restore(idSupplier);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idSupplier) throws Exception {
        if (idSupplier <= 0) {
            throw new Exception("Seleccione un proveedor valido.");
        }

        try {
            supplierDAO.deletePhysical(idSupplier);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws Exception {
        try {
            return supplierDAO.listDocumentTypeOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listRegionOptions() throws Exception {
        try {
            return supplierDAO.listRegionOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listProvinceOptions(int idRegion) throws Exception {
        if (idRegion <= 0) {
            return java.util.Collections.emptyList();
        }

        try {
            return supplierDAO.listProvinceOptions(idRegion);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDistrictOptions(int idProvince) throws Exception {
        if (idProvince <= 0) {
            return java.util.Collections.emptyList();
        }

        try {
            return supplierDAO.listDistrictOptions(idProvince);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Supplier searchByRuc(String ruc) throws Exception {
        SunatCompany company = decolectaService.findByRuc(ruc);

        Supplier supplier = new Supplier();
        supplier.setBusinessName(company.getBusinessName());
        supplier.setDocumentNumber(company.getDocumentNumber());
        supplier.setAddress(company.getAddress());
        return supplier;
    }

    private void validateSupplier(Supplier supplier) throws Exception {
        if (supplier.getBusinessName() == null || supplier.getBusinessName().trim().isEmpty()) {
            throw new Exception("Ingrese la razon social.");
        }

        if (supplier.getBusinessName().trim().length() > 150) {
            throw new Exception("La razon social no debe superar los 150 caracteres.");
        }

        if (supplier.getTradeName() != null && supplier.getTradeName().trim().length() > 150) {
            throw new Exception("El nombre comercial no debe superar los 150 caracteres.");
        }

        if (supplier.getIdDocumentType() <= 0) {
            throw new Exception("Seleccione un tipo de documento.");
        }

        if (supplier.getDocumentNumber() == null || supplier.getDocumentNumber().trim().isEmpty()) {
            throw new Exception("Ingrese el numero de documento.");
        }

        if (supplier.getDocumentNumber().trim().length() > 30) {
            throw new Exception("El numero de documento no debe superar los 30 caracteres.");
        }

        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            String email = supplier.getEmail().trim();

            if (!email.contains("@") || !email.contains(".")) {
                throw new Exception("Ingrese un correo valido.");
            }

            if (email.length() > 150) {
                throw new Exception("El correo no debe superar los 150 caracteres.");
            }
        }

        if (supplier.getIdProvince() != null && supplier.getIdProvince() > 0
                && (supplier.getIdRegion() == null || supplier.getIdRegion() <= 0)) {
            throw new Exception("Seleccione una region antes de seleccionar provincia.");
        }

        if (supplier.getIdDistrict() != null && supplier.getIdDistrict() > 0
                && (supplier.getIdProvince() == null || supplier.getIdProvince() <= 0)) {
            throw new Exception("Seleccione una provincia antes de seleccionar distrito.");
        }
    }

    private void normalizeSupplier(Supplier supplier) {
        supplier.setBusinessName(normalizeRequired(supplier.getBusinessName()));
        supplier.setTradeName(normalizeText(supplier.getTradeName()));
        supplier.setDocumentNumber(normalizeRequired(supplier.getDocumentNumber()));
        supplier.setPhone(normalizeText(supplier.getPhone()));
        supplier.setEmail(normalizeText(supplier.getEmail()));
        supplier.setAddress(normalizeText(supplier.getAddress()));

        supplier.setIdRegion(normalizeInteger(supplier.getIdRegion()));
        supplier.setIdProvince(normalizeInteger(supplier.getIdProvince()));
        supplier.setIdDistrict(normalizeInteger(supplier.getIdDistrict()));
    }

    private String normalizeRequired(String text) {
        return text == null ? "" : text.trim();
    }

    private String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        return text.trim();
    }

    private Integer normalizeInteger(Integer value) {
        if (value == null || value <= 0) {
            return null;
        }

        return value;
    }

    private String normalizeSearch(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }

        return search.trim();
    }

    private int normalizePage(int page) {
        return page < 1 ? 1 : page;
    }

    private int normalizeLimit(int limit) {
        if (limit == 20 || limit == 50) {
            return limit;
        }

        return 10;
    }

    private String getSqlMessage(SQLException e) {
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            return e.getMessage();
        }

        return "Ocurrio un error al comunicarse con la base de datos.";
    }
}
