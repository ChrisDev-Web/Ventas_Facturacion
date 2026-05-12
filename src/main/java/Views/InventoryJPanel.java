package Views;

import Controllers.InventoryController;
import Controllers.StockMovementController;
import Models.InventoryMetrics;
import Models.InventoryProduct;
import Models.StockMovement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class InventoryJPanel extends JPanel implements SectionRefreshable {

    private final InventoryController controller;
    private final StockMovementController movementController;

    private JTextField txtSearch;
    private JComboBox<String> cmbStockFilter;
    private JComboBox<Integer> cmbLimit;
    private JLabel lblPagination;
    private JLabel lblInventoryCost;
    private JLabel lblInventorySale;
    private JLabel lblUnits;
    private JLabel lblStockAlerts;

    private JTable table;
    private DefaultTableModel tableModel;

    private int currentPage = 1;
    private int limit = 10;
    private int totalPages = 1;
    private String currentSearch = "";
    private String currentStockFilter = "TODOS";

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color primaryColor = new Color(30, 136, 229);
    private final Color successColor = new Color(46, 125, 50);
    private final Color dangerColor = new Color(198, 40, 40);
    private final Color warningColor = new Color(245, 124, 0);
    private final Color textColor = new Color(33, 33, 33);
    private final Color borderColor = new Color(220, 220, 220);

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    public InventoryJPanel() {
        this.controller = new InventoryController();
        this.movementController = new StockMovementController();
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createPaginationPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 18));
        container.setBackground(backgroundColor);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Inventario");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Consulta el stock actual de productos.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 12));
        statsPanel.setBackground(backgroundColor);

        lblInventoryCost = createMetricValueLabel();
        lblInventorySale = createMetricValueLabel();
        lblUnits = createMetricValueLabel();
        lblStockAlerts = createMetricValueLabel();

        statsPanel.add(createMetricCard("Valor costo", lblInventoryCost, primaryColor));
        statsPanel.add(createMetricCard("Valor venta", lblInventorySale, successColor));
        statsPanel.add(createMetricCard("Unidades", lblUnits, warningColor));
        statsPanel.add(createMetricCard("Alertas stock", lblStockAlerts, dangerColor));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(backgroundColor);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(280, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbStockFilter = new JComboBox<>(new String[]{"TODOS", "BAJO", "AGOTADO"});
        cmbStockFilter.setPreferredSize(new Dimension(130, 36));
        cmbStockFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbLimit = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbLimit.setPreferredSize(new Dimension(80, 36));
        cmbLimit.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createToolbarButton("Buscar", FontAwesome.SEARCH, primaryColor);
        JButton btnClear = createToolbarButton("Limpiar", FontAwesome.REFRESH, new Color(90, 90, 90));
        JButton btnMovement = createToolbarButton("Nuevo movimiento", FontAwesome.EXCHANGE, successColor);

        btnSearch.addActionListener(e -> {
            currentSearch = txtSearch.getText().trim();
            currentStockFilter = (String) cmbStockFilter.getSelectedItem();
            currentPage = 1;
            loadTable();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbStockFilter.setSelectedIndex(0);
            currentSearch = "";
            currentStockFilter = "TODOS";
            currentPage = 1;
            loadTable();
        });

        btnMovement.addActionListener(e -> {
            StockMovementJPanel panel = new StockMovementJPanel();
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setTitle("Movimientos de Stock");
            dialog.setSize(1200, 760);
            dialog.setLocationRelativeTo(this);
            dialog.setContentPane(panel);
            dialog.setVisible(true);
            loadTable();
        });

        cmbLimit.addActionListener(e -> {
            limit = (Integer) cmbLimit.getSelectedItem();
            currentPage = 1;
            loadTable();
        });

        toolbar.add(txtSearch);
        toolbar.add(cmbStockFilter);
        toolbar.add(btnSearch);
        toolbar.add(btnClear);
        toolbar.add(btnMovement);
        toolbar.add(new JLabel("Mostrar:"));
        toolbar.add(cmbLimit);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(statsPanel, BorderLayout.CENTER);
        container.add(toolbar, BorderLayout.SOUTH);

        return container;
    }

    private JLabel createMetricValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(textColor);
        return label;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
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

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(borderColor));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Categoría / Marca", "Stock", "Precio", "Acciones"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setSelectionBackground(new Color(227, 242, 253));
        table.setSelectionForeground(textColor);

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionsEditor(table));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(backgroundColor);

        JButton btnPrevious = createToolbarButton("Anterior", FontAwesome.CHEVRON_LEFT, new Color(90, 90, 90));
        JButton btnNext = createToolbarButton("Siguiente", FontAwesome.CHEVRON_RIGHT, new Color(90, 90, 90));

        lblPagination = new JLabel();
        lblPagination.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnPrevious.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadTable();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadTable();
            }
        });

        panel.add(btnPrevious);
        panel.add(lblPagination);
        panel.add(btnNext);

        return panel;
    }

    private void loadTable() {
        try {
            loadMetrics();
            tableModel.setRowCount(0);

            int totalRecords = controller.count(currentSearch, currentStockFilter);
            totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<InventoryProduct> list = controller.list(currentSearch, currentStockFilter, currentPage, limit);

            for (InventoryProduct product : list) {
                tableModel.addRow(new Object[]{
                    product.getIdProduct(),
                    product.getName(),
                    product.getCategoryName() + " / " + product.getBrandName(),
                    product.getStock(),
                    "S/ " + money(product.getPrice()),
                    ""
                });
            }

            lblPagination.setText("Página " + currentPage + " de " + totalPages + " | Total: " + totalRecords);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadMetrics() throws Exception {
        InventoryMetrics metrics = controller.getMetrics();

        lblInventoryCost.setText("S/ " + money(metrics.getTotalCostValue()));
        lblInventorySale.setText("S/ " + money(metrics.getTotalSaleValue()));
        lblUnits.setText(metrics.getTotalUnits() + " und. / " + metrics.getActiveProducts() + " prod.");
        lblStockAlerts.setText(metrics.getOutOfStockProducts() + " agot. / " + metrics.getLowStockProducts() + " bajos");
    }

    private void openDetail(int idProduct) {
        try {
            InventoryProduct product = controller.findById(idProduct);

            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            String text = ""
                    + "ID: " + product.getIdProduct() + "\n"
                    + "Producto: " + nullToDash(product.getName()) + "\n"
                    + "Descripción: " + nullToDash(product.getDescription()) + "\n"
                    + "Categoría: " + nullToDash(product.getCategoryName()) + "\n"
                    + "Marca: " + nullToDash(product.getBrandName()) + "\n"
                    + "Costo: S/ " + money(product.getCost()) + "\n"
                    + "Precio: S/ " + money(product.getPrice()) + "\n"
                    + "Stock actual: " + product.getStock() + "\n"
                    + "Estado: " + (product.getStatus() == 1 ? "Activo" : "Inactivo") + "\n"
                    + "Creado: " + formatDate(product.getCreatedAt()) + "\n"
                    + "Actualizado: " + formatDate(product.getUpdatedAt()) + "\n";

            area.setText(text);

            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setTitle("Detalle de inventario");
            dialog.setSize(520, 420);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.add(new JScrollPane(area), BorderLayout.CENTER);
            dialog.setVisible(true);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void openMovements(int idProduct) {
        try {
            List<StockMovement> movements = movementController.list("", idProduct, "TODOS", null, null, 1, 50);

            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Fecha", "Tipo", "Cantidad", "Referencia"},
                    0
            );

            for (StockMovement movement : movements) {
                model.addRow(new Object[]{
                    formatDate(movement.getMovementDate()),
                    movement.getMovementType(),
                    movement.getQuantity(),
                    nullToDash(movement.getReference())
                });
            }

            JTable movementTable = new JTable(model);
            movementTable.setRowHeight(38);
            movementTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            movementTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setTitle("Últimos movimientos del producto");
            dialog.setSize(700, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.add(new JScrollPane(movementTable), BorderLayout.CENTER);
            dialog.setVisible(true);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private JButton createToolbarButton(String text, FontAwesome icon, Color color) {
        JButton button = new JButton(text, createIcon(icon, 14, Color.WHITE));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(110, text.length() * 9 + 45), 36));
        button.setOpaque(true);
        return button;
    }

    private JButton createActionButton(FontAwesome icon, Color color, String tooltip) {
        JButton button = new JButton(createIcon(icon, 15, Color.WHITE));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(34, 30));
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    private Icon createIcon(FontAwesome icon, int size, Color color) {
        return IconFontSwing.buildIcon(icon, size, color);
    }

    private String money(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }

        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private String nullToDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value.trim();
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "-";
        }

        return date.format(dateFormatter);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void refreshSectionData() {
        loadTable();
    }

    private class ActionsRenderer extends JPanel implements TableCellRenderer {

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 7));
            setOpaque(true);
            add(createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle"));
            add(createActionButton(FontAwesome.LIST, warningColor, "Ver movimientos"));
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private int selectedId;

        public ActionsEditor(JTable table) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 7));
            panel.setBackground(Color.WHITE);

            JButton btnView = createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle");
            JButton btnMovements = createActionButton(FontAwesome.LIST, warningColor, "Ver movimientos");

            btnView.addActionListener(e -> {
                fireEditingStopped();
                openDetail(selectedId);
            });

            btnMovements.addActionListener(e -> {
                fireEditingStopped();
                openMovements(selectedId);
            });

            panel.add(btnView);
            panel.add(btnMovements);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column
        ) {
            int modelRow = table.convertRowIndexToModel(row);
            selectedId = (Integer) table.getModel().getValueAt(modelRow, 0);
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
            JFrame frame = new JFrame("Inventario");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 760);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new InventoryJPanel());
            frame.setVisible(true);
        });
    }
}
