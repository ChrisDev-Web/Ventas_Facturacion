package Paneles;

import Controllers.UserController;
import Models.User;
import Presentacion.SectionRefreshable;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProfileJPanel extends JPanel implements SectionRefreshable {

    private final UserController controller;
    private final int idUser;
    private final Consumer<User> onProfileUpdated;

    private User currentUser;

    private AvatarPreviewPanel avatarPreview;
    private JLabel lblDisplayName;
    private JLabel lblUserName;
    private JLabel lblEmail;
    private JLabel lblPhone;
    private JLabel lblStatus;
    private JLabel lblCreatedAt;
    private JLabel lblUpdatedAt;

    private JTextField txtUserName;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtPhotoPath;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color cardColor = Color.WHITE;
    private final Color borderColor = new Color(225, 225, 225);
    private final Color textColor = new Color(33, 33, 33);
    private final Color mutedText = new Color(105, 105, 105);
    private final Color primaryColor = new Color(30, 136, 229);
    private final Color successColor = new Color(46, 125, 50);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProfileJPanel(int idUser, Consumer<User> onProfileUpdated) {
        this.controller = new UserController();
        this.idUser = idUser;
        this.onProfileUpdated = onProfileUpdated;
        initUI();
        loadProfile();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(backgroundColor);

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Perfil");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Actualiza tu información personal y la foto que se mostrará en el sidebar.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(mutedText);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setBackground(backgroundColor);
        content.add(createSummaryCard(), BorderLayout.WEST);
        content.add(createFormCard(), BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard() {
        JPanel card = new JPanel();
        card.setBackground(cardColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(320, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        avatarPreview = new AvatarPreviewPanel(null, null);
        avatarPreview.setAlignmentX(CENTER_ALIGNMENT);
        avatarPreview.setPreferredSize(new Dimension(130, 130));
        avatarPreview.setMaximumSize(new Dimension(130, 130));
        avatarPreview.setMinimumSize(new Dimension(130, 130));

        lblDisplayName = createSummaryValue(new Font("Segoe UI", Font.BOLD, 20), textColor);
        lblDisplayName.setAlignmentX(CENTER_ALIGNMENT);

        lblUserName = createSummaryValue(new Font("Segoe UI", Font.PLAIN, 14), mutedText);
        lblUserName.setAlignmentX(CENTER_ALIGNMENT);

        card.add(avatarPreview);
        card.add(Box.createVerticalStrut(16));
        card.add(lblDisplayName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblUserName);
        card.add(Box.createVerticalStrut(20));

        lblEmail = createInfoLine(card, "Correo");
        lblPhone = createInfoLine(card, "Teléfono");
        lblStatus = createInfoLine(card, "Estado");
        lblCreatedAt = createInfoLine(card, "Creado");
        lblUpdatedAt = createInfoLine(card, "Actualizado");

        return card;
    }

    private JLabel createSummaryValue(Font font, Color color) {
        JLabel label = new JLabel("-");
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JLabel createInfoLine(JPanel parent, String title) {
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(mutedText);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel("-");
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblValue.setForeground(textColor);
        lblValue.setAlignmentX(LEFT_ALIGNMENT);

        parent.add(lblTitle);
        parent.add(Box.createVerticalStrut(4));
        parent.add(lblValue);
        parent.add(Box.createVerticalStrut(14));

        return lblValue;
    }

    private JScrollPane createFormCard() {
        JPanel card = new JPanel();
        card.setBackground(cardColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel sectionTitle = new JLabel("Configuración del perfil");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        sectionTitle.setForeground(textColor);
        sectionTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sectionSubtitle = new JLabel("Puedes actualizar tu usuario, nombre visible, contacto, foto y contraseña.");
        sectionSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionSubtitle.setForeground(mutedText);
        sectionSubtitle.setAlignmentX(LEFT_ALIGNMENT);

        card.add(sectionTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(sectionSubtitle);
        card.add(Box.createVerticalStrut(22));

        JPanel fieldsGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        fieldsGrid.setOpaque(false);

        txtUserName = new JTextField();
        txtFullName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        fieldsGrid.add(createFieldCard("Usuario", txtUserName));
        fieldsGrid.add(createFieldCard("Nombre completo", txtFullName));
        fieldsGrid.add(createFieldCard("Correo", txtEmail));
        fieldsGrid.add(createFieldCard("Teléfono", txtPhone));
        fieldsGrid.add(createFieldCard("Nueva contraseña", txtPassword));
        fieldsGrid.add(createFieldCard("Confirmar contraseña", txtConfirmPassword));

        card.add(fieldsGrid);
        card.add(Box.createVerticalStrut(18));

        JPanel photoCard = new JPanel(new BorderLayout(0, 10));
        photoCard.setOpaque(false);
        photoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblPhotoTitle = new JLabel("Foto de perfil");
        lblPhotoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPhotoTitle.setForeground(textColor);

        txtPhotoPath = new JTextField();
        txtPhotoPath.setPreferredSize(new Dimension(0, 36));

        JPanel photoButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        photoButtons.setOpaque(false);

        JButton btnBrowse = createButton("Elegir imagen", primaryColor);
        JButton btnClearPhoto = createButton("Quitar foto", new Color(120, 120, 120));

        btnBrowse.addActionListener(e -> choosePhoto());
        btnClearPhoto.addActionListener(e -> clearPhoto());

        photoButtons.add(btnBrowse);
        photoButtons.add(btnClearPhoto);

        photoCard.add(lblPhotoTitle, BorderLayout.NORTH);
        photoCard.add(txtPhotoPath, BorderLayout.CENTER);
        photoCard.add(photoButtons, BorderLayout.SOUTH);

        card.add(photoCard);
        card.add(Box.createVerticalStrut(22));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnReload = createButton("Recargar", new Color(90, 90, 90));
        JButton btnSave = createButton("Guardar cambios", successColor);

        btnReload.addActionListener(e -> loadProfile());
        btnSave.addActionListener(e -> saveProfile());

        actions.add(btnReload);
        actions.add(btnSave);

        card.add(actions);

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createFieldCard(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLabel.setForeground(textColor);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 36));

        panel.add(lblLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
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
        button.setPreferredSize(new Dimension(Math.max(130, text.length() * 10 + 32), 38));
        return button;
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar foto de perfil");
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "png", "jpg", "jpeg", "gif", "bmp"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            txtPhotoPath.setText(chooser.getSelectedFile().getAbsolutePath());
            updateAvatarPreview(txtPhotoPath.getText(), txtFullName.getText(), txtUserName.getText());
        }
    }

    private void clearPhoto() {
        txtPhotoPath.setText("");
        updateAvatarPreview(null, txtFullName.getText(), txtUserName.getText());
    }

    private void saveProfile() {
        try {
            User updatedUser = controller.updateProfile(
                    idUser,
                    txtUserName.getText(),
                    txtFullName.getText(),
                    txtEmail.getText(),
                    txtPhone.getText(),
                    txtPhotoPath.getText(),
                    txtPassword.getPassword(),
                    txtConfirmPassword.getPassword()
            );

            currentUser = updatedUser;
            fillForm(updatedUser);

            if (onProfileUpdated != null) {
                onProfileUpdated.accept(updatedUser);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Perfil actualizado correctamente.",
                    "Perfil guardado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadProfile() {
        try {
            currentUser = controller.findUserProfile(idUser);
            fillForm(currentUser);

            if (onProfileUpdated != null && currentUser != null) {
                onProfileUpdated.accept(currentUser);
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void fillForm(User user) {
        if (user == null) {
            return;
        }

        txtUserName.setText(safe(user.getUserName()));
        txtFullName.setText(safe(user.getFullName()));
        txtEmail.setText(safe(user.getEmail()));
        txtPhone.setText(safe(user.getPhone()));
        txtPhotoPath.setText(safe(user.getProfileImagePath()));
        txtPassword.setText("");
        txtConfirmPassword.setText("");

        lblDisplayName.setText(user.getDisplayName());
        lblUserName.setText("@" + safe(user.getUserName(), "usuario"));
        lblEmail.setText(safe(user.getEmail(), "No registrado"));
        lblPhone.setText(safe(user.getPhone(), "No registrado"));
        lblStatus.setText(user.isActive() ? "Activo" : "Inactivo");
        lblCreatedAt.setText(user.getCreatedAt() == null ? "-" : user.getCreatedAt().format(dateFormatter));
        lblUpdatedAt.setText(user.getUpdatedAt() == null ? "-" : user.getUpdatedAt().format(dateFormatter));

        updateAvatarPreview(user.getProfileImagePath(), user.getFullName(), user.getUserName());
    }

    private void updateAvatarPreview(String imagePath, String fullName, String userName) {
        avatarPreview.setDisplayName(firstNonBlank(fullName, userName, "Usuario"));
        avatarPreview.setProfileImage(loadImage(imagePath));
    }

    private Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        try {
            File file = new File(imagePath.trim());
            if (file.exists() && file.isFile()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar la foto de perfil.");
        }

        return null;
    }

    private String safe(String value) {
        return safe(value, "");
    }

    private String safe(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }

        return "";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void refreshSectionData() {
        loadProfile();
    }

    private static class AvatarPreviewPanel extends JPanel {

        private String displayName;
        private Image profileImage;

        public AvatarPreviewPanel(String displayName, Image profileImage) {
            this.displayName = displayName == null || displayName.trim().isEmpty() ? "Usuario" : displayName.trim();
            this.profileImage = profileImage;
            setOpaque(false);
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName == null || displayName.trim().isEmpty() ? "Usuario" : displayName.trim();
            repaint();
        }

        public void setProfileImage(Image profileImage) {
            this.profileImage = profileImage;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 8;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            Ellipse2D circle = new Ellipse2D.Double(x, y, size, size);

            if (profileImage != null) {
                g2.setClip(circle);
                g2.drawImage(profileImage, x, y, size, size, this);
                g2.setClip(null);
            } else {
                g2.setColor(new Color(30, 136, 229));
                g2.fill(circle);

                String initials = getInitials(displayName);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 34));

                FontMetrics metrics = g2.getFontMetrics();
                int textX = x + (size - metrics.stringWidth(initials)) / 2;
                int textY = y + ((size - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.drawString(initials, textX, textY);
            }

            g2.setStroke(new BasicStroke(3f));
            g2.setColor(Color.WHITE);
            g2.draw(circle);
            g2.dispose();
        }

        private String getInitials(String text) {
            String[] parts = text.trim().split("\\s+");

            if (parts.length >= 2) {
                return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
            }

            if (text.length() >= 2) {
                return text.substring(0, 2).toUpperCase();
            }

            return text.substring(0, 1).toUpperCase();
        }
    }
}
