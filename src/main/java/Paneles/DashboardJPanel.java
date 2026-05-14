package Paneles;

import Controllers.DashboardController;
import Models.DashboardAlertItem;
import Models.DashboardChartItem;
import Models.DashboardLatestSale;
import Models.DashboardSummary;
import Presentacion.SectionRefreshable;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class DashboardJPanel extends JPanel implements SectionRefreshable {

    private final DashboardController controller;
    private final Runnable openSaleHistoryAction;
    private final Runnable openAlertsAction;

    private String displayName;

    private JLabel lblWelcome;
    private JLabel lblClockTime;
    private JLabel lblClockDate;
    private JComboBox<String> cmbPeriod;
    private DatePicker dpDateFrom;
    private DatePicker dpDateTo;

    private MetricCard metricTotalSales;
    private MetricCard metricDaySales;
    private MetricCard metricOrders;
    private MetricCard metricClients;
    private MetricCard metricProducts;
    private MetricCard metricAverageTicket;

    private LineChartPanel salesByDayChart;
    private DonutChartPanel categoryChart;
    private HorizontalBarChartPanel topProductsChart;
    private VerticalBarChartPanel salesByHourChart;
    private ComparisonLineChartPanel comparisonChart;
    private DonutChartPanel paymentMethodChart;
    private JLabel lblSalesByDayTitle;
    private JLabel lblComparisonTitle;

    private JPanel latestSalesContainer;
    private JPanel alertsContainer;

    private Timer clockTimer;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color cardColor = Color.WHITE;
    private final Color borderColor = new Color(225, 230, 238);
    private final Color textColor = new Color(30, 38, 50);
    private final Color mutedText = new Color(100, 112, 126);
    private final Color primaryBlue = new Color(35, 132, 245);
    private final Color green = new Color(62, 181, 96);
    private final Color amber = new Color(250, 177, 53);
    private final Color violet = new Color(162, 91, 221);
    private final Color teal = new Color(35, 170, 180);
    private final Color red = new Color(228, 80, 96);
    private final Color softBlue = new Color(226, 241, 255);

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a", new Locale("es", "PE"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "PE"));
    private final DateTimeFormatter tableDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Color[] chartPalette = new Color[]{
        primaryBlue,
        green,
        amber,
        violet,
        teal,
        red,
        new Color(109, 128, 150)
    };

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    public DashboardJPanel(String displayName, Runnable openSaleHistoryAction, Runnable openAlertsAction) {
        this.controller = new DashboardController();
        this.displayName = safeText(displayName, "Usuario Demo");
        this.openSaleHistoryAction = openSaleHistoryAction;
        this.openAlertsAction = openAlertsAction;

        initUI();
        applyPeriodDates();
        startClock();
        loadData();
    }

    public void setDisplayName(String displayName) {
        this.displayName = safeText(displayName, "Usuario Demo");

        if (lblWelcome != null) {
            lblWelcome.setText("Bienvenido, " + this.displayName);
        }
    }

    @Override
    public void refreshSectionData() {
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        JPanel content = new JPanel();
        content.setBackground(backgroundColor);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createHeader());
        content.add(Box.createVerticalStrut(16));
        content.add(createMetricsPanel());
        content.add(Box.createVerticalStrut(14));
        content.add(createChartsRowOne());
        content.add(Box.createVerticalStrut(14));
        content.add(createChartsRowTwo());
        content.add(Box.createVerticalStrut(14));
        content.add(createBottomRow());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBackground(backgroundColor);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        header.setPreferredSize(new Dimension(0, 86));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(textColor);

        lblWelcome = new JLabel("Bienvenido, " + displayName);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setForeground(mutedText);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(lblWelcome);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filters.setOpaque(false);

        filters.add(createClockPanel());

        cmbPeriod = new JComboBox<>(new String[]{"Este mes", "Hoy", "Ultimos 7 dias", "Personalizado"});
        cmbPeriod.setPreferredSize(new Dimension(150, 40));
        cmbPeriod.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbPeriod.addActionListener(e -> {
            applyPeriodDates();
            loadData();
        });

        dpDateFrom = createDatePicker();
        dpDateTo = createDatePicker();

        JButton btnFilter = createButton("Filtrar", FontAwesome.FILTER, primaryBlue);
        btnFilter.setPreferredSize(new Dimension(118, 40));
        btnFilter.addActionListener(e -> {
            if (!"Personalizado".equals(cmbPeriod.getSelectedItem())) {
                cmbPeriod.setSelectedItem("Personalizado");
                return;
            }

            loadData();
        });

        filters.add(createFilterField("Periodo", cmbPeriod));
        filters.add(createFilterField("Desde", dpDateFrom));
        filters.add(createFilterField("Hasta", dpDateTo));
        filters.add(btnFilter);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(filters, BorderLayout.EAST);
        return header;
    }

    private JPanel createClockPanel() {
        RoundedPanel panel = new RoundedPanel(cardColor);
        panel.setLayout(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 14));
        panel.setPreferredSize(new Dimension(210, 58));

        JLabel icon = new JLabel(buildIcon(FontAwesome.CLOCK_O, 24, new Color(70, 88, 110)));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setPreferredSize(new Dimension(36, 40));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        lblClockTime = new JLabel();
        lblClockTime.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblClockTime.setForeground(textColor);

        lblClockDate = new JLabel();
        lblClockDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblClockDate.setForeground(mutedText);

        textPanel.add(lblClockTime);
        textPanel.add(lblClockDate);

        panel.add(icon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        updateClock();
        return panel;
    }

    private JPanel createFilterField(String label, Component field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(field instanceof JComboBox ? 150 : 145, 58));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(mutedText);

        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private DatePicker createDatePicker() {
        DatePickerSettings settings = new DatePickerSettings(new Locale("es", "PE"));
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        settings.setAllowKeyboardEditing(false);

        DatePicker datePicker = new DatePicker(settings);
        datePicker.setPreferredSize(new Dimension(145, 40));
        return datePicker;
    }

    private JPanel createMetricsPanel() {
        JPanel metrics = new JPanel(new GridLayout(1, 6, 12, 0));
        metrics.setBackground(backgroundColor);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
        metrics.setPreferredSize(new Dimension(0, 118));

        metricTotalSales = new MetricCard(FontAwesome.USD, new Color(40, 137, 245), new Color(225, 241, 255), "Ventas Totales");
        metricDaySales = new MetricCard(FontAwesome.SHOPPING_CART, new Color(67, 174, 76), new Color(229, 246, 231), "Ventas del Dia");
        metricOrders = new MetricCard(FontAwesome.CLIPBOARD, new Color(148, 93, 220), new Color(239, 230, 250), "Ordenes");
        metricClients = new MetricCard(FontAwesome.USERS, new Color(246, 168, 43), new Color(255, 243, 222), "Clientes Nuevos");
        metricProducts = new MetricCard(FontAwesome.CUBE, new Color(36, 148, 236), new Color(226, 243, 255), "Productos Vendidos");
        metricAverageTicket = new MetricCard(FontAwesome.CREDIT_CARD, new Color(225, 81, 104), new Color(255, 230, 235), "Ticket Promedio");

        metrics.add(metricTotalSales);
        metrics.add(metricDaySales);
        metrics.add(metricOrders);
        metrics.add(metricClients);
        metrics.add(metricProducts);
        metrics.add(metricAverageTicket);

        return metrics;
    }

    private JPanel createChartsRowOne() {
        JPanel row = createChartRow(302);

        salesByDayChart = new LineChartPanel();
        categoryChart = new DonutChartPanel("Total");
        topProductsChart = new HorizontalBarChartPanel();

        row.add(createChartCard("Ventas por Dia", salesByDayChart));
        row.add(createChartCard("Ventas por Categoria", categoryChart));
        row.add(createChartCard("Top Productos", topProductsChart));
        return row;
    }

    private JPanel createChartsRowTwo() {
        JPanel row = createChartRow(302);

        salesByHourChart = new VerticalBarChartPanel();
        comparisonChart = new ComparisonLineChartPanel();
        paymentMethodChart = new DonutChartPanel("Total");

        row.add(createChartCard("Ventas por Hora", salesByHourChart));
        row.add(createChartCard("Ventas vs. Periodo Anterior", comparisonChart));
        row.add(createChartCard("Resumen por Metodo de Pago", paymentMethodChart));
        return row;
    }

    private JPanel createChartRow(int height) {
        JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
        row.setBackground(backgroundColor);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        row.setPreferredSize(new Dimension(0, height));
        return row;
    }

    private JPanel createChartCard(String title, JPanel chart) {
        RoundedPanel card = new RoundedPanel(cardColor);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(textColor);

        if ("Ventas por Dia".equals(title)) {
            lblSalesByDayTitle = lblTitle;
        } else if ("Ventas vs. Periodo Anterior".equals(title)) {
            lblComparisonTitle = lblTitle;
        }

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setBackground(backgroundColor);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 236));
        row.setPreferredSize(new Dimension(0, 236));

        row.add(createLatestSalesCard());
        row.add(createAlertsCard());
        return row;
    }

    private JPanel createLatestSalesCard() {
        RoundedPanel card = new RoundedPanel(cardColor);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Ultimas Ventas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(textColor);

        JButton btnHistory = createButton("Ver historial", FontAwesome.LIST_ALT, primaryBlue);
        btnHistory.setPreferredSize(new Dimension(136, 34));
        btnHistory.addActionListener(e -> {
            if (openSaleHistoryAction != null) {
                openSaleHistoryAction.run();
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnHistory, BorderLayout.EAST);

        latestSalesContainer = new JPanel();
        latestSalesContainer.setOpaque(false);
        latestSalesContainer.setLayout(new BoxLayout(latestSalesContainer, BoxLayout.Y_AXIS));

        card.add(header, BorderLayout.NORTH);
        card.add(latestSalesContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAlertsCard() {
        RoundedPanel card = new RoundedPanel(cardColor);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Alertas y Notificaciones");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(textColor);

        JButton btnAlerts = createButton("Ir a alertas", FontAwesome.BELL, new Color(60, 109, 218));
        btnAlerts.setPreferredSize(new Dimension(128, 34));
        btnAlerts.addActionListener(e -> {
            if (openAlertsAction != null) {
                openAlertsAction.run();
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnAlerts, BorderLayout.EAST);

        alertsContainer = new JPanel();
        alertsContainer.setOpaque(false);
        alertsContainer.setLayout(new BoxLayout(alertsContainer, BoxLayout.Y_AXIS));

        card.add(header, BorderLayout.NORTH);
        card.add(alertsContainer, BorderLayout.CENTER);
        return card;
    }

    private JButton createButton(String text, FontAwesome icon, Color color) {
        JButton button = new JButton(text, buildIcon(icon, 14, Color.WHITE));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setIconTextGap(8);
        return button;
    }

    private void applyPeriodDates() {
        if (cmbPeriod == null || dpDateFrom == null || dpDateTo == null) {
            return;
        }

        String selected = (String) cmbPeriod.getSelectedItem();
        LocalDate today = LocalDate.now();

        if ("Hoy".equals(selected)) {
            dpDateFrom.setDate(today);
            dpDateTo.setDate(today);
        } else if ("Ultimos 7 dias".equals(selected)) {
            dpDateFrom.setDate(today.minusDays(6));
            dpDateTo.setDate(today);
        } else if ("Este mes".equals(selected)) {
            dpDateFrom.setDate(today.withDayOfMonth(1));
            dpDateTo.setDate(today);
        }
    }

    private void loadData() {
        if (dpDateFrom == null || dpDateTo == null) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            LocalDate dateFrom = dpDateFrom.getDate();
            LocalDate dateTo = dpDateTo.getDate();

            updateChartTitles(dateFrom, dateTo);

            DashboardSummary summary = controller.getSummary(dateFrom, dateTo);
            updateMetrics(summary);

            salesByDayChart.setData(controller.listSalesByDay(dateFrom, dateTo));
            categoryChart.setData(controller.listSalesByCategory(dateFrom, dateTo));
            topProductsChart.setData(controller.listTopProducts(dateFrom, dateTo, 5));
            salesByHourChart.setData(controller.listSalesByHour(dateFrom, dateTo));
            comparisonChart.setData(controller.listSalesComparison(dateFrom, dateTo));
            paymentMethodChart.setData(controller.listPaymentMethods(dateFrom, dateTo));

            updateLatestSales(controller.listLatestSales(5));
            updateAlerts(controller.listAlerts(4));
        } catch (Exception e) {
            showError(e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void updateChartTitles(LocalDate dateFrom, LocalDate dateTo) {
        boolean singleDay = dateFrom != null && dateTo != null && dateFrom.isEqual(dateTo);

        if (lblSalesByDayTitle != null) {
            lblSalesByDayTitle.setText(singleDay ? "Ventas por Hora" : "Ventas por Dia");
        }

        if (lblComparisonTitle != null) {
            lblComparisonTitle.setText(singleDay ? "Ventas por Hora vs. Dia Anterior" : "Ventas vs. Periodo Anterior");
        }
    }

    private void updateMetrics(DashboardSummary summary) {
        metricTotalSales.setValue("S/ " + money(summary.getTotalSales()), summary.getTotalSalesGrowth());
        metricDaySales.setValue("S/ " + money(summary.getDaySales()), summary.getDaySalesGrowth());
        metricOrders.setValue(String.valueOf(summary.getOrders()), summary.getOrdersGrowth());
        metricClients.setValue(String.valueOf(summary.getNewClients()), summary.getNewClientsGrowth());
        metricProducts.setValue(String.valueOf(summary.getProductsSold()), summary.getProductsSoldGrowth());
        metricAverageTicket.setValue("S/ " + money(summary.getAverageTicket()), summary.getAverageTicketGrowth());
    }

    private void updateLatestSales(List<DashboardLatestSale> sales) {
        latestSalesContainer.removeAll();
        latestSalesContainer.add(createSaleHeaderRow());

        if (sales == null || sales.isEmpty()) {
            latestSalesContainer.add(createEmptyRow("Sin ventas registradas."));
        } else {
            for (int i = 0; i < sales.size(); i++) {
                latestSalesContainer.add(createSaleRow(sales.get(i), i));
            }
        }

        latestSalesContainer.revalidate();
        latestSalesContainer.repaint();
    }

    private JPanel createSaleHeaderRow() {
        JPanel row = createGridRow(new Color(248, 250, 253));
        row.add(createTableHeader("Fecha"));
        row.add(createTableHeader("Folio"));
        row.add(createTableHeader("Cliente"));
        row.add(createTableHeader("Total"));
        row.add(createTableHeader("Metodo"));
        return row;
    }

    private JPanel createSaleRow(DashboardLatestSale sale, int index) {
        JPanel row = createGridRow(index % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
        row.add(createTableCell(formatSaleDate(sale.getSaleDate()), false));
        row.add(createTableCell(safeText(sale.getVoucherCode(), "-"), true));
        row.add(createTableCell(safeText(sale.getCustomerName(), "Cliente no registrado"), false));
        row.add(createTableCell("S/ " + money(sale.getTotal()), true));
        row.add(createTableCell(safeText(sale.getPaymentMethodName(), "-"), false));
        return row;
    }

    private JPanel createGridRow(Color background) {
        JPanel row = new JPanel(new GridLayout(1, 5, 8, 0));
        row.setBackground(background);
        row.setBorder(BorderFactory.createEmptyBorder(7, 9, 7, 9));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 31));
        return row;
    }

    private JLabel createTableHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(80, 90, 105));
        return label;
    }

    private JLabel createTableCell(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        label.setForeground(textColor);
        return label;
    }

    private JPanel createEmptyRow(String text) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(mutedText);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        row.add(label, BorderLayout.CENTER);
        return row;
    }

    private void updateAlerts(List<DashboardAlertItem> alerts) {
        alertsContainer.removeAll();

        if (alerts == null || alerts.isEmpty()) {
            alertsContainer.add(createEmptyRow("Sin alertas por ahora."));
        } else {
            for (DashboardAlertItem alert : alerts) {
                alertsContainer.add(createAlertRow(alert));
                alertsContainer.add(Box.createVerticalStrut(7));
            }
        }

        alertsContainer.revalidate();
        alertsContainer.repaint();
    }

    private JPanel createAlertRow(DashboardAlertItem alert) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        Color color = alertColor(alert.getSeverity());
        FontAwesome iconCode = alertIcon(alert.getSeverity());

        JLabel icon = new JLabel(buildIcon(iconCode, 18, color));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setPreferredSize(new Dimension(30, 30));

        JLabel message = new JLabel(safeText(alert.getMessage(), "-"));
        message.setFont(new Font("Segoe UI", Font.BOLD, 13));
        message.setForeground(textColor);

        JLabel time = new JLabel(formatAlertTime(alert.getAlertTime()));
        time.setFont(new Font("Segoe UI", Font.BOLD, 12));
        time.setForeground(new Color(60, 68, 80));
        time.setHorizontalAlignment(SwingConstants.RIGHT);
        time.setPreferredSize(new Dimension(82, 30));

        row.add(icon, BorderLayout.WEST);
        row.add(message, BorderLayout.CENTER);
        row.add(time, BorderLayout.EAST);
        return row;
    }

    private Color alertColor(String severity) {
        String value = safeText(severity, "INFO").toUpperCase(Locale.ROOT);

        if ("WARNING".equals(value)) {
            return amber;
        }

        if ("URGENT".equals(value)) {
            return red.darker();
        }

        if ("SUCCESS".equals(value)) {
            return green;
        }

        if ("CRITICAL".equals(value)) {
            return red;
        }

        return primaryBlue;
    }

    private FontAwesome alertIcon(String severity) {
        String value = safeText(severity, "INFO").toUpperCase(Locale.ROOT);

        if ("WARNING".equals(value) || "CRITICAL".equals(value) || "URGENT".equals(value)) {
            return FontAwesome.EXCLAMATION_TRIANGLE;
        }

        if ("SUCCESS".equals(value)) {
            return FontAwesome.CHECK_CIRCLE;
        }

        return FontAwesome.INFO_CIRCLE;
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();

        if (lblClockTime != null) {
            lblClockTime.setText(now.format(timeFormatter).toUpperCase(Locale.ROOT));
        }

        if (lblClockDate != null) {
            lblClockDate.setText(now.format(dateFormatter));
        }
    }

    private String formatSaleDate(LocalDateTime date) {
        if (date == null) {
            return "-";
        }

        return date.format(tableDateFormatter);
    }

    private String formatAlertTime(LocalDateTime date) {
        if (date == null) {
            return "--:--";
        }

        return date.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String money(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }

        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private String shortMoney(BigDecimal value) {
        if (value == null) {
            return "S/ 0";
        }

        BigDecimal abs = value.abs();

        if (abs.compareTo(new BigDecimal("1000000")) >= 0) {
            return "S/ " + value.divide(new BigDecimal("1000000"), 1, RoundingMode.HALF_UP) + "M";
        }

        if (abs.compareTo(new BigDecimal("1000")) >= 0) {
            return "S/ " + value.divide(new BigDecimal("1000"), 1, RoundingMode.HALF_UP) + "k";
        }

        return "S/ " + value.setScale(0, RoundingMode.HALF_UP);
    }

    private String growthLabel(BigDecimal value) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value.setScale(1, RoundingMode.HALF_UP);
        String sign = normalized.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        return sign + normalized + "%";
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private static Icon buildIcon(FontAwesome icon, int size, Color color) {
        return IconFontSwing.buildIcon(icon, size, color);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Dashboard", JOptionPane.ERROR_MESSAGE);
    }

    private double amount(DashboardChartItem item) {
        if (item == null || item.getAmount() == null) {
            return 0;
        }

        return item.getAmount().doubleValue();
    }

    private double comparisonAmount(DashboardChartItem item) {
        if (item == null || item.getComparisonAmount() == null) {
            return 0;
        }

        return item.getComparisonAmount().doubleValue();
    }

    private double maxAmount(List<DashboardChartItem> data, boolean includeComparison) {
        double max = 0;

        if (data != null) {
            for (DashboardChartItem item : data) {
                max = Math.max(max, amount(item));

                if (includeComparison) {
                    max = Math.max(max, comparisonAmount(item));
                }
            }
        }

        return max <= 0 ? 1 : max;
    }

    private String truncate(Graphics2D g2, String text, int maxWidth) {
        String safe = safeText(text, "-");
        FontMetrics fm = g2.getFontMetrics();

        if (fm.stringWidth(safe) <= maxWidth) {
            return safe;
        }

        String ellipsis = "...";
        int width = fm.stringWidth(ellipsis);
        StringBuilder builder = new StringBuilder();

        for (char c : safe.toCharArray()) {
            if (fm.stringWidth(builder.toString()) + fm.charWidth(c) + width > maxWidth) {
                break;
            }

            builder.append(c);
        }

        return builder + ellipsis;
    }

    private void drawEmpty(Graphics2D g2, JPanel panel) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(mutedText);

        String text = "Sin datos para este periodo";
        FontMetrics fm = g2.getFontMetrics();
        int x = (panel.getWidth() - fm.stringWidth(text)) / 2;
        int y = (panel.getHeight() + fm.getAscent()) / 2 - 4;
        g2.drawString(text, Math.max(10, x), y);
    }

    private class MetricCard extends RoundedPanel {

        private final JLabel lblValue;
        private final JLabel lblGrowth;

        MetricCard(FontAwesome icon, Color iconColor, Color bubbleColor, String title) {
            super(cardColor);
            setLayout(new BorderLayout(13, 0));
            setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));

            IconBubble bubble = new IconBubble(icon, iconColor, bubbleColor);
            bubble.setPreferredSize(new Dimension(58, 58));

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblTitle.setForeground(new Color(74, 85, 100));

            lblValue = new JLabel("0");
            lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblValue.setForeground(textColor);

            lblGrowth = new JLabel("+0.0% vs. periodo anterior");
            lblGrowth.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblGrowth.setForeground(green);

            textPanel.add(Box.createVerticalGlue());
            textPanel.add(lblTitle);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(lblValue);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(lblGrowth);
            textPanel.add(Box.createVerticalGlue());

            add(bubble, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        void setValue(String value, BigDecimal growth) {
            lblValue.setText(value);
            lblGrowth.setText(growthLabel(growth) + " vs. periodo anterior");
            lblGrowth.setForeground(growth != null && growth.compareTo(BigDecimal.ZERO) < 0 ? red : green);
        }
    }

    private class IconBubble extends JPanel {

        private final Icon icon;
        private final Color bubbleColor;

        IconBubble(FontAwesome iconCode, Color iconColor, Color bubbleColor) {
            this.icon = buildIcon(iconCode, 25, iconColor);
            this.bubbleColor = bubbleColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 2;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(bubbleColor);
            g2.fillOval(x, y, size, size);

            int iconX = (getWidth() - icon.getIconWidth()) / 2;
            int iconY = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, iconX, iconY);
            g2.dispose();
        }
    }

    private class LineChartPanel extends JPanel {

        private List<DashboardChartItem> data = new ArrayList<>();

        LineChartPanel() {
            setOpaque(false);
        }

        void setData(List<DashboardChartItem> data) {
            this.data = data == null ? new ArrayList<>() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data.isEmpty()) {
                drawEmpty(g2, this);
                g2.dispose();
                return;
            }

            int left = 58;
            int right = 16;
            int top = 14;
            int bottom = 42;
            int plotW = getWidth() - left - right;
            int plotH = getHeight() - top - bottom;
            double max = maxAmount(data, false);

            drawGrid(g2, left, top, plotW, plotH, max);
            drawLine(g2, data, left, top, plotW, plotH, max, primaryBlue, false);
            drawXAxisLabels(g2, data, left, top, plotW, plotH);

            g2.dispose();
        }
    }

    private class ComparisonLineChartPanel extends JPanel {

        private List<DashboardChartItem> data = new ArrayList<>();

        ComparisonLineChartPanel() {
            setOpaque(false);
        }

        void setData(List<DashboardChartItem> data) {
            this.data = data == null ? new ArrayList<>() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data.isEmpty()) {
                drawEmpty(g2, this);
                g2.dispose();
                return;
            }

            int left = 58;
            int right = 16;
            int top = 14;
            int bottom = 55;
            int plotW = getWidth() - left - right;
            int plotH = getHeight() - top - bottom;
            double max = maxAmount(data, true);

            drawGrid(g2, left, top, plotW, plotH, max);
            drawLine(g2, data, left, top, plotW, plotH, max, primaryBlue, false);
            drawComparisonLine(g2, data, left, top, plotW, plotH, max);
            drawXAxisLabels(g2, data, left, top, plotW, plotH);
            drawLegend(g2, left + 98, top + plotH + 36);

            g2.dispose();
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(primaryBlue);
            g2.drawLine(x, y - 4, x + 22, y - 4);
            g2.drawString("Periodo Actual", x + 28, y);

            g2.setColor(new Color(160, 174, 190));
            Stroke old = g2.getStroke();
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{5f, 5f}, 0f));
            g2.drawLine(x + 152, y - 4, x + 174, y - 4);
            g2.setStroke(old);
            g2.drawString("Periodo Anterior", x + 180, y);
        }
    }

    private class VerticalBarChartPanel extends JPanel {

        private List<DashboardChartItem> data = new ArrayList<>();

        VerticalBarChartPanel() {
            setOpaque(false);
        }

        void setData(List<DashboardChartItem> data) {
            this.data = data == null ? new ArrayList<>() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data.isEmpty()) {
                drawEmpty(g2, this);
                g2.dispose();
                return;
            }

            int left = 50;
            int right = 14;
            int top = 14;
            int bottom = 42;
            int plotW = getWidth() - left - right;
            int plotH = getHeight() - top - bottom;
            double max = maxAmount(data, false);

            drawGrid(g2, left, top, plotW, plotH, max);

            int count = data.size();
            int slot = Math.max(8, plotW / Math.max(1, count));
            int barW = Math.max(5, Math.min(22, slot - 5));

            for (int i = 0; i < count; i++) {
                double value = amount(data.get(i));
                int h = (int) Math.round((value / max) * plotH);
                int x = left + i * slot + (slot - barW) / 2;
                int y = top + plotH - h;

                g2.setColor(primaryBlue);
                g2.fillRoundRect(x, y, barW, h, 5, 5);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(104, 116, 130));

            for (int i = 0; i < count; i += Math.max(1, count / 8)) {
                String label = safeText(data.get(i).getLabel(), "");
                int slotX = left + i * slot + slot / 2;
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, slotX - fm.stringWidth(label) / 2, top + plotH + 18);
            }

            g2.dispose();
        }
    }

    private class HorizontalBarChartPanel extends JPanel {

        private List<DashboardChartItem> data = new ArrayList<>();

        HorizontalBarChartPanel() {
            setOpaque(false);
        }

        void setData(List<DashboardChartItem> data) {
            this.data = data == null ? new ArrayList<>() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data.isEmpty()) {
                drawEmpty(g2, this);
                g2.dispose();
                return;
            }

            int left = 124;
            int right = 72;
            int top = 20;
            int rowH = 33;
            int barH = 17;
            int plotW = getWidth() - left - right;
            double max = maxAmount(data, false);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            for (int i = 0; i < data.size(); i++) {
                DashboardChartItem item = data.get(i);
                int y = top + i * rowH;
                int barW = (int) Math.round((amount(item) / max) * plotW);

                g2.setColor(new Color(80, 90, 104));
                g2.drawString(truncate(g2, item.getLabel(), left - 12), 4, y + 14);

                g2.setColor(new Color(235, 240, 247));
                g2.fillRoundRect(left, y, plotW, barH, 6, 6);

                g2.setColor(primaryBlue);
                g2.fillRoundRect(left, y, Math.max(3, barW), barH, 6, 6);

                g2.setColor(new Color(100, 112, 126));
                g2.drawString(shortMoney(item.getAmount()), left + barW + 8, y + 14);
            }

            g2.dispose();
        }
    }

    private class DonutChartPanel extends JPanel {

        private final String centerTitle;
        private List<DashboardChartItem> data = new ArrayList<>();

        DonutChartPanel(String centerTitle) {
            this.centerTitle = centerTitle;
            setOpaque(false);
        }

        void setData(List<DashboardChartItem> data) {
            this.data = data == null ? new ArrayList<>() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double total = 0;
            for (DashboardChartItem item : data) {
                total += amount(item);
            }

            if (data.isEmpty() || total <= 0) {
                drawEmpty(g2, this);
                g2.dispose();
                return;
            }

            int size = Math.min(getHeight() - 22, Math.max(130, getWidth() / 3));
            int x = 14;
            int y = (getHeight() - size) / 2;
            int start = 90;

            for (int i = 0; i < data.size(); i++) {
                DashboardChartItem item = data.get(i);
                int arc = (int) Math.round(amount(item) / total * 360);
                g2.setColor(chartPalette[i % chartPalette.length]);
                g2.fillArc(x, y, size, size, start, -arc);
                start -= arc;
            }

            int hole = (int) (size * 0.56);
            int hx = x + (size - hole) / 2;
            int hy = y + (size - hole) / 2;
            g2.setColor(cardColor);
            g2.fillOval(hx, hy, hole, hole);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(centerTitle, hx + (hole - fm.stringWidth(centerTitle)) / 2, hy + hole / 2 - 2);

            String totalText = shortMoney(BigDecimal.valueOf(total));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            fm = g2.getFontMetrics();
            g2.drawString(totalText, hx + (hole - fm.stringWidth(totalText)) / 2, hy + hole / 2 + 19);

            drawDonutLegend(g2, x + size + 24, y + 15, getWidth() - (x + size + 30), total);
            g2.dispose();
        }

        private void drawDonutLegend(Graphics2D g2, int x, int y, int width, double total) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            for (int i = 0; i < data.size() && i < 6; i++) {
                DashboardChartItem item = data.get(i);
                int itemY = y + i * 28;
                double percent = amount(item) / total * 100;

                g2.setColor(chartPalette[i % chartPalette.length]);
                g2.fillRect(x, itemY, 12, 12);

                g2.setColor(new Color(70, 82, 96));
                String label = truncate(g2, item.getLabel(), Math.max(70, width - 38));
                g2.drawString(label, x + 22, itemY + 10);

                g2.setColor(new Color(102, 113, 126));
                String detail = shortMoney(item.getAmount()) + " (" + BigDecimal.valueOf(percent).setScale(1, RoundingMode.HALF_UP) + "%)";
                g2.drawString(detail, x + 22, itemY + 24);
            }
        }
    }

    private void drawGrid(Graphics2D g2, int left, int top, int plotW, int plotH, double max) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        for (int i = 0; i <= 4; i++) {
            int y = top + (plotH * i / 4);
            double value = max - (max * i / 4);

            g2.setColor(new Color(230, 235, 242));
            g2.drawLine(left, y, left + plotW, y);

            g2.setColor(new Color(105, 116, 130));
            g2.drawString(shortMoney(BigDecimal.valueOf(value)), 0, y + 4);
        }
    }

    private void drawLine(Graphics2D g2, List<DashboardChartItem> source, int left, int top, int plotW, int plotH,
            double max, Color color, boolean comparison) {
        Path2D path = new Path2D.Double();
        int count = source.size();

        for (int i = 0; i < count; i++) {
            double value = comparison ? comparisonAmount(source.get(i)) : amount(source.get(i));
            int x = count == 1 ? left + plotW / 2 : left + (int) Math.round((double) i * plotW / (count - 1));
            int y = top + plotH - (int) Math.round((value / max) * plotH);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        Stroke oldStroke = g2.getStroke();
        if (comparison) {
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{6f, 6f}, 0f));
        } else {
            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
        g2.setColor(color);
        g2.draw(path);

        for (int i = 0; i < count; i++) {
            double value = comparison ? comparisonAmount(source.get(i)) : amount(source.get(i));
            int x = count == 1 ? left + plotW / 2 : left + (int) Math.round((double) i * plotW / (count - 1));
            int y = top + plotH - (int) Math.round((value / max) * plotH);
            g2.fillOval(x - 3, y - 3, 6, 6);
        }

        g2.setStroke(oldStroke);
    }

    private void drawComparisonLine(Graphics2D g2, List<DashboardChartItem> source, int left, int top, int plotW, int plotH,
            double max) {
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{6f, 6f}, 0f));
        drawLine(g2, source, left, top, plotW, plotH, max, new Color(160, 174, 190), true);
        g2.setStroke(oldStroke);
    }

    private void drawXAxisLabels(Graphics2D g2, List<DashboardChartItem> source, int left, int top, int plotW, int plotH) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(104, 116, 130));

        int count = source.size();
        int step = Math.max(1, count / 7);

        for (int i = 0; i < count; i += step) {
            String label = safeText(source.get(i).getLabel(), "");
            int x = count == 1 ? left + plotW / 2 : left + (int) Math.round((double) i * plotW / (count - 1));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, x - fm.stringWidth(label) / 2, top + plotH + 20);
        }
    }

    private class RoundedPanel extends JPanel {

        private final Color fillColor;

        RoundedPanel(Color fillColor) {
            this.fillColor = fillColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
