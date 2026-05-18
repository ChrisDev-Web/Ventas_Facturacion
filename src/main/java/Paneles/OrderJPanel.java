package Paneles;

import Controllers.OrderController;
import Controllers.SaleController;
import Models.Client;
import Models.Order;
import Models.OrderDetail;
import Models.OrderStats;
import Models.Sale;
import Models.SaleProductItem;
import Models.SelectOption;
import Presentacion.DashboardJFrame;
import Presentacion.DashboardWindowSupport;
import Presentacion.SectionRefreshable;
import Services.VoucherPrintService;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class OrderJPanel extends JPanel implements SectionRefreshable {

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    private final OrderController controller;
    private final SaleController saleController;
    private final VoucherPrintService voucherPrintService;
    private final int idUser;

    private JTextField txtSearch;
    private JComboBox<String> cmbStatus;
    private DatePicker dpDateFrom;
    private DatePicker dpDateTo;
    private JComboBox<Integer> cmbLimit;

    private JLabel lblPendingCount;
    private JLabel lblConvertedCount;
    private JLabel lblCancelledCount;
    private JLabel lblPendingAmount;
    private JLabel lblPagination;

    private JTable table;
    private DefaultTableModel tableModel;

    private int currentPage = 1;
    private int limit = 10;
    private int totalPages = 1;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color cardColor = Color.WHITE;
    private final Color primaryColor = new Color(30, 136, 229);
    private final Color successColor = new Color(46, 125, 50);
    private final Color warningColor = new Color(245, 124, 0);
    private final Color dangerColor = new Color(198, 40, 40);
    private final Color borderColor = new Color(225, 225, 225);
    private final Color textColor = new Color(33, 33, 33);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public OrderJPanel(int idUser) {
        this.idUser = idUser;
        this.controller = new OrderController();
        this.saleController = new SaleController();
        this.voucherPrintService = new VoucherPrintService();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createPaginationPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(backgroundColor);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Ordenes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Gestiona pre-ventas y conviertelas en ventas cuando corresponda.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 12));
        statsPanel.setBackground(backgroundColor);

        lblPendingCount = createMetricValueLabel();
        lblConvertedCount = createMetricValueLabel();
        lblCancelledCount = createMetricValueLabel();
        lblPendingAmount = createMetricValueLabel();

        statsPanel.add(createMetricCard("Pendientes", lblPendingCount, warningColor));
        statsPanel.add(createMetricCard("Convertidas", lblConvertedCount, successColor));
        statsPanel.add(createMetricCard("Anuladas", lblCancelledCount, dangerColor));
        statsPanel.add(createMetricCard("Monto pendiente", lblPendingAmount, primaryColor));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(backgroundColor);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbStatus = new JComboBox<>(new String[]{"TODAS", "PENDIENTE", "CONVERTIDA", "ANULADA"});
        cmbStatus.setPreferredSize(new Dimension(150, 36));
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        dpDateFrom = createDatePicker();
        dpDateTo = createDatePicker();

        JButton btnSearch = createButton("Buscar", primaryColor);
        JButton btnClear = createButton("Limpiar", new Color(90, 90, 90));
        JButton btnNew = createButton("Nueva orden", successColor);

        btnSearch.addActionListener(e -> {
            currentPage = 1;
            loadData();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbStatus.setSelectedIndex(0);
            dpDateFrom.clear();
            dpDateTo.clear();
            currentPage = 1;
            loadData();
        });

        btnNew.addActionListener(e -> openOrderFormDialog(null));

        toolbar.add(txtSearch);
        toolbar.add(cmbStatus);
        toolbar.add(createInlineLabel("Desde"));
        toolbar.add(dpDateFrom);
        toolbar.add(createInlineLabel("Hasta"));
        toolbar.add(dpDateTo);
        toolbar.add(btnSearch);
        toolbar.add(btnClear);
        toolbar.add(btnNew);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(statsPanel, BorderLayout.CENTER);
        container.add(toolbar, BorderLayout.SOUTH);
        return container;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createLineBorder(borderColor));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        top.setBackground(cardColor);

        cmbLimit = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbLimit.setPreferredSize(new Dimension(80, 34));
        cmbLimit.addActionListener(e -> {
            limit = (Integer) cmbLimit.getSelectedItem();
            currentPage = 1;
            loadData();
        });

        top.add(new JLabel("Mostrar"));
        top.add(cmbLimit);
        top.add(new JLabel("registros"));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Codigo", "Cliente", "Estado", "Fecha", "Entrega", "Total", "Venta", "Acciones"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setSelectionBackground(new Color(227, 242, 253));
        table.setSelectionForeground(textColor);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(260);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);
        table.getColumnModel().getColumn(8).setPreferredWidth(190);

        table.getColumnModel().getColumn(8).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(8).setCellEditor(new ActionsEditor(table));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(backgroundColor);

        JButton btnPrevious = createButton("Anterior", new Color(90, 90, 90));
        JButton btnNext = createButton("Siguiente", new Color(90, 90, 90));

        lblPagination = new JLabel();
        lblPagination.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnPrevious.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadData();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadData();
            }
        });

        panel.add(btnPrevious);
        panel.add(lblPagination);
        panel.add(btnNext);
        return panel;
    }

    private JLabel createMetricValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(textColor);
        return label;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(95, 95, 95));

        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(100, 4));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(accent, BorderLayout.SOUTH);
        return card;
    }

    private JLabel createInlineLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private DatePicker createDatePicker() {
        DatePickerSettings settings = new DatePickerSettings(new Locale("es", "PE"));
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        settings.setAllowKeyboardEditing(false);

        DatePicker datePicker = new DatePicker(settings);
        datePicker.setPreferredSize(new Dimension(140, 36));
        return datePicker;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(100, text.length() * 9 + 36), 36));
        return button;
    }

    private void loadData() {
        try {
            String search = txtSearch.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();
            LocalDate dateFrom = dpDateFrom.getDate();
            LocalDate dateTo = dpDateTo.getDate();

            int totalRecords = controller.count(search, status, dateFrom, dateTo);
            totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<Order> list = controller.list(search, status, dateFrom, dateTo, currentPage, limit);

            tableModel.setRowCount(0);
            for (Order order : list) {
                tableModel.addRow(new Object[]{
                    order.getIdOrder(),
                    order.getOrderCode(),
                    safeText(order.getCustomerName(), "Cliente no encontrado"),
                    safeText(order.getStatus(), "-"),
                    formatDate(order.getOrderDate()),
                    formatDate(order.getExpectedDate()),
                    "S/ " + money(order.getTotal()),
                    order.getLinkedVoucherCode() == null || order.getLinkedVoucherCode().isBlank()
                            ? "-"
                            : order.getLinkedVoucherCode(),
                    ""
                });
            }

            lblPagination.setText("Pagina " + currentPage + " de " + totalPages + " | Total: " + totalRecords);
            loadStats(search, status, dateFrom, dateTo);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadStats(String search, String status, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        OrderStats stats = controller.getStats(search, status, dateFrom, dateTo);
        lblPendingCount.setText(String.valueOf(stats.getPendingCount()));
        lblConvertedCount.setText(String.valueOf(stats.getConvertedCount()));
        lblCancelledCount.setText(String.valueOf(stats.getCancelledCount()));
        lblPendingAmount.setText("S/ " + money(stats.getPendingAmount()));
    }

    @Override
    public void refreshSectionData() {
        loadData();
    }

    private void openOrderFormDialog(Integer idOrder) {
        try {
            Order existing = idOrder == null ? null : controller.findById(idOrder);
            OrderFormDialog dialog = new OrderFormDialog(existing);
            dialog.setVisible(true);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showOrderDetail(int idOrder) {
        try {
            Order order = controller.findById(idOrder);

            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setTitle("Detalle de orden");
            dialog.setSize(960, 720);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.setContentPane(createOrderDetailContent(order));
            dialog.setVisible(true);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private JScrollPane createOrderDetailContent(Order order) {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(backgroundColor);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel headerCard = new JPanel(new BorderLayout(0, 12));
        headerCard.setBackground(cardColor);
        headerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JPanel headerTop = new JPanel(new BorderLayout());
        headerTop.setOpaque(false);

        JLabel lblTitle = new JLabel("Orden " + safeText(order.getOrderCode(), "-"));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(textColor);

        JLabel lblStatus = createStatusBadge(order.getStatus());

        JLabel lblSubtitle = new JLabel("Creada por " + safeText(order.getUserName(), "-")
                + " el " + formatDate(order.getOrderDate()));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(95, 95, 95));

        headerTop.add(lblTitle, BorderLayout.WEST);
        headerTop.add(lblStatus, BorderLayout.EAST);
        headerCard.add(headerTop, BorderLayout.NORTH);
        headerCard.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel infoGrid = new JPanel(new GridLayout(2, 4, 12, 12));
        infoGrid.setBackground(backgroundColor);
        infoGrid.add(createInfoCard("Cliente", safeText(order.getCustomerName(), "Cliente no disponible")));
        infoGrid.add(createInfoCard("Documento", buildOrderDocumentText(order)));
        infoGrid.add(createInfoCard("Entrega estimada", formatDate(order.getExpectedDate())));
        infoGrid.add(createInfoCard("Venta vinculada", safeText(order.getLinkedVoucherCode(), "Sin convertir")));
        infoGrid.add(createInfoCard("Subtotal", "S/ " + money(order.getSubtotal())));
        infoGrid.add(createInfoCard("IGV", "S/ " + money(order.getIgvAmount())));
        infoGrid.add(createInfoCard("Total", "S/ " + money(order.getTotal())));
        infoGrid.add(createInfoCard("Actualizacion", order.getConvertedAt() != null
                ? formatDate(order.getConvertedAt())
                : order.getCancelledAt() != null
                        ? formatDate(order.getCancelledAt())
                        : formatDate(order.getOrderDate())));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(backgroundColor);

        center.add(headerCard);
        center.add(createVerticalSpacer(14));
        center.add(infoGrid);

        String notes = safeText(order.getNotes(), "");
        if (!notes.isEmpty()) {
            center.add(createVerticalSpacer(14));
            center.add(createNotesCard(notes));
        }

        center.add(createVerticalSpacer(14));
        center.add(createProductsCard(order));

        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(backgroundColor);
        return scrollPane;
    }

    private JPanel createInfoCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 100, 100));

        JLabel lblValue = new JLabel("<html>" + safeText(value, "-") + "</html>");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblValue.setForeground(textColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel createNotesCard(String notes) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTitle = new JLabel("Notas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(textColor);

        JTextArea area = new JTextArea(notes);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBackground(new Color(249, 250, 252));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 235, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(area, BorderLayout.CENTER);
        return card;
    }

    private JPanel createProductsCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTitle = new JLabel("Productos de la orden");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(textColor);

        DefaultTableModel detailModel = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "P. unit", "Subtotal"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (OrderDetail detail : order.getDetails()) {
            detailModel.addRow(new Object[]{
                safeText(detail.getProductName(), "Producto"),
                detail.getQuantity(),
                "S/ " + money(detail.getUnitPrice()),
                "S/ " + money(detail.getSubtotal())
            });
        }

        JTable detailTable = new JTable(detailModel);
        detailTable.setRowHeight(34);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        detailTable.setEnabled(false);
        detailTable.setShowVerticalLines(false);
        detailTable.setGridColor(new Color(235, 235, 235));
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(420);
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(120);

        JScrollPane tableScroll = new JScrollPane(detailTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(borderColor));
        tableScroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel totals = new JPanel(new GridLayout(1, 3, 12, 12));
        totals.setBackground(cardColor);
        totals.add(createTotalCard("Subtotal", "S/ " + money(order.getSubtotal())));
        totals.add(createTotalCard("IGV", "S/ " + money(order.getIgvAmount())));
        totals.add(createTotalCard("Total", "S/ " + money(order.getTotal())));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        card.add(totals, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createTotalCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(new Color(249, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 235, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setForeground(new Color(100, 100, 100));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(textColor);

        card.add(lblLabel, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JLabel createStatusBadge(String status) {
        Color baseColor = switch (safeText(status, "").toUpperCase(Locale.ROOT)) {
            case "PENDIENTE" -> warningColor;
            case "CONVERTIDA" -> successColor;
            case "ANULADA" -> dangerColor;
            default -> new Color(96, 125, 139);
        };

        JLabel label = new JLabel(safeText(status, "-"));
        label.setOpaque(true);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(baseColor.darker());
        label.setBackground(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 30));
        label.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return label;
    }

    private JPanel createVerticalSpacer(int height) {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, height));
        spacer.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return spacer;
    }

    private String buildOrderDocumentText(Order order) {
        if (order.getCustomerDocumentTypeName() == null || order.getCustomerDocumentNumber() == null) {
            return "-";
        }

        return order.getCustomerDocumentTypeName() + " " + order.getCustomerDocumentNumber();
    }

    private void openConvertDialog(int idOrder) {
        try {
            Order order = controller.findById(idOrder);

            if (!"PENDIENTE".equalsIgnoreCase(order.getStatus())) {
                throw new Exception("Solo se pueden convertir ordenes pendientes.");
            }

            JComboBox<SelectOption> cmbPaymentMethod = new JComboBox<>();
            loadSelectOptions(cmbPaymentMethod, controller.listPaymentMethodOptions(), "Seleccione un metodo");
            cmbPaymentMethod.setPreferredSize(new Dimension(260, 34));

            JComboBox<String> cmbDocumentKind = new JComboBox<>(new String[]{"TICKET", "BOLETA", "FACTURA"});
            cmbDocumentKind.setPreferredSize(new Dimension(180, 34));

            JTextField txtPaidAmount = new JTextField(money(order.getTotal()));
            txtPaidAmount.setPreferredSize(new Dimension(180, 34));

            cmbPaymentMethod.addActionListener(e -> {
                if (isCashPayment((SelectOption) cmbPaymentMethod.getSelectedItem())) {
                    txtPaidAmount.setEditable(true);
                    txtPaidAmount.setText(money(order.getTotal()));
                } else {
                    txtPaidAmount.setEditable(false);
                    txtPaidAmount.setText(money(order.getTotal()));
                }
            });

            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.add(new JLabel("Metodo de pago"));
            panel.add(cmbPaymentMethod);
            panel.add(new JLabel("Documento"));
            panel.add(cmbDocumentKind);
            panel.add(new JLabel("Pago con"));
            panel.add(txtPaidAmount);

            int option = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Convertir orden " + order.getOrderCode(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            int idPaymentMethod = selectedId(cmbPaymentMethod);

            if (idPaymentMethod <= 0) {
                throw new Exception("Seleccione un metodo de pago.");
            }

            BigDecimal paidAmount = isCashPayment((SelectOption) cmbPaymentMethod.getSelectedItem())
                    ? parseMoney(txtPaidAmount.getText())
                    : order.getTotal();

            Sale result = controller.convertToSale(
                    idOrder,
                    idUser <= 0 ? 1 : idUser,
                    idPaymentMethod,
                    (String) cmbDocumentKind.getSelectedItem(),
                    paidAmount
            );

            String printMessage = "\nImpresion enviada a PDF24.";

            try {
                Sale printedSale = saleController.findById(result.getIdSale());
                voucherPrintService.printSale(printedSale);
            } catch (Exception printException) {
                printMessage = "\nVenta registrada, pero no se pudo imprimir en PDF24: " + printException.getMessage();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Orden convertida correctamente.\nComprobante: " + result.getVoucherCode()
                    + "\nTotal: S/ " + money(result.getTotal())
                    + "\nVuelto: S/ " + money(result.getChangeAmount())
                    + printMessage,
                    "Orden convertida",
                    JOptionPane.INFORMATION_MESSAGE
            );

            notifyStockAlertsChanged();
            loadData();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void cancelOrder(int idOrder) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Desea anular esta orden?",
                "Confirmar anulacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.cancel(idOrder);
            JOptionPane.showMessageDialog(this, "Orden anulada correctamente.");
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void notifyStockAlertsChanged() {
        DashboardJFrame dashboard = DashboardWindowSupport.findDashboardFrame(this);

        if (dashboard != null) {
            dashboard.triggerStockAlertRefresh();
        }
    }

    private void loadSelectOptions(JComboBox<SelectOption> combo, List<SelectOption> options, String defaultText) {
        combo.removeAllItems();
        combo.addItem(SelectOption.empty(defaultText));

        for (SelectOption option : options) {
            combo.addItem(option);
        }
    }

    private int selectedId(JComboBox<SelectOption> combo) {
        SelectOption option = (SelectOption) combo.getSelectedItem();
        return option == null ? 0 : option.getId();
    }

    private boolean isCashPayment(SelectOption option) {
        if (option == null || option.getId() <= 0 || option.getName() == null) {
            return false;
        }

        String text = option.getName().trim().toLowerCase(Locale.ROOT);
        return text.contains("efectivo") || text.contains("cash");
    }

    private BigDecimal parseMoney(String value) throws Exception {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }

            return new BigDecimal(value.trim()).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw new Exception("Ingrese un monto valido.");
        }
    }

    private String money(BigDecimal value) {
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        return safeValue.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatDate(LocalDateTime value) {
        return value == null ? "-" : value.format(dateFormatter);
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private final class ActionsRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnView = createActionIconButton(FontAwesome.EYE, primaryColor, "Ver detalle");
        private final JButton btnEdit = createActionIconButton(FontAwesome.PENCIL, warningColor, "Editar");
        private final JButton btnConvert = createActionIconButton(FontAwesome.SHOPPING_CART, successColor, "Convertir a venta");
        private final JButton btnCancel = createActionIconButton(FontAwesome.TIMES, dangerColor, "Anular");

        private ActionsRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            add(btnView);
            add(btnEdit);
            add(btnConvert);
            add(btnCancel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            String status = table.getValueAt(row, 3) == null ? "" : table.getValueAt(row, 3).toString();
            boolean pending = "PENDIENTE".equalsIgnoreCase(status);
            btnEdit.setEnabled(pending);
            btnConvert.setEnabled(pending);
            btnCancel.setEnabled(pending);

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }

            return this;
        }
    }

    private final class ActionsEditor extends AbstractCellEditor implements TableCellEditor {

        private final JTable sourceTable;
        private final JPanel panel;
        private final JButton btnView;
        private final JButton btnEdit;
        private final JButton btnConvert;
        private final JButton btnCancel;
        private int row;

        private ActionsEditor(JTable table) {
            this.sourceTable = table;
            this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            this.panel.setOpaque(true);

            this.btnView = createActionIconButton(FontAwesome.EYE, primaryColor, "Ver detalle");
            this.btnEdit = createActionIconButton(FontAwesome.PENCIL, warningColor, "Editar");
            this.btnConvert = createActionIconButton(FontAwesome.SHOPPING_CART, successColor, "Convertir a venta");
            this.btnCancel = createActionIconButton(FontAwesome.TIMES, dangerColor, "Anular");

            this.btnView.addActionListener(e -> {
                fireEditingStopped();
                showOrderDetail(getCurrentId());
            });

            this.btnEdit.addActionListener(e -> {
                fireEditingStopped();
                openOrderFormDialog(getCurrentId());
            });

            this.btnConvert.addActionListener(e -> {
                fireEditingStopped();
                openConvertDialog(getCurrentId());
            });

            this.btnCancel.addActionListener(e -> {
                fireEditingStopped();
                cancelOrder(getCurrentId());
            });

            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnConvert);
            panel.add(btnCancel);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            String status = table.getValueAt(row, 3) == null ? "" : table.getValueAt(row, 3).toString();
            boolean pending = "PENDIENTE".equalsIgnoreCase(status);
            btnEdit.setEnabled(pending);
            btnConvert.setEnabled(pending);
            btnCancel.setEnabled(pending);
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        private int getCurrentId() {
            Object value = sourceTable.getValueAt(row, 0);
            return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value));
        }
    }

    private JButton createTableActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(58, text.length() * 8 + 16), 26));
        return button;
    }

    private JButton createActionIconButton(FontAwesome icon, Color color, String tooltip) {
        JButton button = new JButton(IconFontSwing.buildIcon(icon, 14, Color.WHITE));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(34, 30));
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private final class OrderFormDialog extends JDialog {

        private final Order existingOrder;
        private final boolean editing;
        private final Map<Integer, CartLine> cart = new LinkedHashMap<>();
        private final Map<Integer, SaleProductItem> productCache = new LinkedHashMap<>();

        private JTextField txtProductSearch;
        private JComboBox<SelectOption> cmbCategory;
        private JComboBox<SelectOption> cmbBrand;
        private JTable productTable;
        private DefaultTableModel productTableModel;

        private JComboBox<SelectOption> cmbDocumentType;
        private JTextField txtDocumentNumber;
        private JTextField txtCustomerName;
        private DatePicker dpExpectedDate;
        private JTextArea txtNotes;

        private JTable cartTable;
        private DefaultTableModel cartTableModel;

        private JLabel lblSubtotal;
        private JLabel lblIgv;
        private JLabel lblTotal;

        private Integer selectedClientId;

        private OrderFormDialog(Order existingOrder) {
            super((JFrame) SwingUtilities.getWindowAncestor(OrderJPanel.this), true);
            this.existingOrder = existingOrder;
            this.editing = existingOrder != null;

            setTitle(editing ? "Editar orden" : "Nueva orden");
            setSize(1380, 840);
            setLocationRelativeTo(OrderJPanel.this);
            setResizable(false);

            initUI();
            loadCombos();
            loadProducts();

            if (editing) {
                fillForm();
            }
        }

        private void initUI() {
            JPanel root = new JPanel(new BorderLayout(14, 14));
            root.setBackground(backgroundColor);
            root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JLabel lblTitle = new JLabel(editing ? "Editar orden pendiente" : "Registrar nueva orden");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitle.setForeground(textColor);
            root.add(lblTitle, BorderLayout.NORTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createProductPanel(), createSummaryPanel());
            splitPane.setResizeWeight(0.6);
            splitPane.setDividerLocation(760);
            splitPane.setBorder(BorderFactory.createEmptyBorder());
            root.add(splitPane, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttons.setBackground(backgroundColor);

            JButton btnSave = createButton(editing ? "Actualizar orden" : "Guardar orden", successColor);
            JButton btnClose = createButton("Cerrar", new Color(90, 90, 90));

            btnSave.addActionListener(e -> saveOrder());
            btnClose.addActionListener(e -> dispose());

            buttons.add(btnClose);
            buttons.add(btnSave);
            root.add(buttons, BorderLayout.SOUTH);

            setContentPane(root);
        }

        private JPanel createProductPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBackground(cardColor);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
            ));

            JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            toolbar.setBackground(cardColor);

            txtProductSearch = new JTextField();
            txtProductSearch.setPreferredSize(new Dimension(240, 34));
            txtProductSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            cmbCategory = new JComboBox<>();
            cmbCategory.setPreferredSize(new Dimension(180, 34));
            cmbBrand = new JComboBox<>();
            cmbBrand.setPreferredSize(new Dimension(180, 34));

            JButton btnSearch = createButton("Buscar", primaryColor);
            JButton btnClear = createButton("Limpiar", new Color(90, 90, 90));

            btnSearch.addActionListener(e -> loadProducts());
            btnClear.addActionListener(e -> {
                txtProductSearch.setText("");
                cmbCategory.setSelectedIndex(0);
                cmbBrand.setSelectedIndex(0);
                loadProducts();
            });

            toolbar.add(txtProductSearch);
            toolbar.add(cmbCategory);
            toolbar.add(cmbBrand);
            toolbar.add(btnSearch);
            toolbar.add(btnClear);

            productTableModel = new DefaultTableModel(
                    new Object[]{"ID", "Producto", "Categoria", "Marca", "Stock", "Precio", "Accion"},
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 6;
                }
            };

            productTable = new JTable(productTableModel);
            productTable.setRowHeight(42);
            productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            productTable.getColumnModel().getColumn(0).setPreferredWidth(55);
            productTable.getColumnModel().getColumn(1).setPreferredWidth(240);
            productTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            productTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            productTable.getColumnModel().getColumn(4).setPreferredWidth(70);
            productTable.getColumnModel().getColumn(5).setPreferredWidth(90);
            productTable.getColumnModel().getColumn(6).setPreferredWidth(90);
            productTable.getColumnModel().getColumn(6).setCellRenderer(new SingleButtonRenderer("Agregar", successColor));
            productTable.getColumnModel().getColumn(6).setCellEditor(new ProductAddEditor());

            JScrollPane scrollPane = new JScrollPane(productTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            panel.add(toolbar, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            return panel;
        }

        private JPanel createSummaryPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBackground(cardColor);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
            ));

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBackground(cardColor);

            cmbDocumentType = new JComboBox<>();
            cmbDocumentType.setPreferredSize(new Dimension(160, 34));

            txtDocumentNumber = new JTextField();
            txtDocumentNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            txtCustomerName = new JTextField();
            txtCustomerName.setEditable(false);
            txtCustomerName.setBackground(new Color(245, 245, 245));

            dpExpectedDate = createDatePicker();
            txtNotes = new JTextArea(4, 20);
            txtNotes.setLineWrap(true);
            txtNotes.setWrapStyleWord(true);
            txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            txtDocumentNumber.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    resolveCustomerPreview();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    resolveCustomerPreview();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    resolveCustomerPreview();
                }
            });

            cmbDocumentType.addActionListener(e -> resolveCustomerPreview());

            form.add(createInlineLabel("Tipo doc cliente"));
            form.add(createInlineLabel("Numero doc cliente"));
            form.add(cmbDocumentType);
            form.add(txtDocumentNumber);
            form.add(createInlineLabel("Cliente encontrado"));
            form.add(createInlineLabel("Entrega estimada"));
            form.add(txtCustomerName);
            form.add(dpExpectedDate);

            JPanel formWrapper = new JPanel(new BorderLayout(0, 8));
            formWrapper.setBackground(cardColor);
            formWrapper.add(form, BorderLayout.NORTH);

            JLabel lblNotes = createInlineLabel("Notas");
            JPanel notesPanel = new JPanel(new BorderLayout(0, 6));
            notesPanel.setBackground(cardColor);
            notesPanel.add(lblNotes, BorderLayout.NORTH);
            notesPanel.add(new JScrollPane(txtNotes), BorderLayout.CENTER);
            formWrapper.add(notesPanel, BorderLayout.CENTER);

            cartTableModel = new DefaultTableModel(
                    new Object[]{"ID", "Producto", "Cant", "P.U", "Total", "Accion"},
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 5;
                }
            };

            cartTable = new JTable(cartTableModel);
            cartTable.setRowHeight(40);
            cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            cartTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            cartTable.getColumnModel().getColumn(1).setPreferredWidth(210);
            cartTable.getColumnModel().getColumn(2).setPreferredWidth(60);
            cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
            cartTable.getColumnModel().getColumn(4).setPreferredWidth(90);
            cartTable.getColumnModel().getColumn(5).setPreferredWidth(90);
            cartTable.getColumnModel().getColumn(5).setCellRenderer(new SingleButtonRenderer("Editar", warningColor));
            cartTable.getColumnModel().getColumn(5).setCellEditor(new CartEditEditor());

            JScrollPane cartScroll = new JScrollPane(cartTable);
            cartScroll.setBorder(BorderFactory.createLineBorder(borderColor));

            lblSubtotal = createSummaryLabel();
            lblIgv = createSummaryLabel();
            lblTotal = createSummaryLabel();

            JPanel totals = new JPanel(new GridLayout(3, 2, 8, 8));
            totals.setBackground(cardColor);
            totals.add(createInlineLabel("Subtotal"));
            totals.add(lblSubtotal);
            totals.add(createInlineLabel("IGV"));
            totals.add(lblIgv);
            totals.add(createInlineLabel("Total"));
            totals.add(lblTotal);

            JPanel center = new JPanel(new BorderLayout(0, 12));
            center.setBackground(cardColor);
            center.add(formWrapper, BorderLayout.NORTH);
            center.add(cartScroll, BorderLayout.CENTER);
            center.add(totals, BorderLayout.SOUTH);

            panel.add(center, BorderLayout.CENTER);
            return panel;
        }

        private JLabel createSummaryLabel() {
            JLabel label = new JLabel("S/ 0.00");
            label.setHorizontalAlignment(JLabel.RIGHT);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return label;
        }

        private void loadCombos() {
            try {
                loadSelectOptions(cmbCategory, controller.listCategoryOptions(), "Todas las categorias");
                loadSelectOptions(cmbBrand, controller.listBrandOptions(), "Todas las marcas");
                loadSelectOptions(cmbDocumentType, controller.listDocumentTypeOptions(), "Seleccione");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void loadProducts() {
            try {
                String search = txtProductSearch.getText().trim();
                int idCategory = selectedId(cmbCategory);
                int idBrand = selectedId(cmbBrand);

                List<SaleProductItem> products = controller.listProducts(search, idCategory, idBrand);

                productCache.clear();
                productTableModel.setRowCount(0);

                for (SaleProductItem product : products) {
                    productCache.put(product.getIdProduct(), product);
                    productTableModel.addRow(new Object[]{
                        product.getIdProduct(),
                        safeText(product.getName(), "Producto"),
                        safeText(product.getCategoryName(), "-"),
                        safeText(product.getBrandName(), "-"),
                        product.getStock(),
                        "S/ " + money(product.getPrice()),
                        ""
                    });
                }
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void fillForm() {
            if (existingOrder == null) {
                return;
            }

            selectComboItemById(cmbDocumentType, existingOrder.getCustomerDocumentTypeId() == null ? 0 : existingOrder.getCustomerDocumentTypeId());
            txtDocumentNumber.setText(safeText(existingOrder.getCustomerDocumentNumber(), ""));
            txtCustomerName.setText(safeText(existingOrder.getCustomerName(), ""));
            txtNotes.setText(safeText(existingOrder.getNotes(), ""));
            selectedClientId = existingOrder.getIdClient();

            if (existingOrder.getExpectedDate() != null) {
                dpExpectedDate.setDate(existingOrder.getExpectedDate().toLocalDate());
            }

            for (OrderDetail detail : existingOrder.getDetails()) {
                SaleProductItem product = productCache.get(detail.getIdProduct());
                if (product != null) {
                    cart.put(detail.getIdProduct(), new CartLine(product, detail.getQuantity()));
                } else {
                    cart.put(detail.getIdProduct(), new CartLine(
                            detail.getIdProduct(),
                            safeText(detail.getProductName(), "Producto"),
                            detail.getUnitPrice() == null ? BigDecimal.ZERO : detail.getUnitPrice(),
                            detail.getQuantity(),
                            detail.getQuantity()
                    ));
                }
            }

            refreshCartTable();
            updateTotals();
        }

        private void resolveCustomerPreview() {
            int idDocumentType = selectedId(cmbDocumentType);
            String documentNumber = txtDocumentNumber.getText() == null ? "" : txtDocumentNumber.getText().trim();

            if (idDocumentType <= 0 || documentNumber.isEmpty()) {
                selectedClientId = null;
                txtCustomerName.setText("");
                return;
            }

            try {
                Client client = controller.findActiveClientByDocument(idDocumentType, documentNumber);
                if (client == null) {
                    selectedClientId = null;
                    txtCustomerName.setText("");
                    return;
                }

                selectedClientId = client.getIdClient();
                txtCustomerName.setText(client.getFullName());
            } catch (Exception e) {
                selectedClientId = null;
                txtCustomerName.setText("");
            }
        }

        private void addProductToCart(int idProduct) {
            SaleProductItem product = productCache.get(idProduct);

            if (product == null) {
                showError("No se encontro el producto.");
                return;
            }

            int currentQuantity = cart.containsKey(idProduct) ? cart.get(idProduct).quantity : 0;
            String input = JOptionPane.showInputDialog(
                    this,
                    "Cantidad para " + product.getName(),
                    currentQuantity <= 0 ? "1" : String.valueOf(currentQuantity + 1)
            );

            if (input == null) {
                return;
            }

            try {
                int quantity = Integer.parseInt(input.trim());

                if (quantity <= 0) {
                    throw new Exception("La cantidad debe ser mayor a cero.");
                }

                if (quantity > product.getStock()) {
                    throw new Exception("No hay stock suficiente.");
                }

                cart.put(idProduct, new CartLine(product, quantity));
                refreshCartTable();
                updateTotals();

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void editCartLine(int idProduct) {
            CartLine line = cart.get(idProduct);

            if (line == null) {
                return;
            }

            String input = JOptionPane.showInputDialog(
                    this,
                    "Nueva cantidad para " + line.name + " (0 para quitar)",
                    String.valueOf(line.quantity)
            );

            if (input == null) {
                return;
            }

            try {
                int quantity = Integer.parseInt(input.trim());

                if (quantity <= 0) {
                    cart.remove(idProduct);
                } else {
                    if (line.stock > 0 && quantity > line.stock) {
                        throw new Exception("No hay stock suficiente.");
                    }

                    line.quantity = quantity;
                    cart.put(idProduct, line);
                }

                refreshCartTable();
                updateTotals();

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void refreshCartTable() {
            cartTableModel.setRowCount(0);

            for (CartLine line : cart.values()) {
                cartTableModel.addRow(new Object[]{
                    line.idProduct,
                    line.name,
                    line.quantity,
                    "S/ " + money(line.price),
                    "S/ " + money(line.getLineTotal()),
                    ""
                });
            }
        }

        private void updateTotals() {
            BigDecimal total = BigDecimal.ZERO;

            for (CartLine line : cart.values()) {
                total = total.add(line.getLineTotal());
            }

            total = total.setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = total.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
            BigDecimal igv = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);

            lblSubtotal.setText("S/ " + money(subtotal));
            lblIgv.setText("S/ " + money(igv));
            lblTotal.setText("S/ " + money(total));
        }

        private void saveOrder() {
            try {
                if (cart.isEmpty()) {
                    throw new Exception("Agregue productos a la orden.");
                }

                if (selectedClientId == null || selectedClientId <= 0) {
                    throw new Exception("No se encontro un cliente activo con ese documento.");
                }

                Order order = new Order();
                if (editing) {
                    order.setIdOrder(existingOrder.getIdOrder());
                }

                order.setIdUser(idUser <= 0 ? 1 : idUser);

                order.setCustomerDocumentTypeId(selectedId(cmbDocumentType));
                order.setCustomerDocumentNumber(txtDocumentNumber.getText() == null ? "" : txtDocumentNumber.getText().trim());
                order.setCustomerName(txtCustomerName.getText());
                order.setIdClient(selectedClientId);
                order.setNotes(txtNotes.getText() == null ? null : txtNotes.getText().trim());

                LocalDate expectedDate = dpExpectedDate.getDate();
                if (expectedDate != null) {
                    order.setExpectedDate(expectedDate.atStartOfDay());
                }

                for (CartLine line : cart.values()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setIdProduct(line.idProduct);
                    detail.setProductName(line.name);
                    detail.setQuantity(line.quantity);
                    detail.setOriginalUnitPrice(line.price);
                    detail.setUnitPrice(line.price);
                    detail.setSubtotalBeforeDiscount(line.getLineTotal());
                    detail.setSubtotal(line.getLineTotal());
                    detail.setDiscountType("NONE");
                    detail.setDiscountValue(BigDecimal.ZERO);
                    detail.setDiscountAmount(BigDecimal.ZERO);
                    order.getDetails().add(detail);
                }

                if (editing) {
                    controller.update(order);
                    JOptionPane.showMessageDialog(this, "Orden actualizada correctamente.");
                } else {
                    Order result = controller.create(order);
                    JOptionPane.showMessageDialog(this, "Orden registrada correctamente.\nCodigo: " + result.getOrderCode());
                }

                loadData();
                dispose();

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void selectComboItemById(JComboBox<SelectOption> combo, int id) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                SelectOption option = combo.getItemAt(i);
                if (option != null && option.getId() == id) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }

            if (combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
            }
        }

        private final class ProductAddEditor extends AbstractCellEditor implements TableCellEditor {

            private int row;
            private final JButton button = createTableActionButton("Agregar", successColor);

            private ProductAddEditor() {
                button.addActionListener(e -> {
                    fireEditingStopped();
                    Object value = productTable.getValueAt(row, 0);
                    int idProduct = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value));
                    addProductToCart(idProduct);
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                this.row = row;
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "";
            }
        }

        private final class CartEditEditor extends AbstractCellEditor implements TableCellEditor {

            private int row;
            private final JButton button = createTableActionButton("Editar", warningColor);

            private CartEditEditor() {
                button.addActionListener(e -> {
                    fireEditingStopped();
                    Object value = cartTable.getValueAt(row, 0);
                    int idProduct = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value));
                    editCartLine(idProduct);
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                this.row = row;
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "";
            }
        }
    }

    private final class SingleButtonRenderer extends JPanel implements TableCellRenderer {

        private final JButton button;

        private SingleButtonRenderer(String text, Color color) {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6));
            button = createTableActionButton(text, color);
            add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : UIManager.getColor("Table.background"));
            return this;
        }
    }

    private static final class CartLine {

        private final int idProduct;
        private final String name;
        private final BigDecimal price;
        private final int stock;
        private int quantity;

        private CartLine(SaleProductItem product, int quantity) {
            this(product.getIdProduct(), product.getName(), product.getPrice(), product.getStock(), quantity);
        }

        private CartLine(int idProduct, String name, BigDecimal price, int stock, int quantity) {
            this.idProduct = idProduct;
            this.name = name;
            this.price = price == null ? BigDecimal.ZERO : price;
            this.stock = stock;
            this.quantity = quantity;
        }

        private BigDecimal getLineTotal() {
            return price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
