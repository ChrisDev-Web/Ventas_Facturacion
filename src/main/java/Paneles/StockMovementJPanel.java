package Paneles;

import Controllers.StockMovementController;
import Models.SelectOption;
import Models.StockMovement;
import Presentacion.SectionRefreshable;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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

public class StockMovementJPanel extends JPanel implements SectionRefreshable {

    private final StockMovementController controller;

    private JTextField txtSearch;
    private DatePicker dpDateFrom;
    private DatePicker dpDateTo;
    private JComboBox<SelectOption> cmbProduct;
    private JComboBox<String> cmbMovementType;
    private JComboBox<Integer> cmbLimit;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblPagination;

    private int currentPage = 1;
    private int limit = 10;
    private int totalPages = 1;

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

    public StockMovementJPanel() {
        this.controller = new StockMovementController();
        initUI();
        loadCombos();
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

        JLabel lblTitle = new JLabel("Movimientos Stock");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Registra y consulta entradas, salidas y ajustes de stock.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(backgroundColor);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(220, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbProduct = new JComboBox<>();
        cmbProduct.setPreferredSize(new Dimension(220, 36));
        cmbProduct.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbMovementType = new JComboBox<>(new String[]{"TODOS", "ENTRADA", "SALIDA", "AJUSTE"});
        cmbMovementType.setPreferredSize(new Dimension(120, 36));
        cmbMovementType.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        dpDateFrom = createDatePicker();
        dpDateTo = createDatePicker();

        cmbLimit = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbLimit.setPreferredSize(new Dimension(80, 36));
        cmbLimit.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createToolbarButton("Buscar", FontAwesome.SEARCH, primaryColor);
        JButton btnClear = createToolbarButton("Limpiar", FontAwesome.REFRESH, new Color(90, 90, 90));
        JButton btnNew = createToolbarButton("Nuevo", FontAwesome.PLUS, successColor);

        btnSearch.addActionListener(e -> {
            currentPage = 1;
            loadTable();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbProduct.setSelectedIndex(0);
            cmbMovementType.setSelectedIndex(0);
            dpDateFrom.setDate(null);
            dpDateTo.setDate(null);
            currentPage = 1;
            loadTable();
        });

        btnNew.addActionListener(e -> openFormDialog());

        cmbLimit.addActionListener(e -> {
            limit = (Integer) cmbLimit.getSelectedItem();
            currentPage = 1;
            loadTable();
        });

        toolbar.add(txtSearch);
        toolbar.add(cmbProduct);
        toolbar.add(cmbMovementType);
        toolbar.add(dpDateFrom);
        toolbar.add(dpDateTo);
        toolbar.add(btnSearch);
        toolbar.add(btnClear);
        toolbar.add(btnNew);
        toolbar.add(new JLabel("Mostrar:"));
        toolbar.add(cmbLimit);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(toolbar, BorderLayout.SOUTH);

        return container;
    }

    private DatePicker createDatePicker() {
        DatePickerSettings settings = new DatePickerSettings(new Locale("es", "PE"));
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        settings.setAllowKeyboardEditing(false);

        DatePicker datePicker = new DatePicker(settings);
        datePicker.setPreferredSize(new Dimension(130, 36));
        return datePicker;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(borderColor));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Fecha", "Producto", "Tipo", "Cantidad", "Referencia", "Acciones"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
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
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(280);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        table.getColumnModel().getColumn(6).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionsEditor(table));

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

