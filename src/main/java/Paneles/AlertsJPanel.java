package Paneles;

import Controllers.AlertController;
import Models.StockAlert;
import Models.StockAlertSummary;
import Presentacion.SectionRefreshable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class AlertsJPanel extends JPanel implements SectionRefreshable {

    private final AlertController controller;

    private JTextField txtSearch;
    private JComboBox<String> cmbSeverity;
    private JComboBox<Integer> cmbLimit;

    private JLabel lblWarningCount;
    private JLabel lblCriticalCount;
    private JLabel lblUrgentCount;
    private JLabel lblTotalAlerts;
    private JLabel lblPagination;

    private JTable table;
    private DefaultTableModel tableModel;

    private int currentPage = 1;
    private int limit = 10;
    private int totalPages = 1;
    private String currentSearch = "";
    private String currentSeverity = "TODOS";

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color textColor = new Color(33, 33, 33);
    private final Color borderColor = new Color(220, 220, 220);
    private final Color primaryColor = new Color(30, 136, 229);
    private final Color warningColor = new Color(245, 158, 11);
    private final Color criticalColor = new Color(220, 38, 38);
    private final Color urgentColor = new Color(153, 27, 27);
    private final Color neutralColor = new Color(79, 70, 229);

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    public AlertsJPanel() {
        this.controller = new AlertController();
        initUI();
        loadData();
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

        JLabel lblTitle = new JLabel("Alertas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Monitorea productos con stock bajo, critico o urgente.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 12));
        statsPanel.setBackground(backgroundColor);

        lblWarningCount = createMetricValueLabel();
        lblCriticalCount = createMetricValueLabel();
        lblUrgentCount = createMetricValueLabel();
        lblTotalAlerts = createMetricValueLabel();

        statsPanel.add(createMetricCard("Advertencia", lblWarningCount, warningColor));
        statsPanel.add(createMetricCard("Stock critico", lblCriticalCount, criticalColor));
        statsPanel.add(createMetricCard("Stock urgente", lblUrgentCount, urgentColor));
        statsPanel.add(createMetricCard("Total alertas", lblTotalAlerts, neutralColor));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(backgroundColor);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbSeverity = new JComboBox<>(new String[]{
            "Todos",
            "Advertencia (6 a 10)",
            "Critico (1 a 5)",
            "Urgente (0)"
        });
        cmbSeverity.setPreferredSize(new Dimension(190, 36));
        cmbSeverity.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbLimit = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbLimit.setPreferredSize(new Dimension(80, 36));
        cmbLimit.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createToolbarButton("Buscar", FontAwesome.SEARCH, primaryColor);
        JButton btnClear = createToolbarButton("Limpiar", FontAwesome.REFRESH, new Color(90, 90, 90));

        btnSearch.addActionListener(e -> {
            currentSearch = txtSearch.getText().trim();
            currentSeverity = selectedSeverity();
            currentPage = 1;
            loadData();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbSeverity.setSelectedIndex(0);
            currentSearch = "";
            currentSeverity = "TODOS";
            currentPage = 1;
            loadData();
        });

        cmbLimit.addActionListener(e -> {
            limit = (Integer) cmbLimit.getSelectedItem();
            currentPage = 1;
            loadData();
        });

        toolbar.add(txtSearch);
        toolbar.add(cmbSeverity);
        toolbar.add(btnSearch);
        toolbar.add(btnClear);
        toolbar.add(new JLabel("Mostrar:"));
        toolbar.add(cmbLimit);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(statsPanel, BorderLayout.CENTER);
        container.add(toolbar, BorderLayout.SOUTH);

        return container;
    }

    private JLabel createMetricValueLabel() {
        JLabel label = new JLabel("0");
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
                new Object[]{"ID", "Producto", "Categoria / Marca", "Stock", "Nivel", "Accion", "Actualizado"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setSelectionBackground(new Color(227, 242, 253));
        table.setSelectionForeground(textColor);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(240);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(280);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

        table.getColumnModel().getColumn(3).setCellRenderer(new StockRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new SeverityRenderer());

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

    @Override
    public void refreshSectionData() {
        loadData();
    }

    private void loadData() {
        try {
            loadSummary();
            tableModel.setRowCount(0);

            int totalRecords = controller.count(currentSearch, currentSeverity);
            totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<StockAlert> alerts = controller.list(currentSearch, currentSeverity, currentPage, limit);

            for (StockAlert alert : alerts) {
                tableModel.addRow(new Object[]{
                    alert.getIdProduct(),
                    safeText(alert.getProductName(), "-"),
                    safeText(alert.getCategoryName(), "-") + " / " + safeText(alert.getBrandName(), "-"),
                    alert.getStock(),
                    safeText(alert.getSeverityLabel(), "-"),
                    safeText(alert.getRecommendedAction(), "-"),
                    formatDate(alert.getAlertTime())
                });
            }

            lblPagination.setText("Pagina " + currentPage + " de " + totalPages + " | Total: " + totalRecords);

        } catch (Exception e) {
            showErrorRow(e.getMessage());
        }
    }

    private void loadSummary() throws Exception {
        StockAlertSummary summary = controller.getSummary();
        lblWarningCount.setText(String.valueOf(summary.getWarningCount()));
        lblCriticalCount.setText(String.valueOf(summary.getCriticalCount()));
        lblUrgentCount.setText(String.valueOf(summary.getUrgentCount()));
        lblTotalAlerts.setText(String.valueOf(summary.getTotalAlerts()));
    }

    private String selectedSeverity() {
        return switch (cmbSeverity.getSelectedIndex()) {
            case 1 -> "WARNING";
            case 2 -> "CRITICAL";
            case 3 -> "URGENT";
            default -> "TODOS";
        };
    }

    private JButton createToolbarButton(String text, FontAwesome icon, Color color) {
        JButton button = new JButton(text, IconFontSwing.buildIcon(icon, 14, Color.WHITE));
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

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }

        return dateFormatter.format(dateTime);
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private void showErrorRow(String message) {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"-", "Error", "-", "-", "-", safeText(message, "No se pudieron cargar las alertas."), "-"});
        lblPagination.setText("Pagina 1 de 1 | Total: 0");
    }

    private class StockRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            int stockValue = value instanceof Number number ? number.intValue() : 0;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setText(String.valueOf(stockValue));

            if (stockValue == 0) {
                setForeground(urgentColor);
            } else if (stockValue <= 5) {
                setForeground(criticalColor);
            } else {
                setForeground(warningColor);
            }
        }
    }

    private class SeverityRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            String text = safeText(value == null ? null : value.toString(), "-");
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setText(text);

            if (text.toUpperCase().contains("URGENTE")) {
                setForeground(urgentColor);
            } else if (text.toUpperCase().contains("CRIT")) {
                setForeground(criticalColor);
            } else {
                setForeground(warningColor);
            }
        }
    }
}
