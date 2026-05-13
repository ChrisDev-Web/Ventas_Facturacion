package Paneles;

import Controllers.SaleController;
import Models.Sale;
import Models.SaleDetail;
import Models.SaleHistoryStats;
import Models.SaleRanking;
import Models.SelectOption;
import Services.VoucherPrintService;
import Presentacion.SectionRefreshable;
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
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class SaleHistoryJPanel extends JPanel implements SectionRefreshable {

    private final SaleController controller;
    private final VoucherPrintService voucherPrintService;

    private JLabel lblTotalSales;
    private JLabel lblAverageTicket;
    private JLabel lblReturns;
    private JLabel lblDiscountCount;
    private JLabel lblRanking;

    private JTextField txtSearch;
    private DatePicker dpDateFrom;
    private DatePicker dpDateTo;
    private JComboBox<SelectOption> cmbPaymentMethod;
    private JComboBox<SelectOption> cmbUser;
    private JComboBox<Integer> cmbLimit;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblPagination;

    private int currentPage = 1;
    private int limit = 10;
    private int totalPages = 1;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color cardColor = Color.WHITE;
    private final Color primaryColor = new Color(30, 136, 229);
    private final Color purpleColor = new Color(111, 66, 193);
    private final Color successColor = new Color(46, 125, 50);
    private final Color borderColor = new Color(225, 225, 225);
    private final Color textColor = new Color(33, 33, 33);

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    public SaleHistoryJPanel() {
        this.controller = new SaleController();
        this.voucherPrintService = new VoucherPrintService();
        initUI();
        loadCombos();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createPaginationPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(backgroundColor);

        JLabel title = new JLabel("Historial de ventas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(textColor);

        JPanel stats = new JPanel(new GridLayout(1, 5, 12, 12));
        stats.setBackground(backgroundColor);

        lblTotalSales = createStatValue();
        lblAverageTicket = createStatValue();
        lblReturns = createStatValue();
        lblDiscountCount = createStatValue();
        lblRanking = createRankingValue();

        stats.add(createStatCard("Ventas", lblTotalSales));
        stats.add(createStatCard("Ticket promedio", lblAverageTicket));
        stats.add(createStatCard("Devoluciones", lblReturns));
        stats.add(createStatCard("Con descuento", lblDiscountCount));
        stats.add(createStatCard("Ranking vendedores", lblRanking));

        JPanel filters = new JPanel(new GridLayout(2, 1, 8, 8));
        filters.setBackground(cardColor);
        filters.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row1.setBackground(cardColor);

        dpDateFrom = createDatePicker();
        dpDateFrom.setDate(LocalDate.now());

        dpDateTo = createDatePicker();
        dpDateTo.setDate(LocalDate.now());

        cmbPaymentMethod = new JComboBox<>();
        cmbPaymentMethod.setPreferredSize(new Dimension(200, 36));

        cmbUser = new JComboBox<>();
        cmbUser.setPreferredSize(new Dimension(200, 36));

        row1.add(createLabel("Desde"));
        row1.add(dpDateFrom);
        row1.add(createLabel("Hasta"));
        row1.add(dpDateTo);
        row1.add(cmbPaymentMethod);
        row1.add(cmbUser);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row2.setBackground(cardColor);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(480, 36));

        JButton btnSearch = createButton("Buscar", primaryColor);
        JButton btnClear = createButton("Limpiar", new Color(90, 90, 90));

        btnSearch.addActionListener(e -> {
            currentPage = 1;
            loadData();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            dpDateFrom.setDate(LocalDate.now());
            dpDateTo.setDate(LocalDate.now());
            cmbPaymentMethod.setSelectedIndex(0);
            cmbUser.setSelectedIndex(0);
            currentPage = 1;
            loadData();
        });

        row2.add(txtSearch);
        row2.add(btnSearch);
        row2.add(btnClear);

        filters.add(row1);
        filters.add(row2);

        container.add(title, BorderLayout.NORTH);
        container.add(stats, BorderLayout.CENTER);
        container.add(filters, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(80, 80, 80));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createStatValue() {
        JLabel label = new JLabel("S/ 0.00");
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(textColor);
        return label;
    }

    private JLabel createRankingValue() {
        JLabel label = new JLabel("<html>-</html>");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(textColor);
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
    }

    private JLabel createLabel(String text) {
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
                new Object[]{"ID Venta", "Usuario", "Método Pago", "Estado", "Fecha", "Total", "Acciones"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setSelectionBackground(new Color(237, 231, 246));
        table.setSelectionForeground(textColor);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(230);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        table.getColumnModel().getColumn(6).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionsEditor(table));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

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

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(100, text.length() * 10 + 35), 36));
        return button;
    }

    private JButton createActionButton(FontAwesome icon, Color color, String tooltip) {
        JButton button = new JButton(IconFontSwing.buildIcon(icon, 15, Color.WHITE));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(34, 30));
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadCombos() {
        try {
            loadCombo(cmbPaymentMethod, controller.listPaymentMethodOptions(), "Todos los pagos");
            loadCombo(cmbUser, controller.listUserOptions(), "Todos los usuarios");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadCombo(JComboBox<SelectOption> combo, List<SelectOption> list, String defaultText) {
        combo.removeAllItems();
        combo.addItem(SelectOption.empty(defaultText));

        for (SelectOption option : list) {
            combo.addItem(option);
        }
    }

    private int selectedId(JComboBox<SelectOption> combo) {
        SelectOption option = (SelectOption) combo.getSelectedItem();
        return option == null ? 0 : option.getId();
    }

    private void selectComboItemById(JComboBox<SelectOption> combo, int id) {
        if (combo.getItemCount() == 0) {
            return;
        }

        for (int i = 0; i < combo.getItemCount(); i++) {
            SelectOption option = combo.getItemAt(i);

            if (option != null && option.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }

        combo.setSelectedIndex(0);
    }

    private void loadData() {
        try {
            String search = txtSearch.getText().trim();
            LocalDate dateFrom = dpDateFrom.getDate();
            LocalDate dateTo = dpDateTo.getDate();
            int idPaymentMethod = selectedId(cmbPaymentMethod);
            int idUser = selectedId(cmbUser);

            int totalRecords = controller.countHistory(search, idPaymentMethod, idUser, dateFrom, dateTo);
            totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<Sale> list = controller.listHistory(search, idPaymentMethod, idUser, dateFrom, dateTo, currentPage, limit);

            tableModel.setRowCount(0);

            for (Sale sale : list) {
                tableModel.addRow(new Object[]{
                    "#" + sale.getIdSale(),
                    sale.getUserName(),
                    sale.getPaymentMethodName(),
                    sale.getStatus() == 1 ? "Conforme" : "Anulado",
                    formatDate(sale.getSaleDate()),
                    "S/ " + money(sale.getTotal()),
                    ""
                });
            }

            lblPagination.setText("Página " + currentPage + " de " + totalPages + " | Total: " + totalRecords);

            loadStats(search, idPaymentMethod, idUser, dateFrom, dateTo);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @Override
    public void refreshSectionData() {
        int currentPaymentMethodId = selectedId(cmbPaymentMethod);
        int currentUserId = selectedId(cmbUser);
        LocalDate currentDateFrom = dpDateFrom.getDate();
        LocalDate currentDateTo = dpDateTo.getDate();

        loadCombos();
        selectComboItemById(cmbPaymentMethod, currentPaymentMethodId);
        selectComboItemById(cmbUser, currentUserId);
        dpDateFrom.setDate(currentDateFrom);
        dpDateTo.setDate(currentDateTo);
        loadData();
    }

    private void loadStats(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        SaleHistoryStats stats = controller.getStats(search, idPaymentMethod, idUser, dateFrom, dateTo);

        lblTotalSales.setText("S/ " + money(stats.getTotalSales()));
        lblAverageTicket.setText("S/ " + money(stats.getAverageTicket()));
        lblReturns.setText("S/ " + money(stats.getTotalReturns()));
        lblDiscountCount.setText(String.valueOf(stats.getDiscountSalesCount()));

        List<SaleRanking> ranking = controller.getRanking(search, idPaymentMethod, idUser, dateFrom, dateTo);
        if (ranking.isEmpty()) {
            lblRanking.setText("<html>-</html>");
        } else {
            lblRanking.setText(buildRankingText(ranking));
        }
    }

    private String buildRankingText(List<SaleRanking> ranking) {
        StringBuilder builder = new StringBuilder("<html>");

        for (int i = 0; i < ranking.size(); i++) {
            SaleRanking item = ranking.get(i);

            if (i > 0) {
                builder.append("<br>");
            }

            builder.append(i + 1)
                    .append(". ")
                    .append(safeText(item.getUserName(), "Sin nombre"))
                    .append(" - S/ ")
                    .append(money(item.getTotalSales()));
        }

        builder.append("</html>");
        return builder.toString();
    }

    private LocalDate parseDate(String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new Exception("Formato de fecha inválido. Use yyyy-MM-dd.");
        }
    }

    private void showSaleDetail(int idSale) {
        try {
            Sale sale = controller.findById(idSale);

            if (sale != null) {
                JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
                dialog.setTitle("Detalle de venta");
                dialog.setSize(920, 680);
                dialog.setLocationRelativeTo(this);
                dialog.setResizable(false);
                dialog.setContentPane(createSaleDetailContent(dialog, sale));
                dialog.setVisible(true);
                return;
            }

            StringBuilder detailText = new StringBuilder();
            detailText.append("Comprobante: ").append(sale.getVoucherCode()).append("\n");
            detailText.append("Usuario: ").append(sale.getUserName()).append("\n");
            detailText.append("Método de pago: ").append(sale.getPaymentMethodName()).append("\n");
            detailText.append("Fecha: ").append(formatDate(sale.getSaleDate())).append("\n");

            detailText.append("Cliente: ")
                    .append(sale.getCustomerName() == null || sale.getCustomerName().trim().isEmpty()
                            ? "Cliente no registrado"
                            : sale.getCustomerName())
                    .append("\n");

            detailText.append("Documento cliente: ");

            if (sale.getCustomerDocumentTypeName() == null || sale.getCustomerDocumentNumber() == null) {
                detailText.append("-");
            } else {
                detailText.append(sale.getCustomerDocumentTypeName())
                        .append(" ")
                        .append(sale.getCustomerDocumentNumber());
            }

            detailText.append("\n\n");
            detailText.append("PRODUCTOS\n");

            for (SaleDetail detail : sale.getDetails()) {
                detailText.append("- ")
                        .append(detail.getProductName())
                        .append(" | Cant: ")
                        .append(detail.getQuantity())
                        .append(" | P.U: S/ ")
                        .append(money(detail.getUnitPrice()))
                        .append(" | Total: S/ ")
                        .append(money(detail.getSubtotal()))
                        .append("\n");
            }

            detailText.append("\nSubtotal: S/ ").append(money(sale.getSubtotal()));
            detailText.append("\nIGV: S/ ").append(money(sale.getIgvAmount()));
            detailText.append("\nTotal: S/ ").append(money(sale.getTotal()));
            detailText.append("\nPago con: S/ ").append(money(sale.getPaidAmount()));
            detailText.append("\nVuelto: S/ ").append(money(sale.getChangeAmount()));

            JTextArea area = new JTextArea(detailText.toString());
            area.setEditable(false);
            area.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setTitle("Detalle de venta");
            dialog.setSize(650, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.add(new JScrollPane(area), BorderLayout.CENTER);
            dialog.setVisible(true);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private JPanel createSaleDetailContent(JDialog dialog, Sale sale) {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(backgroundColor);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        root.add(createSaleDetailHeader(sale), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBackground(backgroundColor);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(createSaleInfoPanel(sale));
        body.add(Box.createVerticalStrut(16));
        body.add(createSaleProductsPanel(sale));
        body.add(Box.createVerticalStrut(16));
        body.add(createSaleSummaryPanel(sale));

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setBackground(backgroundColor);

        JButton btnClose = createButton("Cerrar", new Color(90, 90, 90));
        btnClose.setPreferredSize(new Dimension(130, 38));
        btnClose.addActionListener(e -> dialog.dispose());
        footer.add(btnClose);

        root.add(scrollPane, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createSaleDetailHeader(Sale sale) {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setBackground(purpleColor);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(purpleColor.darker()),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblTitle = new JLabel(safeText(sale.getDocumentLabel(), "Detalle de venta"));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblCode = new JLabel(safeText(sale.getVoucherCode(), "-"));
        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblCode.setForeground(new Color(245, 245, 245));

        JLabel lblStatus = new JLabel(sale.getStatus() == 1 ? "Conforme" : "Anulado");
        lblStatus.setOpaque(true);
        lblStatus.setBackground(sale.getStatus() == 1 ? successColor : new Color(211, 47, 47));
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(lblStatus, BorderLayout.EAST);

        JLabel lblSubtitle = new JLabel("Resumen de la venta y productos registrados");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(232, 226, 247));

        header.add(topRow, BorderLayout.NORTH);
        header.add(lblCode, BorderLayout.CENTER);
        header.add(lblSubtitle, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createSaleInfoPanel(Sale sale) {
        JPanel wrapper = createDetailCard();
        wrapper.setLayout(new BorderLayout(0, 14));

        JLabel title = new JLabel("Informacion general");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(textColor);

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);
        grid.add(createInfoItem("Fecha", formatDate(sale.getSaleDate())));
        grid.add(createInfoItem("Usuario", safeText(sale.getUserName(), "-")));
        grid.add(createInfoItem("Metodo de pago", safeText(sale.getPaymentMethodName(), "-")));
        grid.add(createInfoItem("Cliente", buildCustomerLabel(sale)));
        grid.add(createInfoItem("Documento", buildCustomerDocumentLabel(sale)));
        grid.add(createInfoItem("Items", String.valueOf(sale.getDetails() == null ? 0 : sale.getDetails().size())));

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createInfoItem(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setForeground(new Color(110, 110, 110));

        JLabel lblValue = new JLabel("<html>" + safeText(value, "-") + "</html>");
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblValue.setForeground(textColor);

        panel.add(lblLabel, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSaleProductsPanel(Sale sale) {
        JPanel wrapper = createDetailCard();
        wrapper.setLayout(new BorderLayout(0, 14));

        JLabel title = new JLabel("Productos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(textColor);

        DefaultTableModel productModel = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "P.U.", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (sale.getDetails() != null) {
            for (SaleDetail detail : sale.getDetails()) {
                productModel.addRow(new Object[]{
                    safeText(detail.getProductName(), "-"),
                    detail.getQuantity(),
                    "S/ " + money(detail.getUnitPrice()),
                    "S/ " + money(detail.getSubtotal())
                });
            }
        }

        JTable productTable = new JTable(productModel);
        productTable.setRowHeight(34);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        productTable.setEnabled(false);
        productTable.setShowVerticalLines(false);
        productTable.setGridColor(new Color(230, 230, 230));
        productTable.getColumnModel().getColumn(0).setPreferredWidth(430);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(110);

        JScrollPane tableScroll = new JScrollPane(productTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(borderColor));
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.setPreferredSize(new Dimension(0, 220));

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(tableScroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createSaleSummaryPanel(Sale sale) {
        JPanel wrapper = createDetailCard();
        wrapper.setLayout(new BorderLayout(0, 14));

        JLabel title = new JLabel("Resumen de importes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(textColor);

        JPanel grid = new JPanel(new GridLayout(1, 5, 12, 12));
        grid.setOpaque(false);
        grid.add(createAmountCard("Subtotal", "S/ " + money(sale.getSubtotal()), false));
        grid.add(createAmountCard("IGV", "S/ " + money(sale.getIgvAmount()), false));
        grid.add(createAmountCard("Pago con", "S/ " + money(sale.getPaidAmount()), false));
        grid.add(createAmountCard("Vuelto", "S/ " + money(sale.getChangeAmount()), false));
        grid.add(createAmountCard("Total", "S/ " + money(sale.getTotal()), true));

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createAmountCard(String label, String value, boolean highlight) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(highlight ? purpleColor : new Color(248, 249, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(highlight ? purpleColor.darker() : new Color(230, 233, 240)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setForeground(highlight ? new Color(236, 231, 247) : new Color(110, 110, 110));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, highlight ? 22 : 18));
        lblValue.setForeground(highlight ? Color.WHITE : textColor);

        panel.add(lblLabel, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailCard() {
        JPanel panel = new JPanel();
        panel.setBackground(cardColor);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private String buildCustomerLabel(Sale sale) {
        return safeText(sale.getCustomerName(), "Cliente no registrado");
    }

    private String buildCustomerDocumentLabel(Sale sale) {
        if (sale.getCustomerDocumentTypeName() == null || sale.getCustomerDocumentNumber() == null
                || sale.getCustomerDocumentNumber().trim().isEmpty()) {
            return "Sin documento";
        }

        return sale.getCustomerDocumentTypeName() + " " + sale.getCustomerDocumentNumber();
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private void printSaleVoucher(int idSale) {
        try {
            Sale sale = controller.findById(idSale);
            voucherPrintService.printSale(sale);

            JOptionPane.showMessageDialog(
                    this,
                    "Impresion enviada a PDF24 correctamente.",
                    "Impresion enviada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private int getSelectedSaleId(JTable table, int row) {
        int modelRow = table.convertRowIndexToModel(row);
        String value = table.getModel().getValueAt(modelRow, 0).toString();
        return Integer.parseInt(value.replace("#", ""));
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "-";
        }
        return date.format(dateFormatter);
    }

    private String money(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private class ActionsRenderer extends JPanel implements TableCellRenderer {

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 8));
            setOpaque(true);
            add(createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle"));
            add(createActionButton(FontAwesome.PRINT, successColor, "Imprimir"));
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private int selectedId;

        public ActionsEditor(JTable table) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 8));
            panel.setBackground(Color.WHITE);

            JButton btnView = createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle");
            JButton btnPrint = createActionButton(FontAwesome.PRINT, successColor, "Imprimir");

            btnView.addActionListener(e -> {
                fireEditingStopped();
                showSaleDetail(selectedId);
            });

            btnPrint.addActionListener(e -> {
                fireEditingStopped();
                if (System.currentTimeMillis() >= 0) {
                    printSaleVoucher(selectedId);
                    return;
                }
                JOptionPane.showMessageDialog(SaleHistoryJPanel.this, "Aquí puedes agregar la impresión del comprobante.");
            });

            panel.add(btnView);
            panel.add(btnPrint);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column) {
            selectedId = getSelectedSaleId(table, row);
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.out.println("No se pudo cargar el LookAndFeel.");
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Historial de ventas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new SaleHistoryJPanel());
            frame.setVisible(true);
        });
    }
}
