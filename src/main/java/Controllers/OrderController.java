package Controllers;

import DAO.OrderDAO;
import DAO.ClientDAO;
import Models.Client;
import Models.Order;
import Models.OrderDetail;
import Models.OrderStats;
import Models.Sale;
import Models.SaleProductItem;
import Models.SelectOption;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderController {

    private final OrderDAO orderDAO;
    private final ClientDAO clientDAO;

    public OrderController() {
        this.orderDAO = new OrderDAO();
        this.clientDAO = new ClientDAO();
    }

    public List<SaleProductItem> listProducts(String search, int idCategory, int idBrand) throws Exception {
        try {
            return orderDAO.listProducts(normalizeSearch(search), idCategory, idBrand);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listCategoryOptions() throws Exception {
        try {
            return orderDAO.listCategoryOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listBrandOptions() throws Exception {
        try {
            return orderDAO.listBrandOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws Exception {
        try {
            return orderDAO.listDocumentTypeOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listPaymentMethodOptions() throws Exception {
        try {
            return orderDAO.listPaymentMethodOptions();
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

    public Order create(Order order) throws Exception {
        validateOrder(order);
        resolveCustomer(order);

        try {
            Order result = orderDAO.create(order);

            if (result == null) {
                throw new Exception("No se pudo registrar la orden.");
            }

            return result;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(Order order) throws Exception {
        if (order.getIdOrder() <= 0) {
            throw new Exception("Seleccione una orden valida.");
        }

        validateOrder(order);
        resolveCustomer(order);

        try {
            orderDAO.update(order);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int count(String search, String status, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        try {
            return orderDAO.count(normalizeSearch(search), normalizeStatus(status), dateFrom, dateTo);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Order> list(String search, String status, LocalDate dateFrom, LocalDate dateTo, int page, int limit)
            throws Exception {
        try {
            return orderDAO.list(
                    normalizeSearch(search),
                    normalizeStatus(status),
                    dateFrom,
                    dateTo,
                    normalizePage(page),
                    normalizeLimit(limit)
            );
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public OrderStats getStats(String search, String status, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        try {
            return orderDAO.getStats(normalizeSearch(search), normalizeStatus(status), dateFrom, dateTo);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Order findById(int idOrder) throws Exception {
        if (idOrder <= 0) {
            throw new Exception("Seleccione una orden valida.");
        }

        try {
            Order order = orderDAO.findById(idOrder);

            if (order == null) {
                throw new Exception("No se encontro la orden.");
            }

            return order;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void cancel(int idOrder) throws Exception {
        if (idOrder <= 0) {
            throw new Exception("Seleccione una orden valida.");
        }

        try {
            orderDAO.cancel(idOrder);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Sale convertToSale(int idOrder, int idUser, int idPaymentMethod, String documentKind, BigDecimal paidAmount)
            throws Exception {
        if (idOrder <= 0) {
            throw new Exception("Seleccione una orden valida.");
        }

        if (idUser <= 0) {
            throw new Exception("Usuario no valido.");
        }

        if (idPaymentMethod <= 0) {
            throw new Exception("Seleccione un metodo de pago.");
        }

        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Ingrese un monto pagado valido.");
        }

        try {
            Sale sale = orderDAO.convertToSale(
                    idOrder,
                    idUser,
                    idPaymentMethod,
                    normalizeDocumentKind(documentKind),
                    paidAmount
            );

            if (sale == null) {
                throw new Exception("No se pudo convertir la orden.");
            }

            return sale;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateOrder(Order order) throws Exception {
        if (order.getIdUser() <= 0) {
            throw new Exception("Usuario no valido.");
        }

        if (order.getCustomerDocumentTypeId() == null || order.getCustomerDocumentTypeId() <= 0) {
            throw new Exception("Seleccione el tipo de documento del cliente.");
        }

        if (normalizeDocument(order.getCustomerDocumentNumber()) == null) {
            throw new Exception("Ingrese el numero de documento del cliente.");
        }

        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            throw new Exception("Agregue productos a la orden.");
        }

        for (OrderDetail detail : order.getDetails()) {
            if (detail.getIdProduct() <= 0) {
                throw new Exception("La orden tiene productos invalidos.");
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

    private void resolveCustomer(Order order) throws Exception {
        Client client = findActiveClientByDocument(order.getCustomerDocumentTypeId(), order.getCustomerDocumentNumber());

        if (client == null) {
            throw new Exception("No existe un cliente activo registrado con ese documento.");
        }

        order.setIdClient(client.getIdClient());
        order.setCustomerName(client.getFullName().trim());
        order.setCustomerDocumentTypeId(client.getIdDocumentType());
        order.setCustomerDocumentNumber(normalizeDocument(client.getDocumentNumber()));
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

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty() || status.trim().equalsIgnoreCase("TODAS")) {
            return "";
        }

        return status.trim().toUpperCase();
    }

    private String normalizeDocumentKind(String documentKind) {
        if (documentKind == null || documentKind.trim().isEmpty()) {
            return "TICKET";
        }

        String normalized = documentKind.trim().toUpperCase();
        if (!normalized.equals("BOLETA") && !normalized.equals("FACTURA")) {
            return "TICKET";
        }

        return normalized;
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