    private void loadCombos() {
        try {
            cmbProduct.removeAllItems();
            cmbProduct.addItem(SelectOption.empty("Todos los productos"));

            for (SelectOption option : controller.listProductOptions()) {
                cmbProduct.addItem(option);
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
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

    @Override
    public void refreshSectionData() {
        int currentProductId = selectedId(cmbProduct);
        LocalDate currentDateFrom = dpDateFrom.getDate();
        LocalDate currentDateTo = dpDateTo.getDate();

        loadCombos();
        selectComboItemById(cmbProduct, currentProductId);
        dpDateFrom.setDate(currentDateFrom);
        dpDateTo.setDate(currentDateTo);
        loadTable();
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);

            String search = txtSearch.getText().trim();
            int idProduct = selectedId(cmbProduct);
            String movementType = (String) cmbMovementType.getSelectedItem();
            LocalDate dateFrom = dpDateFrom.getDate();
            LocalDate dateTo = dpDateTo.getDate();

            int totalRecords = controller.count(search, idProduct, movementType, dateFrom, dateTo);
            totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<StockMovement> list = controller.list(
                    search,
                    idProduct,
                    movementType,
                    dateFrom,
                    dateTo,
                    currentPage,
                    limit
            );

            for (StockMovement movement : list) {
                tableModel.addRow(new Object[]{
                    movement.getIdStockMovement(),
                    formatDate(movement.getMovementDate()),
                    movement.getProductName(),
                    movement.getMovementType(),
                    movement.getQuantity(),
                    nullToDash(movement.getReference()),
                    ""
                });
            }

            lblPagination.setText("Página " + currentPage + " de " + totalPages + " | Total: " + totalRecords);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void openFormDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle("Nuevo movimiento de stock");
        dialog.setSize(560, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(7, 0, 7, 0);

        JComboBox<SelectOption> cmbFormProduct = new JComboBox<>();
        cmbFormProduct.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbFormProduct.setPreferredSize(new Dimension(460, 36));

        JComboBox<String> cmbFormType = new JComboBox<>(new String[]{"ENTRADA", "SALIDA", "AJUSTE"});
        cmbFormType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbFormType.setPreferredSize(new Dimension(460, 36));

        JTextField txtQuantity = new JTextField();
        txtQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtQuantity.setPreferredSize(new Dimension(460, 36));

        JTextArea txtDescription = new JTextArea();
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JScrollPane descriptionScroll = new JScrollPane(txtDescription);
        descriptionScroll.setPreferredSize(new Dimension(460, 90));

        try {
            cmbFormProduct.addItem(SelectOption.empty("Seleccione producto"));

            for (SelectOption option : controller.listProductOptions()) {
                cmbFormProduct.addItem(option);
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }

        int row = 0;

        gbc.gridy = row++;
        form.add(createFormLabel("Producto"), gbc);

        gbc.gridy = row++;
        form.add(cmbFormProduct, gbc);

        gbc.gridy = row++;
        form.add(createFormLabel("Tipo de movimiento"), gbc);

        gbc.gridy = row++;
        form.add(cmbFormType, gbc);

        gbc.gridy = row++;
        form.add(createFormLabel("Cantidad / nuevo stock si es AJUSTE"), gbc);

        gbc.gridy = row++;
        form.add(txtQuantity, gbc);

        gbc.gridy = row++;
        form.add(createFormLabel("Descripción"), gbc);

        gbc.gridy = row++;
        form.add(descriptionScroll, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Color.WHITE);

        JButton btnCancel = createToolbarButton("Cancelar", FontAwesome.TIMES, new Color(90, 90, 90));
        JButton btnSave = createToolbarButton("Guardar", FontAwesome.CHECK, successColor);

        buttons.add(btnCancel);
        buttons.add(btnSave);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            try {
                int idProduct = selectedId(cmbFormProduct);
                String type = (String) cmbFormType.getSelectedItem();
                int quantity = parseInteger(txtQuantity.getText());

                controller.create(
                        idProduct,
                        type,
                        quantity,
                        txtDescription.getText()
                );

                dialog.dispose();
                loadCombos();
                loadTable();

                JOptionPane.showMessageDialog(
                        this,
                        "Movimiento registrado correctamente.",
                        "Operación exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openDetail(int idStockMovement) {
        try {
            StockMovement movement = controller.findById(idStockMovement);

            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            String text = ""
                    + "ID: " + movement.getIdStockMovement() + "\n"
                    + "Producto: " + nullToDash(movement.getProductName()) + "\n"
                    + "Categoría: " + nullToDash(movement.getCategoryName()) + "\n"
                    + "Marca: " + nullToDash(movement.getBrandName()) + "\n"
                    + "Tipo: " + nullToDash(movement.getMovementType()) + "\n"
                    + "Cantidad: " + movement.getQuantity() + "\n"
                    + "Referencia: " + nullToDash(movement.getReference()) + "\n"
                    + "Referencia ID: " + (movement.getReferenceId() == null ? "-" : movement.getReferenceId()) + "\n"
                    + "Fecha: " + formatDate(movement.getMovementDate()) + "\n\n"
                    + "Descripción:\n" + nullToDash(movement.getDescription());

            area.setText(text);

            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setTitle("Detalle del movimiento");
            dialog.setSize(560, 450);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.add(new JScrollPane(area), BorderLayout.CENTER);
            dialog.setVisible(true);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JButton createToolbarButton(String text, FontAwesome icon, Color color) {
        JButton button = new JButton(text, createIcon(icon, 14, Color.WHITE));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(105, text.length() * 9 + 45), 36));
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

    private int selectedId(JComboBox<SelectOption> comboBox) {
        SelectOption option = (SelectOption) comboBox.getSelectedItem();

        if (option == null) {
            return 0;
        }

        return option.getId();
    }

    private int parseInteger(String value) throws Exception {
        try {
            if (value == null || value.trim().isEmpty()) {
                throw new Exception();
            }

            return Integer.parseInt(value.trim());

        } catch (Exception e) {
            throw new Exception("Ingrese una cantidad válida.");
        }
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

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "-";
        }

        return date.format(dateFormatter);
    }

    private String nullToDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value.trim();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private class ActionsRenderer extends JPanel implements TableCellRenderer {

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 7));
            setOpaque(true);
            add(createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle"));
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

            btnView.addActionListener(e -> {
                fireEditingStopped();
                openDetail(selectedId);
            });

            panel.add(btnView);
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
            JFrame frame = new JFrame("Movimientos Stock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1250, 760);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new StockMovementJPanel());
            frame.setVisible(true);
        });
    }
}
