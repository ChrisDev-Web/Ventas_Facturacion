package Paneles;

import Controllers.ProductController;
import Models.Product;
import Models.SelectOption;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class ProductJPanel extends JPanel {

    private final ProductController controller;

    private JTextField txtSearch;
    private JComboBox<Integer> cmbLimit;
    private JLabel lblPagination;
    private JTable table;
    private DefaultTableModel tableModel;

    private int currentPage = 1;
    private int limit = 10;
    private int totalPages = 1;
    private String currentSearch = "";

    private JDialog inactiveDialog;
    private JTextField txtInactiveSearch;
    private JComboBox<Integer> cmbInactiveLimit;
    private JLabel lblInactivePagination;
    private JTable inactiveTable;
    private DefaultTableModel inactiveTableModel;

    private int inactivePage = 1;
    private int inactiveLimit = 10;
    private int inactiveTotalPages = 1;
    private String inactiveSearch = "";

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

    public ProductJPanel() {
        this.controller = new ProductController();
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

        JLabel lblTitle = new JLabel("Productos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Administra productos, stock, precios, categorías y marcas.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(backgroundColor);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(280, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createToolbarButton("Buscar", FontAwesome.SEARCH, primaryColor);
        JButton btnClear = createToolbarButton("Limpiar", FontAwesome.REFRESH, new Color(90, 90, 90));
        JButton btnNew = createToolbarButton("Nuevo", FontAwesome.PLUS, successColor);
        JButton btnInactive = createToolbarButton("Ver inactivos", FontAwesome.EYE, warningColor);

        JLabel lblShow = new JLabel("Mostrar:");
        lblShow.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbLimit = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbLimit.setPreferredSize(new Dimension(80, 36));
        cmbLimit.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnSearch.addActionListener(e -> {
            currentSearch = txtSearch.getText().trim();
            currentPage = 1;
            loadTable();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            currentSearch = "";
            currentPage = 1;
            loadTable();
        });

        btnNew.addActionListener(e -> openFormDialog(null, false));

        btnInactive.addActionListener(e -> openInactiveDialog());

        cmbLimit.addActionListener(e -> {
            limit = (Integer) cmbLimit.getSelectedItem();
            currentPage = 1;
            loadTable();
        });

        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnClear);
        toolbar.add(btnNew);
        toolbar.add(btnInactive);
        toolbar.add(lblShow);
        toolbar.add(cmbLimit);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(toolbar, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(borderColor));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Precio", "Stock", "Acciones"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
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
        table.getColumnModel().getColumn(1).setPreferredWidth(320);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(170);

        table.getColumnModel().getColumn(4).setCellRenderer(new ActiveActionsRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActiveActionsEditor(table));

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
            tableModel.setRowCount(0);

            int totalRecords = controller.countActive(currentSearch);
            totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<Product> list = controller.listActive(currentSearch, currentPage, limit);

            for (Product product : list) {
                tableModel.addRow(new Object[]{
                    product.getIdProduct(),
                    product.getName(),
                    "S/ " + formatMoney(product.getPrice()),
                    product.getStock(),
                    ""
                });
            }

            lblPagination.setText("Página " + currentPage + " de " + totalPages + " | Total: " + totalRecords);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void openFormDialog(Product product, boolean readOnly) {
        boolean isEdit = product != null && !readOnly;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle(readOnly ? "Detalle del Producto" : isEdit ? "Editar Producto" : "Nuevo Producto");
        dialog.setSize(readOnly ? 780 : 800, readOnly ? 760 : 710);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(7, 8, 7, 8);

        int row = 0;

        JTextField txtId = createFormTextField();
        JTextField txtName = createFormTextField();

        JComboBox<SelectOption> cmbCategory = createComboBox();
        JComboBox<SelectOption> cmbBrand = createComboBox();

        JTextField txtCost = createFormTextField();
        JTextField txtProfitMargin = createFormTextField();
        JTextField txtPrice = createFormTextField();
        JTextField txtStock = createFormTextField();

        JTextArea txtDescription = new JTextArea();
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JScrollPane descriptionScroll = new JScrollPane(txtDescription);
        descriptionScroll.setPreferredSize(new Dimension(300, 80));

        JLabel lblImageStatus = new JLabel("Sin imagen");
        lblImageStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblImageStatus.setForeground(new Color(90, 90, 90));

        final String[] selectedImageBase64 = new String[]{null};

        JButton btnSelectImage = createToolbarButton("Seleccionar imagen", FontAwesome.PICTURE_O, primaryColor);
        JButton btnRemoveImage = createToolbarButton("Quitar imagen", FontAwesome.TRASH, dangerColor);

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(btnSelectImage);
        imagePanel.add(btnRemoveImage);
        imagePanel.add(lblImageStatus);

        JTextField txtStatus = createFormTextField();
        JTextField txtCreatedAt = createFormTextField();
        JTextField txtUpdatedAt = createFormTextField();

        loadCombo(cmbCategory, controllerListCategories(), "Seleccione");
        loadCombo(cmbBrand, controllerListBrands(), "Seleccione");

        if (product != null) {
            txtId.setText(String.valueOf(product.getIdProduct()));
            txtName.setText(nullToEmpty(product.getName()));
            txtCost.setText(formatMoney(product.getCost()));
            txtProfitMargin.setText(formatMoney(product.getProfitMargin()));
            txtPrice.setText(formatMoney(product.getPrice()));
            txtStock.setText(String.valueOf(product.getStock()));
            txtDescription.setText(nullToEmpty(product.getDescription()));

            selectedImageBase64[0] = product.getImage();

            if (product.getImage() != null && !product.getImage().trim().isEmpty()) {
                lblImageStatus.setText("Imagen cargada");
            }

            selectComboItem(cmbCategory, product.getIdCategory());
            selectComboItem(cmbBrand, product.getIdBrand());

            txtStatus.setText(getStatusText(product.getStatus()));
            txtCreatedAt.setText(formatDate(product.getCreatedAt()));
            txtUpdatedAt.setText(formatDate(product.getUpdatedAt()));
        } else {
            txtCost.setText("0.00");
            txtProfitMargin.setText("0.00");
            txtPrice.setText("0.00");
            txtStock.setText("0");
        }

        txtPrice.setEditable(false);

        txtCost.addCaretListener(e -> updateCalculatedPrice(txtCost, txtProfitMargin, txtPrice));
        txtProfitMargin.addCaretListener(e -> updateCalculatedPrice(txtCost, txtProfitMargin, txtPrice));

        btnSelectImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "webp"));

            int option = fileChooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    byte[] bytes = Files.readAllBytes(fileChooser.getSelectedFile().toPath());
                    selectedImageBase64[0] = Base64.getEncoder().encodeToString(bytes);
                    lblImageStatus.setText(fileChooser.getSelectedFile().getName());
                } catch (Exception ex) {
                    showError("No se pudo cargar la imagen.");
                }
            }
        });

        btnRemoveImage.addActionListener(e -> {
            selectedImageBase64[0] = null;
            lblImageStatus.setText("Sin imagen");
        });

        boolean editable = !readOnly;

        txtId.setEditable(false);
        txtName.setEditable(editable);
        txtCost.setEditable(editable);
        txtProfitMargin.setEditable(editable);
        txtStock.setEditable(editable);
        txtDescription.setEditable(editable);

        cmbCategory.setEnabled(editable);
        cmbBrand.setEnabled(editable);
        btnSelectImage.setEnabled(editable);
        btnRemoveImage.setEnabled(editable);

        txtStatus.setEditable(false);
        txtCreatedAt.setEditable(false);
        txtUpdatedAt.setEditable(false);

        if (readOnly && product != null) {
            row = addField(form, gbc, row, "ID", txtId, "Estado", txtStatus);
        }

        row = addField(form, gbc, row, "Nombre", txtName, "Categoría", cmbCategory);
        row = addField(form, gbc, row, "Marca", cmbBrand, "Stock", txtStock);
        row = addField(form, gbc, row, "Costo", txtCost, "Margen de ganancia (%)", txtProfitMargin);
        row = addField(form, gbc, row, "Precio calculado", txtPrice, "Imagen", imagePanel);
        row = addField(form, gbc, row, "Descripción", descriptionScroll, "", new JLabel(""));

        if (readOnly && product != null) {
            row = addField(form, gbc, row, "Fecha de creación", txtCreatedAt, "Fecha de actualización", txtUpdatedAt);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Color.WHITE);

        JButton btnCancel = createToolbarButton(readOnly ? "Cerrar" : "Cancelar", FontAwesome.TIMES, new Color(90, 90, 90));
        buttons.add(btnCancel);

        if (!readOnly) {
            JButton btnSave = createToolbarButton("Guardar", FontAwesome.CHECK, successColor);
            buttons.add(btnSave);

            btnSave.addActionListener(e -> {
                try {
                    Product formProduct = new Product();

                    if (isEdit) {
                        formProduct.setIdProduct(product.getIdProduct());
                    }

                    formProduct.setName(txtName.getText());
                    formProduct.setDescription(txtDescription.getText());
                    formProduct.setCost(parseMoney(txtCost.getText()));
                    formProduct.setProfitMargin(parseMoney(txtProfitMargin.getText()));
                    formProduct.setStock(parseInteger(txtStock.getText()));
                    formProduct.setImage(selectedImageBase64[0]);
                    formProduct.setIdCategory(getSelectedId(cmbCategory));
                    formProduct.setIdBrand(getSelectedId(cmbBrand));

                    if (isEdit) {
                        controller.update(formProduct);
                    } else {
                        controller.create(formProduct);
                    }

                    dialog.dispose();
                    loadTable();

                    JOptionPane.showMessageDialog(
                            this,
                            isEdit ? "Producto actualizado correctamente." : "Producto registrado correctamente.",
                            "Operación exitosa",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            });
        }

        btnCancel.addActionListener(e -> dialog.dispose());

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private int addField(
            JPanel form,
            GridBagConstraints gbc,
            int row,
            String labelLeft,
            java.awt.Component fieldLeft,
            String labelRight,
            java.awt.Component fieldRight
    ) {
        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(createFormLabel(labelLeft), gbc);

        gbc.gridx = 1;
        form.add(createFormLabel(labelRight), gbc);

        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(fieldLeft, gbc);

        gbc.gridx = 1;
        form.add(fieldRight, gbc);

        return row + 1;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JTextField createFormTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(300, 36));
        return textField;
    }

    private JComboBox<SelectOption> createComboBox() {
        JComboBox<SelectOption> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(300, 36));
        return comboBox;
    }

    private void loadCombo(JComboBox<SelectOption> comboBox, List<SelectOption> options, String defaultText) {
        comboBox.removeAllItems();
        comboBox.addItem(SelectOption.empty(defaultText));

        for (SelectOption option : options) {
            comboBox.addItem(option);
        }
    }

    private void selectComboItem(JComboBox<SelectOption> comboBox, int id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            SelectOption option = comboBox.getItemAt(i);

            if (option.getId() == id) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }

        comboBox.setSelectedIndex(0);
    }

    private int getSelectedId(JComboBox<SelectOption> comboBox) {
        SelectOption option = (SelectOption) comboBox.getSelectedItem();

        if (option == null) {
            return 0;
        }

        return option.getId();
    }

    private List<SelectOption> controllerListCategories() {
        try {
            return controller.listCategoryOptions();
        } catch (Exception e) {
            showError(e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private List<SelectOption> controllerListBrands() {
        try {
            return controller.listBrandOptions();
        } catch (Exception e) {
            showError(e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private void updateCalculatedPrice(JTextField txtCost, JTextField txtProfitMargin, JTextField txtPrice) {
        try {
            BigDecimal cost = parseMoney(txtCost.getText());
            BigDecimal margin = parseMoney(txtProfitMargin.getText());

            if (margin.compareTo(BigDecimal.valueOf(100)) >= 0) {
                txtPrice.setText("0.00");
                return;
            }

            BigDecimal marginDecimal = margin.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            BigDecimal divisor = BigDecimal.ONE.subtract(marginDecimal);

            BigDecimal price = cost.divide(divisor, 2, RoundingMode.HALF_UP);

            txtPrice.setText(formatMoney(price));

        } catch (Exception e) {
            txtPrice.setText("0.00");
        }
    }

    private void viewDetail(int idProduct) {
        try {
            Product product = controller.findById(idProduct);
            openFormDialog(product, true);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void editProduct(int idProduct) {
        try {
            Product product = controller.findById(idProduct);
            openFormDialog(product, false);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deleteLogical(int idProduct) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar este producto?\nSe eliminará de forma lógica.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteLogical(idProduct);
            loadTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Producto eliminado correctamente.",
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void openInactiveDialog() {
        inactivePage = 1;
        inactiveLimit = 10;
        inactiveSearch = "";

        inactiveDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
        inactiveDialog.setTitle("Productos Inactivos");
        inactiveDialog.setSize(980, 600);
        inactiveDialog.setLocationRelativeTo(this);
        inactiveDialog.setLayout(new BorderLayout(0, 15));
        inactiveDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Productos Inactivos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(textColor);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(backgroundColor);

        txtInactiveSearch = new JTextField();
        txtInactiveSearch.setPreferredSize(new Dimension(260, 36));
        txtInactiveSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createToolbarButton("Buscar", FontAwesome.SEARCH, primaryColor);
        JButton btnClear = createToolbarButton("Limpiar", FontAwesome.REFRESH, new Color(90, 90, 90));

        JLabel lblShow = new JLabel("Mostrar:");
        lblShow.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbInactiveLimit = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbInactiveLimit.setPreferredSize(new Dimension(80, 36));

        btnSearch.addActionListener(e -> {
            inactiveSearch = txtInactiveSearch.getText().trim();
            inactivePage = 1;
            loadInactiveTable();
        });

        btnClear.addActionListener(e -> {
            txtInactiveSearch.setText("");
            inactiveSearch = "";
            inactivePage = 1;
            loadInactiveTable();
        });

        cmbInactiveLimit.addActionListener(e -> {
            inactiveLimit = (Integer) cmbInactiveLimit.getSelectedItem();
            inactivePage = 1;
            loadInactiveTable();
        });

        toolbar.add(txtInactiveSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnClear);
        toolbar.add(lblShow);
        toolbar.add(cmbInactiveLimit);

        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(toolbar, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(createInactiveTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createInactivePaginationPanel(), BorderLayout.SOUTH);

        inactiveDialog.add(mainPanel, BorderLayout.CENTER);

        loadInactiveTable();

        inactiveDialog.setVisible(true);
    }

    private JPanel createInactiveTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(borderColor));

        inactiveTableModel = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Precio", "Eliminado", "Acciones"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        inactiveTable = new JTable(inactiveTableModel);
        inactiveTable.setRowHeight(46);
        inactiveTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inactiveTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        inactiveTable.getTableHeader().setPreferredSize(new Dimension(0, 38));
        inactiveTable.setSelectionBackground(new Color(255, 243, 224));
        inactiveTable.setSelectionForeground(textColor);

        inactiveTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        inactiveTable.getColumnModel().getColumn(1).setPreferredWidth(280);
        inactiveTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        inactiveTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        inactiveTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        inactiveTable.getColumnModel().getColumn(4).setCellRenderer(new InactiveActionsRenderer());
        inactiveTable.getColumnModel().getColumn(4).setCellEditor(new InactiveActionsEditor(inactiveTable));

        JScrollPane scrollPane = new JScrollPane(inactiveTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInactivePaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(backgroundColor);

        JButton btnPrevious = createToolbarButton("Anterior", FontAwesome.CHEVRON_LEFT, new Color(90, 90, 90));
        JButton btnNext = createToolbarButton("Siguiente", FontAwesome.CHEVRON_RIGHT, new Color(90, 90, 90));

        lblInactivePagination = new JLabel();
        lblInactivePagination.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnPrevious.addActionListener(e -> {
            if (inactivePage > 1) {
                inactivePage--;
                loadInactiveTable();
            }
        });

        btnNext.addActionListener(e -> {
            if (inactivePage < inactiveTotalPages) {
                inactivePage++;
                loadInactiveTable();
            }
        });

        panel.add(btnPrevious);
        panel.add(lblInactivePagination);
        panel.add(btnNext);

        return panel;
    }

    private void loadInactiveTable() {
        try {
            inactiveTableModel.setRowCount(0);

            int totalRecords = controller.countInactive(inactiveSearch);
            inactiveTotalPages = Math.max(1, (int) Math.ceil((double) totalRecords / inactiveLimit));

            if (inactivePage > inactiveTotalPages) {
                inactivePage = inactiveTotalPages;
            }

            List<Product> list = controller.listInactive(inactiveSearch, inactivePage, inactiveLimit);

            for (Product product : list) {
                inactiveTableModel.addRow(new Object[]{
                    product.getIdProduct(),
                    product.getName(),
                    "S/ " + formatMoney(product.getPrice()),
                    product.getDeletedAt() == null ? "-" : formatDate(product.getDeletedAt()),
                    ""
                });
            }

            lblInactivePagination.setText("Página " + inactivePage + " de " + inactiveTotalPages + " | Total: " + totalRecords);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void restoreProduct(int idProduct) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas restaurar este producto?",
                "Confirmar restauración",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.restore(idProduct);
            loadInactiveTable();
            loadTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Producto restaurado correctamente.",
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deletePhysical(int idProduct) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar físicamente este producto?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación física",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deletePhysical(idProduct);
            loadInactiveTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Producto eliminado físicamente.",
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

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
        button.setPreferredSize(new Dimension(Math.max(105, text.length() * 10 + 45), 36));
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

    private BigDecimal parseMoney(String value) throws Exception {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }

            return new BigDecimal(value.trim()).setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            throw new Exception("Ingrese valores numéricos válidos en costo y margen.");
        }
    }

    private int parseInteger(String value) throws Exception {
        try {
            if (value == null || value.trim().isEmpty()) {
                return 0;
            }

            return Integer.parseInt(value.trim());

        } catch (Exception e) {
            throw new Exception("Ingrese un stock válido.");
        }
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }

        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String getStatusText(int status) {
        return status == 1 ? "Activo" : "Inactivo";
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "-";
        }

        return date.format(dateFormatter);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private class ActiveActionsRenderer extends JPanel implements TableCellRenderer {

        public ActiveActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 7));
            setOpaque(true);

            add(createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle"));
            add(createActionButton(FontAwesome.PENCIL, warningColor, "Editar"));
            add(createActionButton(FontAwesome.TRASH, dangerColor, "Eliminar"));
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private class ActiveActionsEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private int selectedId;

        public ActiveActionsEditor(JTable table) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 7));
            panel.setBackground(Color.WHITE);

            JButton btnView = createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle");
            JButton btnEdit = createActionButton(FontAwesome.PENCIL, warningColor, "Editar");
            JButton btnDelete = createActionButton(FontAwesome.TRASH, dangerColor, "Eliminar");

            btnView.addActionListener(e -> {
                fireEditingStopped();
                viewDetail(selectedId);
            });

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                editProduct(selectedId);
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                deleteLogical(selectedId);
            });

            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column) {
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

    private class InactiveActionsRenderer extends JPanel implements TableCellRenderer {

        public InactiveActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 7));
            setOpaque(true);

            add(createActionButton(FontAwesome.REFRESH, successColor, "Restaurar"));
            add(createActionButton(FontAwesome.TRASH, dangerColor, "Eliminar físico"));
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private class InactiveActionsEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private int selectedId;

        public InactiveActionsEditor(JTable table) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 7));
            panel.setBackground(Color.WHITE);

            JButton btnRestore = createActionButton(FontAwesome.REFRESH, successColor, "Restaurar");
            JButton btnDelete = createActionButton(FontAwesome.TRASH, dangerColor, "Eliminar físico");

            btnRestore.addActionListener(e -> {
                fireEditingStopped();
                restoreProduct(selectedId);
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                deletePhysical(selectedId);
            });

            panel.add(btnRestore);
            panel.add(btnDelete);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column) {
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
            JFrame frame = new JFrame("Productos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 760);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ProductJPanel());
            frame.setVisible(true);
        });
    }
}