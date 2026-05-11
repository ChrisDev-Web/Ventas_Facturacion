package Views;

import Controllers.SupplierController;
import Models.SelectOption;
import Models.Supplier;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

public class SupplierJPanel extends JPanel {

    private final SupplierController controller;

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

    public SupplierJPanel() {
        this.controller = new SupplierController();
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

        JLabel lblTitle = new JLabel("Proveedores");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Administra los proveedores del sistema.");
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
                new Object[]{"ID", "Razón Social", "Documento", "Teléfono", "Acciones"},
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
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
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

            List<Supplier> list = controller.listActive(currentSearch, currentPage, limit);

            for (Supplier supplier : list) {
                tableModel.addRow(new Object[]{
                    supplier.getIdSupplier(),
                    supplier.getBusinessName(),
                    buildDocument(supplier),
                    nullToDash(supplier.getPhone()),
                    ""
                });
            }

            lblPagination.setText("Página " + currentPage + " de " + totalPages + " | Total: " + totalRecords);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void openFormDialog(Supplier supplier, boolean readOnly) {
        boolean isEdit = supplier != null && !readOnly;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle(readOnly ? "Detalle del Proveedor" : isEdit ? "Editar Proveedor" : "Nuevo Proveedor");
        dialog.setSize(readOnly ? 780 : 780, readOnly ? 720 : 680);
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
        JTextField txtBusinessName = createFormTextField();
        JTextField txtTradeName = createFormTextField();

        JComboBox<SelectOption> cmbDocumentType = createComboBox();
        JTextField txtDocumentNumber = createFormTextField();
        JButton btnSearchRuc = createIconButton(FontAwesome.SEARCH, primaryColor, "Buscar por RUC");
        JPanel documentNumberPanel = createDocumentSearchPanel(txtDocumentNumber, btnSearchRuc);

        JTextField txtPhone = createFormTextField();
        JTextField txtEmail = createFormTextField();

        JTextArea txtAddress = new JTextArea();
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);

        JScrollPane addressScroll = new JScrollPane(txtAddress);
        addressScroll.setPreferredSize(new Dimension(300, 75));

        JComboBox<SelectOption> cmbRegion = createComboBox();
        JComboBox<SelectOption> cmbProvince = createComboBox();
        JComboBox<SelectOption> cmbDistrict = createComboBox();

        JTextField txtStatus = createFormTextField();
        JTextField txtCreatedAt = createFormTextField();
        JTextField txtUpdatedAt = createFormTextField();

        loadCombo(cmbDocumentType, controllerListDocumentTypes(), "Seleccione");
        loadCombo(cmbRegion, controllerListRegions(), "Seleccione");

        btnSearchRuc.addActionListener(e -> searchSupplierByRuc(
                cmbDocumentType,
                txtDocumentNumber,
                txtBusinessName,
                txtAddress
        ));

        if (supplier != null) {
            txtId.setText(String.valueOf(supplier.getIdSupplier()));
            txtBusinessName.setText(nullToEmpty(supplier.getBusinessName()));
            txtTradeName.setText(nullToEmpty(supplier.getTradeName()));

            txtDocumentNumber.setText(nullToEmpty(supplier.getDocumentNumber()));
            txtPhone.setText(nullToEmpty(supplier.getPhone()));
            txtEmail.setText(nullToEmpty(supplier.getEmail()));
            txtAddress.setText(nullToEmpty(supplier.getAddress()));

            selectComboItem(cmbDocumentType, supplier.getIdDocumentType());
            selectComboItem(cmbRegion, supplier.getIdRegion());

            if (supplier.getIdRegion() != null) {
                loadCombo(cmbProvince, controllerListProvinces(supplier.getIdRegion()), "Seleccione");
                selectComboItem(cmbProvince, supplier.getIdProvince());
            } else {
                loadCombo(cmbProvince, java.util.Collections.emptyList(), "Seleccione");
            }

            if (supplier.getIdProvince() != null) {
                loadCombo(cmbDistrict, controllerListDistricts(supplier.getIdProvince()), "Seleccione");
                selectComboItem(cmbDistrict, supplier.getIdDistrict());
            } else {
                loadCombo(cmbDistrict, java.util.Collections.emptyList(), "Seleccione");
            }

            txtStatus.setText(getStatusText(supplier.getStatus()));
            txtCreatedAt.setText(formatDate(supplier.getCreatedAt()));
            txtUpdatedAt.setText(formatDate(supplier.getUpdatedAt()));
        } else {
            loadCombo(cmbProvince, java.util.Collections.emptyList(), "Seleccione");
            loadCombo(cmbDistrict, java.util.Collections.emptyList(), "Seleccione");
        }

        cmbRegion.addActionListener(e -> {
            if (readOnly) {
                return;
            }

            int idRegion = getSelectedId(cmbRegion);
            loadCombo(cmbProvince, controllerListProvinces(idRegion), "Seleccione");
            loadCombo(cmbDistrict, java.util.Collections.emptyList(), "Seleccione");
        });

        cmbProvince.addActionListener(e -> {
            if (readOnly) {
                return;
            }

            int idProvince = getSelectedId(cmbProvince);
            loadCombo(cmbDistrict, controllerListDistricts(idProvince), "Seleccione");
        });

        boolean editable = !readOnly;

        txtId.setEditable(false);
        txtBusinessName.setEditable(editable);
        txtTradeName.setEditable(editable);
        txtDocumentNumber.setEditable(editable);
        txtPhone.setEditable(editable);
        txtEmail.setEditable(editable);
        txtAddress.setEditable(editable);

        btnSearchRuc.setEnabled(editable);
        cmbDocumentType.setEnabled(editable);
        cmbRegion.setEnabled(editable);
        cmbProvince.setEnabled(editable);
        cmbDistrict.setEnabled(editable);

        txtStatus.setEditable(false);
        txtCreatedAt.setEditable(false);
        txtUpdatedAt.setEditable(false);

        if (readOnly && supplier != null) {
            row = addField(form, gbc, row, "ID", txtId, "Estado", txtStatus);
        }

        row = addField(form, gbc, row, "Razón social", txtBusinessName, "Nombre comercial", txtTradeName);
        row = addField(
                form,
                gbc,
                row,
                "Tipo de documento",
                cmbDocumentType,
                "Nro. documento",
                readOnly ? txtDocumentNumber : documentNumberPanel
        );
        row = addField(form, gbc, row, "Teléfono", txtPhone, "Correo", txtEmail);
        row = addField(form, gbc, row, "Región", cmbRegion, "Provincia", cmbProvince);
        row = addField(form, gbc, row, "Distrito", cmbDistrict, "Dirección", addressScroll);

        if (readOnly && supplier != null) {
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
                    Supplier formSupplier = new Supplier();

                    if (isEdit) {
                        formSupplier.setIdSupplier(supplier.getIdSupplier());
                    }

                    formSupplier.setBusinessName(txtBusinessName.getText());
                    formSupplier.setTradeName(txtTradeName.getText());
                    formSupplier.setIdDocumentType(getSelectedId(cmbDocumentType));
                    formSupplier.setDocumentNumber(txtDocumentNumber.getText());
                    formSupplier.setPhone(txtPhone.getText());
                    formSupplier.setEmail(txtEmail.getText());
                    formSupplier.setAddress(txtAddress.getText());

                    formSupplier.setIdRegion(getSelectedNullableId(cmbRegion));
                    formSupplier.setIdProvince(getSelectedNullableId(cmbProvince));
                    formSupplier.setIdDistrict(getSelectedNullableId(cmbDistrict));

                    if (isEdit) {
                        controller.update(formSupplier);
                    } else {
                        controller.create(formSupplier);
                    }

                    dialog.dispose();
                    loadTable();

                    JOptionPane.showMessageDialog(
                            this,
                            isEdit ? "Proveedor actualizado correctamente." : "Proveedor registrado correctamente.",
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

    private JButton createIconButton(FontAwesome icon, Color color, String tooltip) {
        JButton button = new JButton(createIcon(icon, 14, Color.WHITE));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(42, 36));
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    private JPanel createDocumentSearchPanel(JTextField textField, JButton button) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 36));
        panel.add(textField, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        return panel;
    }

    private void loadCombo(JComboBox<SelectOption> comboBox, List<SelectOption> options, String defaultText) {
        comboBox.removeAllItems();
        comboBox.addItem(SelectOption.empty(defaultText));

        for (SelectOption option : options) {
            comboBox.addItem(option);
        }
    }

    private void selectComboItem(JComboBox<SelectOption> comboBox, Integer id) {
        if (id == null) {
            comboBox.setSelectedIndex(0);
            return;
        }

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

    private Integer getSelectedNullableId(JComboBox<SelectOption> comboBox) {
        int id = getSelectedId(comboBox);

        if (id <= 0) {
            return null;
        }

        return id;
    }

    private void searchSupplierByRuc(
            JComboBox<SelectOption> cmbDocumentType,
            JTextField txtDocumentNumber,
            JTextField txtBusinessName,
            JTextArea txtAddress
    ) {
        try {
            if (!isRucSelected(cmbDocumentType)) {
                throw new Exception("Seleccione el tipo de documento RUC para realizar la consulta.");
            }

            Supplier foundSupplier = controller.searchByRuc(txtDocumentNumber.getText());

            txtBusinessName.setText(nullToEmpty(foundSupplier.getBusinessName()));
            txtDocumentNumber.setText(nullToEmpty(foundSupplier.getDocumentNumber()));
            txtAddress.setText(nullToEmpty(foundSupplier.getAddress()));

            JOptionPane.showMessageDialog(
                    this,
                    "Datos encontrados correctamente.",
                    "Consulta RUC",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private boolean isRucSelected(JComboBox<SelectOption> comboBox) {
        SelectOption option = (SelectOption) comboBox.getSelectedItem();

        if (option == null || option.getName() == null) {
            return false;
        }

        return option.getName().trim().toUpperCase().contains("RUC");
    }

    private List<SelectOption> controllerListDocumentTypes() {
        try {
            return controller.listDocumentTypeOptions();
        } catch (Exception e) {
            showError(e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private List<SelectOption> controllerListRegions() {
        try {
            return controller.listRegionOptions();
        } catch (Exception e) {
            showError(e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private List<SelectOption> controllerListProvinces(int idRegion) {
        try {
            return controller.listProvinceOptions(idRegion);
        } catch (Exception e) {
            showError(e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private List<SelectOption> controllerListDistricts(int idProvince) {
        try {
            return controller.listDistrictOptions(idProvince);
        } catch (Exception e) {
            showError(e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private void viewDetail(int idSupplier) {
        try {
            Supplier supplier = controller.findById(idSupplier);
            openFormDialog(supplier, true);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void editSupplier(int idSupplier) {
        try {
            Supplier supplier = controller.findById(idSupplier);
            openFormDialog(supplier, false);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deleteLogical(int idSupplier) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar este proveedor?\nSe eliminará de forma lógica.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteLogical(idSupplier);
            loadTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Proveedor eliminado correctamente.",
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
        inactiveDialog.setTitle("Proveedores Inactivos");
        inactiveDialog.setSize(980, 600);
        inactiveDialog.setLocationRelativeTo(this);
        inactiveDialog.setLayout(new BorderLayout(0, 15));
        inactiveDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Proveedores Inactivos");
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
                new Object[]{"ID", "Razón Social", "Documento", "Eliminado", "Acciones"},
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
        inactiveTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        inactiveTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        inactiveTable.getColumnModel().getColumn(3).setPreferredWidth(150);
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

            List<Supplier> list = controller.listInactive(inactiveSearch, inactivePage, inactiveLimit);

            for (Supplier supplier : list) {
                inactiveTableModel.addRow(new Object[]{
                    supplier.getIdSupplier(),
                    supplier.getBusinessName(),
                    buildDocument(supplier),
                    supplier.getDeletedAt() == null ? "-" : formatDate(supplier.getDeletedAt()),
                    ""
                });
            }

            lblInactivePagination.setText("Página " + inactivePage + " de " + inactiveTotalPages + " | Total: " + totalRecords);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void restoreSupplier(int idSupplier) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas restaurar este proveedor?",
                "Confirmar restauración",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.restore(idSupplier);
            loadInactiveTable();
            loadTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Proveedor restaurado correctamente.",
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deletePhysical(int idSupplier) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar físicamente este proveedor?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación física",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deletePhysical(idSupplier);
            loadInactiveTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Proveedor eliminado físicamente.",
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

    private String buildDocument(Supplier supplier) {
        String type = supplier.getDocumentTypeName() == null ? "" : supplier.getDocumentTypeName();
        String number = supplier.getDocumentNumber() == null ? "" : supplier.getDocumentNumber();

        String value = (type + " " + number).trim();

        return value.isEmpty() ? "-" : value;
    }

    private String nullToDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value.trim();
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
                editSupplier(selectedId);
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
                restoreSupplier(selectedId);
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
            JFrame frame = new JFrame("Proveedores");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 760);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new SupplierJPanel());
            frame.setVisible(true);
        });
    }
}
