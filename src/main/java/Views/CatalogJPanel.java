package Views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CatalogJPanel extends JPanel {

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color textColor = new Color(33, 33, 33);
    private final Color cardColor = Color.WHITE;
    private final Color borderColor = new Color(220, 220, 220);

    public CatalogJPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Catálogo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(textColor);

        JLabel lblSubtitle = new JLabel("Administra categorías, marcas y métodos de pago del sistema.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSubtitle, BorderLayout.SOUTH);

        JPanel content = new JPanel(new GridLayout(2, 2, 18, 18));
        content.setBackground(backgroundColor);

        content.add(new CategoryJPanel());
        content.add(new BrandJPanel());
        content.add(new PaymentMethodJPanel());
        content.add(createEmptyCatalogPanel());

        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createEmptyCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblTitle = new JLabel("Próximamente");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(textColor);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitle = new JLabel("Espacio reservado para otro CRUD del catálogo.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(90, 90, 90));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(cardColor);
        centerPanel.add(lblTitle, BorderLayout.CENTER);
        centerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }
}