package Presentacion;

import Models.StockAlert;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class StockAlertToastPanel extends JPanel {

    private final Color warningColor = new Color(245, 158, 11);
    private final Color criticalColor = new Color(220, 38, 38);
    private final Color urgentColor = new Color(153, 27, 27);
    private final Color textColor = new Color(25, 30, 38);
    private final Color mutedText = new Color(92, 99, 112);

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    public StockAlertToastPanel(StockAlert alert) {
        Color accent = severityColor(alert.getSeverity());
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 108));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(8, 0));

        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel icon = new JLabel(buildIcon(alert.getSeverity(), accent));
        icon.setPreferredSize(new Dimension(34, 34));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel title = new JLabel(buildTitle(alert));
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(textColor);

        JLabel message = new JLabel(buildMessage(alert));
        message.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        message.setForeground(textColor);

        JLabel action = new JLabel(safeText(alert.getRecommendedAction(), ""));
        action.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        action.setForeground(mutedText);

        textPanel.add(title);
        textPanel.add(message);
        textPanel.add(action);

        JPanel stockBadgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        stockBadgeWrapper.setOpaque(false);

        JLabel stockBadge = new JLabel("Stock " + alert.getStock());
        stockBadge.setOpaque(true);
        stockBadge.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28));
        stockBadge.setForeground(accent);
        stockBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stockBadge.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        stockBadgeWrapper.add(stockBadge);

        content.add(icon, BorderLayout.WEST);
        content.add(textPanel, BorderLayout.CENTER);
        content.add(stockBadgeWrapper, BorderLayout.EAST);

        add(accentBar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private String buildTitle(StockAlert alert) {
        String severityLabel = safeText(alert.getSeverityLabel(), "Alerta de stock");
        return severityLabel + " - " + safeText(alert.getProductName(), "Producto");
    }

    private String buildMessage(StockAlert alert) {
        String category = safeText(alert.getCategoryName(), "-");
        String brand = safeText(alert.getBrandName(), "-");
        return "<html>" + escapeHtml(category) + " / " + escapeHtml(brand)
                + "<br>El producto ahora tiene " + alert.getStock() + " unidades disponibles.</html>";
    }

    private Icon buildIcon(String severity, Color color) {
        FontAwesome iconCode = "WARNING".equalsIgnoreCase(safeText(severity, ""))
                ? FontAwesome.BELL
                : FontAwesome.EXCLAMATION_TRIANGLE;

        return IconFontSwing.buildIcon(iconCode, 18, color);
    }

    private Color severityColor(String severity) {
        String value = safeText(severity, "WARNING").toUpperCase();

        if ("URGENT".equals(value)) {
            return urgentColor;
        }

        if ("CRITICAL".equals(value)) {
            return criticalColor;
        }

        return warningColor;
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private String escapeHtml(String value) {
        return safeText(value, "")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
