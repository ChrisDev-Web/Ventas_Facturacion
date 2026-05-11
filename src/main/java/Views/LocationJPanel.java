package Views;

import Controllers.LocationController;
import Models.LocationItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

public class LocationJPanel extends JPanel {

    private final LocationController controller;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color cardColor = Color.WHITE;
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

    public LocationJPanel() {
        this.controller = new LocationController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Ubicación");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Administra países, regiones, provincias y distritos en una sola vista.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 18, 18));
        gridPanel.setBackground(backgroundColor);

        gridPanel.add(new CrudBoxPanel("COUNTRY", "Countries", "País"));
        gridPanel.add(new CrudBoxPanel("REGION", "Regions", "Región"));
        gridPanel.add(new CrudBoxPanel("PROVINCE", "Provinces", "Provincia"));
        gridPanel.add(new CrudBoxPanel("DISTRICT", "Districts", "Distrito"));

        add(header, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
    }

    private JButton createToolbarButton(String text, FontAwesome icon, Color color) {
        JButton button = new JButton(text, createIcon(icon, 13, Color.WHITE));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(88, text.length() * 9 + 38), 32));
        button.setOpaque(true);
        return button;
    }

    private JButton createSmallActionButton(FontAwesome icon, Color color, String tooltip) {
        JButton button = new JButton(createIcon(icon, 14, Color.WHITE));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(30, 27));
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

    private String shortText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        String text = value.trim();

        if (text.length() > 32) {
            return text.substring(0, 32) + "...";
        }

        return text;
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "-";
        }

        return date.format(dateFormatter);
    }

    private String getStatusText(int status) {
        return status == 1 ? "Activo" : "Inactivo";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private class CrudBoxPanel extends JPanel {

        private final String entity;
        private final String title;
        private final String singularName;

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

        public CrudBoxPanel(String entity, String title, String singularName) {
            this.entity = entity;
            this.title = title;
            this.singularName = singularName;

            initBox();
            loadTable();
        }

        private void initBox() {
            setLayout(new BorderLayout(0, 10));
            setBackground(cardColor);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
            ));

            add(createBoxHeader(), BorderLayout.NORTH);
            add(createTablePanel(), BorderLayout.CENTER);
            add(createPaginationPanel(), BorderLayout.SOUTH);
        }

        private JPanel createBoxHeader() {
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.setBackground(cardColor);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblTitle.setForeground(textColor);

            JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
            toolbar.setBackground(cardColor);

            txtSearch = new JTextField();
            txtSearch.setPreferredSize(new Dimension(150, 32));
            txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JButton btnSearch = createToolbarButton("", FontAwesome.SEARCH, primaryColor);
            JButton btnClear = createToolbarButton("", FontAwesome.REFRESH, new Color(90, 90, 90));
            JButton btnNew = createToolbarButton("", FontAwesome.PLUS, successColor);
            JButton btnInactive = createToolbarButton("", FontAwesome.EYE, warningColor);

            btnSearch.setToolTipText("Buscar");
            btnClear.setToolTipText("Limpiar");
            btnNew.setToolTipText("Nuevo");
            btnInactive.setToolTipText("Ver inactivos");

            cmbLimit = new JComboBox<>(new Integer[]{10, 20, 50});
            cmbLimit.setPreferredSize(new Dimension(65, 32));
            cmbLimit.setFont(new Font("Segoe UI", Font.PLAIN, 13));

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
            toolbar.add(cmbLimit);

            panel.add(lblTitle, BorderLayout.NORTH);
            panel.add(toolbar, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createTablePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(cardColor);
            panel.setBorder(BorderFactory.createLineBorder(borderColor));

            tableModel = new DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Descripción", "Acciones"},
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3;
                }
            };

            table = new JTable(tableModel);
            table.setRowHeight(42);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getTableHeader().setPreferredSize(new Dimension(0, 34));
            table.setSelectionBackground(new Color(227, 242, 253));
            table.setSelectionForeground(textColor);

            table.getColumnModel().getColumn(0).setPreferredWidth(45);
            table.getColumnModel().getColumn(1).setPreferredWidth(130);
            table.getColumnModel().getColumn(2).setPreferredWidth(190);
            table.getColumnModel().getColumn(3).setPreferredWidth(120);

            table.getColumnModel().getColumn(3).setCellRenderer(new ActiveActionsRenderer());
            table.getColumnModel().getColumn(3).setCellEditor(new ActiveActionsEditor());

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createPaginationPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
            panel.setBackground(cardColor);

            JButton btnPrevious = createToolbarButton("", FontAwesome.CHEVRON_LEFT, new Color(90, 90, 90));
            JButton btnNext = createToolbarButton("", FontAwesome.CHEVRON_RIGHT, new Color(90, 90, 90));

            btnPrevious.setToolTipText("Anterior");
            btnNext.setToolTipText("Siguiente");

            lblPagination = new JLabel();
            lblPagination.setFont(new Font("Segoe UI", Font.PLAIN, 12));

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

                int totalRecords = controller.countActive(entity, currentSearch);
                totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

                if (currentPage > totalPages) {
                    currentPage = totalPages;
                }

                List<LocationItem> list = controller.listActive(entity, currentSearch, currentPage, limit);

                for (LocationItem item : list) {
                    tableModel.addRow(new Object[]{
                        item.getId(),
                        item.getName(),
                        shortText(item.getDescription()),
                        ""
                    });
                }

                lblPagination.setText("Pág. " + currentPage + "/" + totalPages + " | " + totalRecords);

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void openFormDialog(LocationItem item, boolean readOnly) {
            boolean isEdit = item != null && !readOnly;

            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(LocationJPanel.this), true);
            dialog.setTitle(readOnly ? "Detalle de " + singularName : isEdit ? "Editar " + singularName : "Nuevo " + singularName);
            dialog.setSize(readOnly ? 560 : 520, readOnly ? 600 : 430);
            dialog.setLocationRelativeTo(LocationJPanel.this);
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

            int row = 0;

            if (readOnly && item != null) {
                JTextField txtId = createFormTextField();
                txtId.setText(String.valueOf(item.getId()));
                txtId.setEditable(false);

                gbc.gridy = row++;
                form.add(createFormLabel("ID"), gbc);

                gbc.gridy = row++;
                form.add(txtId, gbc);
            }

            JComboBox<ComboItem> cmbParent = null;

            if (!"COUNTRY".equals(entity)) {
                if (readOnly) {
                    JTextField txtParent = createFormTextField();
                    txtParent.setText(item == null || item.getParentName() == null ? "-" : item.getParentName());
                    txtParent.setEditable(false);

                    gbc.gridy = row++;
                    form.add(createFormLabel(getParentLabel()), gbc);

                    gbc.gridy = row++;
                    form.add(txtParent, gbc);
                } else {
                    cmbParent = new JComboBox<>();
                    cmbParent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    cmbParent.setPreferredSize(new Dimension(460, 36));

                    try {
                        List<LocationItem> parents = controller.listParentOptions(entity);

                        cmbParent.addItem(new ComboItem(0, "Seleccione"));

                        for (LocationItem parent : parents) {
                            cmbParent.addItem(new ComboItem(parent.getId(), parent.getName()));
                        }

                        if (item != null && item.getParentId() != null) {
                            selectComboItem(cmbParent, item.getParentId());
                        }

                    } catch (Exception e) {
                        showError(e.getMessage());
                    }

                    gbc.gridy = row++;
                    form.add(createFormLabel(getParentLabel()), gbc);

                    gbc.gridy = row++;
                    form.add(cmbParent, gbc);
                }
            }

            JTextField txtName = createFormTextField();

            JTextArea txtDescription = new JTextArea();
            txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txtDescription.setLineWrap(true);
            txtDescription.setWrapStyleWord(true);

            JScrollPane descriptionScroll = new JScrollPane(txtDescription);
            descriptionScroll.setPreferredSize(new Dimension(460, 95));

            if (item != null) {
                txtName.setText(item.getName() == null ? "" : item.getName());
                txtDescription.setText(item.getDescription() == null ? "" : item.getDescription());
            }

            txtName.setEditable(!readOnly);
            txtDescription.setEditable(!readOnly);

            gbc.gridy = row++;
            form.add(createFormLabel("Nombre"), gbc);

            gbc.gridy = row++;
            form.add(txtName, gbc);

            gbc.gridy = row++;
            form.add(createFormLabel("Descripción"), gbc);

            gbc.gridy = row++;
            form.add(descriptionScroll, gbc);

            if (readOnly && item != null) {
                JTextField txtStatus = createFormTextField();
                txtStatus.setText(getStatusText(item.getStatus()));
                txtStatus.setEditable(false);

                JTextField txtCreatedAt = createFormTextField();
                txtCreatedAt.setText(formatDate(item.getCreatedAt()));
                txtCreatedAt.setEditable(false);

                JTextField txtUpdatedAt = createFormTextField();
                txtUpdatedAt.setText(formatDate(item.getUpdatedAt()));
                txtUpdatedAt.setEditable(false);

                gbc.gridy = row++;
                form.add(createFormLabel("Estado"), gbc);

                gbc.gridy = row++;
                form.add(txtStatus, gbc);

                gbc.gridy = row++;
                form.add(createFormLabel("Fecha de creación"), gbc);

                gbc.gridy = row++;
                form.add(txtCreatedAt, gbc);

                gbc.gridy = row++;
                form.add(createFormLabel("Fecha de actualización"), gbc);

                gbc.gridy = row++;
                form.add(txtUpdatedAt, gbc);
            }

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttons.setBackground(Color.WHITE);

            JButton btnCancel = createToolbarButton(readOnly ? "Cerrar" : "Cancelar", FontAwesome.TIMES, new Color(90, 90, 90));
            buttons.add(btnCancel);

            if (!readOnly) {
                JButton btnSave = createToolbarButton("Guardar", FontAwesome.CHECK, successColor);
                buttons.add(btnSave);

                JComboBox<ComboItem> finalCmbParent = cmbParent;

                btnSave.addActionListener(e -> {
                    try {
                        Integer parentId = null;

                        if (!"COUNTRY".equals(entity)) {
                            ComboItem selected = (ComboItem) finalCmbParent.getSelectedItem();

                            if (selected == null || selected.getId() <= 0) {
                                throw new Exception("Seleccione " + getParentLabel().toLowerCase() + ".");
                            }

                            parentId = selected.getId();
                        }

                        if (isEdit) {
                            controller.update(entity, item.getId(), parentId, txtName.getText(), txtDescription.getText());
                        } else {
                            controller.create(entity, parentId, txtName.getText(), txtDescription.getText());
                        }

                        dialog.dispose();
                        loadTable();

                        JOptionPane.showMessageDialog(
                                LocationJPanel.this,
                                isEdit ? singularName + " actualizado correctamente." : singularName + " registrado correctamente.",
                                "Operación exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } catch (Exception ex) {
                        showError(ex.getMessage());
                    }
                });
            }

            btnCancel.addActionListener(e -> dialog.dispose());

            dialog.add(form, BorderLayout.CENTER);
            dialog.add(buttons, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }

        private JLabel createFormLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return label;
        }

        private JTextField createFormTextField() {
            JTextField textField = new JTextField();
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setPreferredSize(new Dimension(460, 36));
            return textField;
        }

        private String getParentLabel() {
            if ("REGION".equals(entity)) {
                return "País";
            }

            if ("PROVINCE".equals(entity)) {
                return "Región";
            }

            if ("DISTRICT".equals(entity)) {
                return "Provincia";
            }

            return "Padre";
        }

        private void selectComboItem(JComboBox<ComboItem> comboBox, int id) {
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                ComboItem item = comboBox.getItemAt(i);

                if (item.getId() == id) {
                    comboBox.setSelectedIndex(i);
                    return;
                }
            }
        }

        private void viewDetail(int id) {
            try {
                LocationItem item = controller.findById(entity, id);
                openFormDialog(item, true);
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void editItem(int id) {
            try {
                LocationItem item = controller.findById(entity, id);
                openFormDialog(item, false);
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void deleteLogical(int id) {
            int option = JOptionPane.showConfirmDialog(
                    LocationJPanel.this,
                    "¿Deseas eliminar este registro?\nSe eliminará de forma lógica.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                controller.deleteLogical(entity, id);
                loadTable();

                JOptionPane.showMessageDialog(
                        LocationJPanel.this,
                        "Registro eliminado correctamente.",
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

            inactiveDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(LocationJPanel.this), true);
            inactiveDialog.setTitle(title + " Inactivos");
            inactiveDialog.setSize(920, 580);
            inactiveDialog.setLocationRelativeTo(LocationJPanel.this);
            inactiveDialog.setLayout(new BorderLayout(0, 15));
            inactiveDialog.setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
            mainPanel.setBackground(backgroundColor);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel topPanel = new JPanel(new BorderLayout(0, 15));
            topPanel.setBackground(backgroundColor);

            JLabel lblTitle = new JLabel(title + " Inactivos");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitle.setForeground(textColor);

            JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            toolbar.setBackground(backgroundColor);

            txtInactiveSearch = new JTextField();
            txtInactiveSearch.setPreferredSize(new Dimension(260, 36));
            txtInactiveSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JButton btnSearch = createToolbarButton("Buscar", FontAwesome.SEARCH, primaryColor);
            JButton btnClear = createToolbarButton("Limpiar", FontAwesome.REFRESH, new Color(90, 90, 90));

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
            toolbar.add(new JLabel("Mostrar:"));
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
                    new Object[]{"ID", "Nombre", "Descripción", "Eliminado", "Acciones"},
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
            inactiveTable.getColumnModel().getColumn(1).setPreferredWidth(180);
            inactiveTable.getColumnModel().getColumn(2).setPreferredWidth(300);
            inactiveTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            inactiveTable.getColumnModel().getColumn(4).setPreferredWidth(150);

            inactiveTable.getColumnModel().getColumn(4).setCellRenderer(new InactiveActionsRenderer());
            inactiveTable.getColumnModel().getColumn(4).setCellEditor(new InactiveActionsEditor());

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

                int totalRecords = controller.countInactive(entity, inactiveSearch);
                inactiveTotalPages = Math.max(1, (int) Math.ceil((double) totalRecords / inactiveLimit));

                if (inactivePage > inactiveTotalPages) {
                    inactivePage = inactiveTotalPages;
                }

                List<LocationItem> list = controller.listInactive(entity, inactiveSearch, inactivePage, inactiveLimit);

                for (LocationItem item : list) {
                    inactiveTableModel.addRow(new Object[]{
                        item.getId(),
                        item.getName(),
                        shortText(item.getDescription()),
                        item.getDeletedAt() == null ? "-" : formatDate(item.getDeletedAt()),
                        ""
                    });
                }

                lblInactivePagination.setText("Página " + inactivePage + " de " + inactiveTotalPages + " | Total: " + totalRecords);

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void restoreItem(int id) {
            int option = JOptionPane.showConfirmDialog(
                    LocationJPanel.this,
                    "¿Deseas restaurar este registro?",
                    "Confirmar restauración",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                controller.restore(entity, id);
                loadInactiveTable();
                loadTable();

                JOptionPane.showMessageDialog(
                        LocationJPanel.this,
                        "Registro restaurado correctamente.",
                        "Operación exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private void deletePhysical(int id) {
            int option = JOptionPane.showConfirmDialog(
                    LocationJPanel.this,
                    "¿Deseas eliminar físicamente este registro?\nEsta acción no se puede deshacer.",
                    "Confirmar eliminación física",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                controller.deletePhysical(entity, id);
                loadInactiveTable();

                JOptionPane.showMessageDialog(
                        LocationJPanel.this,
                        "Registro eliminado físicamente.",
                        "Operación exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        private class ActiveActionsRenderer extends JPanel implements TableCellRenderer {

            public ActiveActionsRenderer() {
                setLayout(new FlowLayout(FlowLayout.CENTER, 4, 7));
                setOpaque(true);

                add(createSmallActionButton(FontAwesome.EYE, primaryColor, "Ver detalle"));
                add(createSmallActionButton(FontAwesome.PENCIL, warningColor, "Editar"));
                add(createSmallActionButton(FontAwesome.TRASH, dangerColor, "Eliminar"));
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

            public ActiveActionsEditor() {
                panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 7));
                panel.setBackground(Color.WHITE);

                JButton btnView = createSmallActionButton(FontAwesome.EYE, primaryColor, "Ver detalle");
                JButton btnEdit = createSmallActionButton(FontAwesome.PENCIL, warningColor, "Editar");
                JButton btnDelete = createSmallActionButton(FontAwesome.TRASH, dangerColor, "Eliminar");

                btnView.addActionListener(e -> {
                    fireEditingStopped();
                    viewDetail(selectedId);
                });

                btnEdit.addActionListener(e -> {
                    fireEditingStopped();
                    editItem(selectedId);
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

                add(createSmallActionButton(FontAwesome.REFRESH, successColor, "Restaurar"));
                add(createSmallActionButton(FontAwesome.TRASH, dangerColor, "Eliminar físico"));
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

            public InactiveActionsEditor() {
                panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 7));
                panel.setBackground(Color.WHITE);

                JButton btnRestore = createSmallActionButton(FontAwesome.REFRESH, successColor, "Restaurar");
                JButton btnDelete = createSmallActionButton(FontAwesome.TRASH, dangerColor, "Eliminar físico");

                btnRestore.addActionListener(e -> {
                    fireEditingStopped();
                    restoreItem(selectedId);
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
    }

    private static class ComboItem {

        private final int id;
        private final String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
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
            JFrame frame = new JFrame("Ubicación");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new LocationJPanel());
            frame.setVisible(true);
        });
    }
}