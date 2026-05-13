package Paneles;

import Controllers.UserController;
import Models.SelectOption;
import Models.User;
import Presentacion.SectionRefreshable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class UserJPanel extends JPanel implements SectionRefreshable {

    private final UserController controller;

    private JTextField txtSearch;
    private JComboBox<Integer> cmbLimit;
    private JLabel lblPagination;
    private JTable table;
    private DefaultTableModel tableModel;

    private int currentPage = 1;
    private int limit = 10;
    private int totalPages = 1;
    private String currentSearch = "";

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

    public UserJPanel() {
        this.controller = new UserController();
        initUI();
        loadTable();
    }

    @Override
    public void refreshSectionData() {
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

        JLabel lblTitle = new JLabel("Usuarios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Administra las cuentas de acceso al sistema.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(backgroundColor);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createToolbarButton("Buscar", FontAwesome.SEARCH, primaryColor);
        JButton btnClear = createToolbarButton("Limpiar", FontAwesome.REFRESH, new Color(90, 90, 90));
        JButton btnNew = createToolbarButton("Nuevo", FontAwesome.PLUS, successColor);

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

        cmbLimit.addActionListener(e -> {
            limit = (Integer) cmbLimit.getSelectedItem();
            currentPage = 1;
            loadTable();
        });

        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnClear);
        toolbar.add(btnNew);
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
                new Object[]{"ID", "Usuario", "Nombre completo", "Correo", "Telefono", "Estado", "Acciones"},
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

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(240);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(95);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

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

    private void loadTable() {
        try {
            tableModel.setRowCount(0);

            int totalRecords = controller.countUsers(currentSearch);
            totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / limit));

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<User> list = controller.listUsers(currentSearch, currentPage, limit);

            for (User user : list) {
                tableModel.addRow(new Object[]{
                    user.getIdUser(),
                    nullToDash(user.getUserName()),
                    nullToDash(user.getFullName()),
                    nullToDash(user.getEmail()),
                    nullToDash(user.getPhone()),
                    getStatusText(user.getStatus()),
                    ""
                });
            }

            lblPagination.setText("Pagina " + currentPage + " de " + totalPages + " | Total: " + totalRecords);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void openFormDialog(User user, boolean readOnly) {
        boolean isEdit = user != null && !readOnly;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle(readOnly ? "Detalle del Usuario" : isEdit ? "Editar Usuario" : "Nuevo Usuario");
        dialog.setSize(780, readOnly ? 590 : 640);
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

        JTextField txtId = createFormTextField();
        JTextField txtUserName = createFormTextField();
        JTextField txtFullName = createFormTextField();
        JTextField txtEmail = createFormTextField();
        JTextField txtPhone = createFormTextField();
        JTextField txtProfileImagePath = createFormTextField();
        JComboBox<SelectOption> cmbStatus = createStatusComboBox();
        JPasswordField txtPassword = createPasswordField();
        JPasswordField txtConfirmPassword = createPasswordField();
        JTextField txtCreatedAt = createFormTextField();
        JTextField txtUpdatedAt = createFormTextField();

        JPanel imagePathPanel = createImagePathPanel(txtProfileImagePath, readOnly);

        if (user != null) {
            txtId.setText(String.valueOf(user.getIdUser()));
            txtUserName.setText(nullToEmpty(user.getUserName()));
            txtFullName.setText(nullToEmpty(user.getFullName()));
            txtEmail.setText(nullToEmpty(user.getEmail()));
            txtPhone.setText(nullToEmpty(user.getPhone()));
            txtProfileImagePath.setText(nullToEmpty(user.getProfileImagePath()));
            selectStatus(cmbStatus, user.getStatus());
            txtCreatedAt.setText(formatDate(user.getCreatedAt()));
            txtUpdatedAt.setText(formatDate(user.getUpdatedAt()));
        }

        boolean editable = !readOnly;
        txtId.setEditable(false);
        txtUserName.setEditable(editable);
        txtFullName.setEditable(editable);
        txtEmail.setEditable(editable);
        txtPhone.setEditable(editable);
        txtProfileImagePath.setEditable(editable);
        cmbStatus.setEnabled(editable);
        txtPassword.setEditable(editable);
        txtConfirmPassword.setEditable(editable);
        txtCreatedAt.setEditable(false);
        txtUpdatedAt.setEditable(false);

        int row = 0;

        if (user != null) {
            row = addField(form, gbc, row, "ID", txtId, "Estado", cmbStatus);
        } else {
            row = addField(form, gbc, row, "Estado", cmbStatus, "", createEmptyPanel());
        }

        row = addField(form, gbc, row, "Usuario", txtUserName, "Nombre completo", txtFullName);
        row = addField(form, gbc, row, "Correo", txtEmail, "Telefono", txtPhone);
        row = addWideField(form, gbc, row, "Ruta foto de perfil", readOnly ? txtProfileImagePath : imagePathPanel);

        if (!readOnly) {
            String passwordLabel = isEdit ? "Nueva contrasena" : "Contrasena";
            String confirmLabel = isEdit ? "Confirmar nueva contrasena" : "Confirmar contrasena";
            row = addField(form, gbc, row, passwordLabel, txtPassword, confirmLabel, txtConfirmPassword);
        }

        if (user != null) {
            row = addField(form, gbc, row, "Fecha de creacion", txtCreatedAt, "Fecha de actualizacion", txtUpdatedAt);
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
                    User formUser = new User();

                    if (isEdit) {
                        formUser.setIdUser(user.getIdUser());
                    }

                    formUser.setUserName(txtUserName.getText());
                    formUser.setFullName(txtFullName.getText());
                    formUser.setEmail(txtEmail.getText());
                    formUser.setPhone(txtPhone.getText());
                    formUser.setProfileImagePath(txtProfileImagePath.getText());
                    formUser.setStatus(getSelectedStatus(cmbStatus));

                    if (isEdit) {
                        controller.updateUser(formUser, txtPassword.getPassword(), txtConfirmPassword.getPassword());
                    } else {
                        controller.createUser(formUser, txtPassword.getPassword(), txtConfirmPassword.getPassword());
                    }

                    dialog.dispose();
                    loadTable();

                    JOptionPane.showMessageDialog(
                            this,
                            isEdit ? "Usuario actualizado correctamente." : "Usuario registrado correctamente.",
                            "Operacion exitosa",
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

    private JPanel createImagePathPanel(JTextField txtProfileImagePath, boolean readOnly) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JButton btnBrowse = createActionButton(FontAwesome.FOLDER_OPEN, primaryColor, "Seleccionar imagen");
        btnBrowse.setEnabled(!readOnly);
        btnBrowse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                txtProfileImagePath.setText(selectedFile.getAbsolutePath());
            }
        });

        panel.add(txtProfileImagePath, BorderLayout.CENTER);
        panel.add(btnBrowse, BorderLayout.EAST);
        return panel;
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

    private int addWideField(JPanel form, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        form.add(createFormLabel(label), gbc);

        row++;

        gbc.gridy = row;
        form.add(field, gbc);
        gbc.gridwidth = 1;

        return row + 1;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JPanel createEmptyPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private JTextField createFormTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(300, 36));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 36));
        return passwordField;
    }

    private JComboBox<SelectOption> createStatusComboBox() {
        JComboBox<SelectOption> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(300, 36));
        comboBox.addItem(new SelectOption(1, "Activo"));
        comboBox.addItem(new SelectOption(0, "Inactivo"));
        return comboBox;
    }

    private int getSelectedStatus(JComboBox<SelectOption> comboBox) {
        SelectOption option = (SelectOption) comboBox.getSelectedItem();
        return option == null ? 1 : option.getId();
    }

    private void selectStatus(JComboBox<SelectOption> comboBox, int status) {
        int normalizedStatus = status == 0 ? 0 : 1;

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            SelectOption option = comboBox.getItemAt(i);

            if (option != null && option.getId() == normalizedStatus) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }

        comboBox.setSelectedIndex(0);
    }

    private void viewDetail(int idUser) {
        try {
            User user = controller.findUserById(idUser);
            openFormDialog(user, true);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void editUser(int idUser) {
        try {
            User user = controller.findUserById(idUser);
            openFormDialog(user, false);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deletePhysical(int idUser) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Deseas eliminar fisicamente este usuario?\nEsta accion no se puede deshacer.",
                "Confirmar eliminacion fisica",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteUserPhysical(idUser);
            loadTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Usuario eliminado fisicamente.",
                    "Operacion exitosa",
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

    private class ActionsRenderer extends JPanel implements TableCellRenderer {

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 7));
            setOpaque(true);

            add(createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle"));
            add(createActionButton(FontAwesome.PENCIL, warningColor, "Editar"));
            add(createActionButton(FontAwesome.TRASH, dangerColor, "Eliminar fisico"));
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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 7));
            panel.setBackground(Color.WHITE);

            JButton btnView = createActionButton(FontAwesome.EYE, primaryColor, "Ver detalle");
            JButton btnEdit = createActionButton(FontAwesome.PENCIL, warningColor, "Editar");
            JButton btnDelete = createActionButton(FontAwesome.TRASH, dangerColor, "Eliminar fisico");

            btnView.addActionListener(e -> {
                fireEditingStopped();
                viewDetail(selectedId);
            });

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                editUser(selectedId);
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                deletePhysical(selectedId);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.out.println("No se pudo cargar el LookAndFeel.");
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Usuarios");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 760);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new UserJPanel());
            frame.setVisible(true);
        });
    }
}
