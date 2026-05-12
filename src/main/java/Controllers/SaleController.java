package Controllers;

import DAO.ClientDAO;
import DAO.SaleDAO;
import Models.Client;
import Models.Sale;
import Models.SaleDetail;
import Models.SaleHistoryStats;
import Models.SaleProductItem;
import Models.SaleRanking;
import Models.SelectOption;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SaleController {

    private final SaleDAO saleDAO;
    private final ClientDAO clientDAO;

    public SaleController() {
        this.saleDAO = new SaleDAO();
        this.clientDAO = new ClientDAO();
    }

    public List<SaleProductItem> listProducts(String search, int idCategory, int idBrand) throws Exception {
        try {
            return saleDAO.listProducts(normalizeSearch(search), idCategory, idBrand);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Sale createSale(Sale sale) throws Exception {
        validateSale(sale);
        resolveCustomerForSale(sale);

        try {
            return saleDAO.createSale(sale);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Client findActiveClientByDocument(Integer idDocumentType, String documentNumber) throws Exception {
        if (idDocumentType == null || idDocumentType <= 0) {
            return null;
        }

        String normalizedDocument = normalizeDocument(documentNumber);

        if (normalizedDocument == null) {
            return null;
        }

        try {
            return clientDAO.findActiveByDocument(idDocumentType, normalizedDocument);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listCategoryOptions() throws Exception {
        try {
            return saleDAO.listCategoryOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listBrandOptions() throws Exception {
        try {
            return saleDAO.listBrandOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listPaymentMethodOptions() throws Exception {
        try {
            return saleDAO.listPaymentMethodOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws Exception {
        try {
            return saleDAO.listDocumentTypeOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listUserOptions() throws Exception {
        try {
            return saleDAO.listUserOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Sale> listHistory(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo, int page, int limit) throws Exception {
        try {
            return saleDAO.listHistory(normalizeSearch(search), idPaymentMethod, idUser, dateFrom, dateTo, normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countHistory(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        try {
            return saleDAO.countHistory(normalizeSearch(search), idPaymentMethod, idUser, dateFrom, dateTo);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public SaleHistoryStats getStats(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        try {
            return saleDAO.getStats(normalizeSearch(search), idPaymentMethod, idUser, dateFrom, dateTo);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SaleRanking> getRanking(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        try {
            return saleDAO.getRanking(normalizeSearch(search), idPaymentMethod, idUser, dateFrom, dateTo);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Sale findById(int idSale) throws Exception {
        if (idSale <= 0) {
            throw new Exception("Seleccione una venta valida.");
        }

        try {
            Sale sale = saleDAO.findById(idSale);
            if (sale == null) {
                throw new Exception("No se encontro la venta.");
            }
            return sale;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateSale(Sale sale) throws Exception {
        if (sale.getIdUser() <= 0) {
            throw new Exception("Usuario no valido.");
        }

        if (sale.getIdPaymentMethod() <= 0) {
            throw new Exception("Seleccione un metodo de pago.");
        }

        if (sale.getDetails() == null || sale.getDetails().isEmpty()) {
            throw new Exception("Agregue productos al carrito.");
        }

        if (sale.getPaidAmount() == null || sale.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Ingrese un monto pagado valido.");
        }

        if (!isTicket(sale.getDocumentKind())) {
            if (sale.getCustomerDocumentTypeId() == null || sale.getCustomerDocumentTypeId() <= 0) {
                throw new Exception("Seleccione el tipo de documento del cliente.");
            }

            if (normalizeDocument(sale.getCustomerDocumentNumber()) == null) {
                throw new Exception("Ingrese el numero de documento del cliente.");
            }
        }

        for (SaleDetail detail : sale.getDetails()) {
            if (detail.getIdProduct() <= 0) {
                throw new Exception("El carrito tiene productos invalidos.");
            }

            if (detail.getQuantity() <= 0) {
                throw new Exception("La cantidad debe ser mayor a cero.");
            }

            if (detail.getDiscountType() == null || detail.getDiscountType().trim().isEmpty()) {
                detail.setDiscountType("NONE");
            }

            if (detail.getDiscountValue() == null) {
                detail.setDiscountValue(BigDecimal.ZERO);
            }
        }
    }

    private void resolveCustomerForSale(Sale sale) throws Exception {
        if (isTicket(sale.getDocumentKind())) {
            sale.setIdClient(null);
            sale.setCustomerName(null);
            sale.setCustomerDocumentTypeId(null);
            sale.setCustomerDocumentNumber(null);
            return;
        }

        Client client = findActiveClientByDocument(sale.getCustomerDocumentTypeId(), sale.getCustomerDocumentNumber());

        if (client == null) {
            throw new Exception("No existe un cliente activo registrado con ese tipo y numero de documento.");
        }

        sale.setIdClient(client.getIdClient());
        sale.setCustomerName(client.getFullName().trim());
        sale.setCustomerDocumentNumber(normalizeDocument(client.getDocumentNumber()));
        sale.setCustomerDocumentTypeId(client.getIdDocumentType());
    }

    private boolean isTicket(String documentKind) {
        return documentKind == null || documentKind.trim().equalsIgnoreCase("TICKET");
    }

    private String normalizeDocument(String documentNumber) {
        if (documentNumber == null || documentNumber.trim().isEmpty()) {
            return null;
        }

        return documentNumber.trim();
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
