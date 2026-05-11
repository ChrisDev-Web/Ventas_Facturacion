package Views;

import Controllers.SaleController;
import Models.Sale;
import Models.SaleDetail;
import Models.SaleProductItem;
import Models.SelectOption;
import com.github.lgooddatepicker.zinternaltools.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SaleJPanel extends JPanel {

    private final SaleController controller;
    private final int idUser;
    private final String userName;

    private JTextField txtSearch;
    private JComboBox<SelectOption> cmbCategory;
    private JComboBox<SelectOption> cmbBrand;

    private JPanel productGrid;
    private JPanel cartListPanel;

    private JComboBox<SelectOption> cmbPaymentMethod;
    private JComboBox<String> cmbDocumentKind;
    private JTextField txtCustomerName;
    private JComboBox<SelectOption> cmbCustomerDocumentType;
    private JTextField txtCustomerDocumentNumber;
    private JTextField txtPaidAmount;

    private JLabel lblSubtotal;
    private JLabel lblIgv;
    private JLabel lblDiscount;
    private JLabel lblTotal;
    private JLabel lblChange;
    private JLabel lblCartCount;

    private final Map<Integer, CartItem> cart = new LinkedHashMap<>();

    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color cardColor = Color.WHITE;
    private final Color primaryColor = new Color(111, 66, 193);
    private final Color successColor = new Color(46, 125, 50);
    private final Color dangerColor = new Color(198, 40, 40);
    private final Color borderColor = new Color(225, 225, 225);
    private final Color textColor = new Color(33, 33, 33);

    public SaleJPanel(int idUser, String userName) {
        this.idUser = idUser;
        this.userName = userName;
        this.controller = new SaleController();
        initUI();
        loadCombos();
        loadProducts();
        refreshCart();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createCartPanel(), BorderLayout.EAST);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 14));
        header.setBackground(backgroundColor);

        JLabel title = new JLabel("Ventas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(textColor);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setBackground(cardColor);
        filters.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(340, 38));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbCategory = new JComboBox<>();
        cmbCategory.setPreferredSize(new Dimension(210, 38));

        cmbBrand = new JComboBox<>();
        cmbBrand.setPreferredSize(new Dimension(210, 38));

        JButton btnSearch = createButton("Buscar", primaryColor);
        JButton btnClear = createButton("Limpiar filtros", new Color(90, 90, 90));

        btnSearch.addActionListener(e -> loadProducts());

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbCategory.setSelectedIndex(0);
            cmbBrand.setSelectedIndex(0);
            loadProducts();
        });

        filters.add(txtSearch);
        filters.add(cmbCategory);
        filters.add(cmbBrand);
        filters.add(btnSearch);
        filters.add(btnClear);

        header.add(title, BorderLayout.NORTH);
        header.add(filters, BorderLayout.SOUTH);

        return header;
    }

    private JScrollPane createCenter() {
        productGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        productGrid.setBackground(backgroundColor);

        JScrollPane scroll = new JScrollPane(productGrid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);

        return scroll;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(390, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(cardColor);

        JLabel title = new JLabel("Carrito");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textColor);

        lblCartCount = new JLabel("0 productos seleccionados");
        lblCartCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCartCount.setForeground(new Color(90, 90, 90));

        top.add(title, BorderLayout.NORTH);
        top.add(lblCartCount, BorderLayout.SOUTH);

        cartListPanel = new JPanel();
        cartListPanel.setLayout(new BoxLayout(cartListPanel, BoxLayout.Y_AXIS));
        cartListPanel.setBackground(cardColor);

        JScrollPane cartScroll = new JScrollPane(cartListPanel);
        cartScroll.setPreferredSize(new Dimension(350, 330));
        cartScroll.setBorder(BorderFactory.createLineBorder(borderColor));
        cartScroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel controls = new JPanel(new GridLayout(0, 2, 8, 8));
        controls.setBackground(cardColor);

        cmbPaymentMethod = new JComboBox<>();
        cmbDocumentKind = new JComboBox<>(new String[]{"TICKET", "BOLETA", "FACTURA"});
        cmbCustomerDocumentType = new JComboBox<>();
        txtCustomerName = new JTextField();
        txtCustomerDocumentNumber = new JTextField();
        txtPaidAmount = new JTextField();
        
        cmbPaymentMethod.addActionListener(e -> updatePaymentControls());

        cmbDocumentKind.addActionListener(e -> updateDocumentControls());

        txtPaidAmount.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotals();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotals();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTotals();
            }
        });

        controls.add(createLabel("Método pago"));
        controls.add(createLabel("Documento"));
        controls.add(cmbPaymentMethod);
        controls.add(cmbDocumentKind);

        controls.add(createLabel("Cliente"));
        controls.add(createLabel("Tipo doc cliente"));
        controls.add(txtCustomerName);
        controls.add(cmbCustomerDocumentType);

        controls.add(createLabel("N° doc cliente"));
        controls.add(createLabel("Pago con"));
        controls.add(txtCustomerDocumentNumber);
        controls.add(txtPaidAmount);

        JPanel totals = new JPanel(new GridLayout(0, 2, 4, 4));
        totals.setBackground(cardColor);

        lblSubtotal = createAmountLabel();
        lblIgv = createAmountLabel();
        lblDiscount = createAmountLabel();
        lblTotal = createTotalLabel();
        lblChange = createAmountLabel();

        totals.add(createLabel("Productos"));
        totals.add(lblSubtotal);
        totals.add(createLabel("IGV 18%"));
        totals.add(lblIgv);
        totals.add(createLabel("Descuento"));
        totals.add(lblDiscount);
        totals.add(createLabel("Vuelto"));
        totals.add(lblChange);
        totals.add(createLabel("Total"));
        totals.add(lblTotal);

        JButton btnConfirm = createButton("Confirmar Pedido", primaryColor);
        btnConfirm.setPreferredSize(new Dimension(350, 42));
        btnConfirm.addActionListener(e -> confirmSale());

        JPanel south = new JPanel(new BorderLayout(0, 12));
        south.setBackground(cardColor);
        south.add(controls, BorderLayout.NORTH);
        south.add(totals, BorderLayout.CENTER);
        south.add(btnConfirm, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(cartScroll, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(90, 90, 90));
        return label;
    }

    private JLabel createAmountLabel() {
        JLabel label = new JLabel("S/ 0.00");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JLabel createTotalLabel() {
        JLabel label = new JLabel("S/ 0.00");
        label.setFont(new Font("Segoe UI", Font.BOLD, 21));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadCombos() {
        try {
            loadCombo(cmbCategory, controller.listCategoryOptions(), "Todas las categorías");
            loadCombo(cmbBrand, controller.listBrandOptions(), "Todas las marcas");
            loadCombo(cmbPaymentMethod, controller.listPaymentMethodOptions(), "Seleccione");
            loadCombo(cmbCustomerDocumentType, controller.listDocumentTypeOptions(), "Sin documento");

            updatePaymentControls();
            updateDocumentControls();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadCombo(JComboBox<SelectOption> combo, List<SelectOption> list, String defaultText) {
        combo.removeAllItems();
        combo.addItem(SelectOption.empty(defaultText));

        for (SelectOption option : list) {
            combo.addItem(option);
        }
    }

    private int selectedId(JComboBox<SelectOption> combo) {
        SelectOption option = (SelectOption) combo.getSelectedItem();
        return option == null ? 0 : option.getId();
    }

    private void loadProducts() {
        try {
            productGrid.removeAll();

            List<SaleProductItem> products = controller.listProducts(
                    txtSearch.getText(),
                    selectedId(cmbCategory),
                    selectedId(cmbBrand)
            );

            for (SaleProductItem product : products) {
                productGrid.add(createProductCard(product));
            }

            productGrid.revalidate();
            productGrid.repaint();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private JPanel createProductCard(SaleProductItem product) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(cardColor);

        Dimension cardSize = new Dimension(210, 285);
        card.setPreferredSize(cardSize);
        card.setMinimumSize(cardSize);
        card.setMaximumSize(cardSize);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel image = new JLabel();
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setVerticalAlignment(SwingConstants.CENTER);
        image.setPreferredSize(new Dimension(190, 125));

        ImageIcon productIcon = getProductImage(product.getImage(), 190, 125);

        if (productIcon != null) {
            image.setIcon(productIcon);
        } else {
            image.setText("Sin imagen");
            image.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            image.setForeground(new Color(130, 130, 130));
        }

        JLabel name = new JLabel("<html><b>#" + product.getIdProduct() + " "
                + shorten(product.getName(), 34) + "</b></html>");
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setForeground(textColor);

        JLabel meta = new JLabel(shorten(product.getCategoryName(), 15) + " · " + shorten(product.getBrandName(), 15));
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        meta.setForeground(new Color(90, 90, 90));

        JLabel stock = new JLabel("Stock: " + product.getStock());
        stock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stock.setForeground(successColor);

        JLabel price = new JLabel("S/ " + money(product.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 16));
        price.setForeground(primaryColor);

        JButton add = createButton("+", primaryColor);
        add.setPreferredSize(new Dimension(42, 32));
        add.addActionListener(e -> addToCart(product));

        JPanel info = new JPanel(new GridLayout(0, 1, 2, 2));
        info.setBackground(cardColor);
        info.add(name);
        info.add(meta);
        info.add(stock);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(cardColor);
        bottom.add(price, BorderLayout.WEST);
        bottom.add(add, BorderLayout.EAST);

        card.add(image, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private ImageIcon getProductImage(String base64, int width, int height) {
        try {
            if (base64 == null || base64.trim().isEmpty()) {
                return null;
            }

            byte[] bytes = Base64.getDecoder().decode(base64);
            ImageIcon icon = new ImageIcon(bytes);
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);

        } catch (Exception e) {
            return null;
        }
    }

    private void addToCart(SaleProductItem product) {
        CartItem item = cart.get(product.getIdProduct());

        if (item == null) {
            item = new CartItem(product);
            cart.put(product.getIdProduct(), item);
        } else {
            if (item.quantity >= product.getStock()) {
                showError("No hay stock suficiente.");
                return;
            }
            item.quantity++;
        }

        refreshCart();
    }

    private void refreshCart() {
        cartListPanel.removeAll();

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("<html><center>Agrega productos del catálogo<br>para iniciar el pedido.</center></html>");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(120, 120, 120));
            empty.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(cardColor);
            emptyPanel.setPreferredSize(new Dimension(330, 260));
            emptyPanel.add(empty, BorderLayout.CENTER);

            cartListPanel.add(emptyPanel);
        } else {
            for (CartItem item : cart.values()) {
                cartListPanel.add(createCartRow(item));
            }
        }

        cartListPanel.revalidate();
        cartListPanel.repaint();

        updateTotals();
    }

    private JPanel createCartRow(CartItem item) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(cardColor);

        Dimension rowSize = new Dimension(330, 62);
        row.setPreferredSize(rowSize);
        row.setMaximumSize(rowSize);
        row.setMinimumSize(rowSize);

        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
                BorderFactory.createEmptyBorder(7, 5, 7, 5)
        ));

        JLabel name = new JLabel(shorten(item.product.getName(), 24));
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setForeground(textColor);

        JLabel price = new JLabel("S/ " + money(item.product.getPrice()) + " x " + item.quantity);
        price.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        price.setForeground(new Color(90, 90, 90));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setBackground(cardColor);
        textPanel.add(name);
        textPanel.add(price);

        JButton minus = createSmallButton("-");
        JButton plus = createSmallButton("+");
        JButton remove = createSmallButton("x");

        minus.addActionListener(e -> {
            if (item.quantity > 1) {
                item.quantity--;
            } else {
                cart.remove(item.product.getIdProduct());
            }

            refreshCart();
        });

        plus.addActionListener(e -> {
            if (item.quantity >= item.product.getStock()) {
                showError("No hay stock suficiente.");
                return;
            }

            item.quantity++;
            refreshCart();
        });

        remove.addActionListener(e -> {
            cart.remove(item.product.getIdProduct());
            refreshCart();
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 8));
        actions.setBackground(cardColor);
        actions.add(minus);
        actions.add(plus);
        actions.add(remove);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(actions, BorderLayout.EAST);

        return row;
    }

    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(38, 26));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void updateTotals() {
        BigDecimal total = calculateCartTotal();

        int count = 0;

        for (CartItem item : cart.values()) {
            count += item.quantity;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            subtotal = total.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
            igv = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
        }

        lblSubtotal.setText("S/ " + money(subtotal));
        lblIgv.setText("S/ " + money(igv));
        lblDiscount.setText("S/ 0.00");
        lblTotal.setText("S/ " + money(total));

        BigDecimal change = BigDecimal.ZERO;

        if (isCashPaymentSelected()) {
            BigDecimal paid = parseMoneySafe(txtPaidAmount.getText());
            change = paid.subtract(total);

            if (change.compareTo(BigDecimal.ZERO) < 0) {
                change = BigDecimal.ZERO;
            }
        }

        lblChange.setText("S/ " + money(change));
        lblCartCount.setText(count + " productos seleccionados");
    }
    
    private boolean isCashPaymentSelected() {
        SelectOption option = (SelectOption) cmbPaymentMethod.getSelectedItem();

        if (option == null || option.getName() == null) {
            return false;
        }

        return option.getName().trim().equalsIgnoreCase("Efectivo");
    }

    private boolean isTicketSelected() {
        String documentKind = (String) cmbDocumentKind.getSelectedItem();

        if (documentKind == null) {
            return true;
        }

        return documentKind.trim().equalsIgnoreCase("TICKET");
    }

    private void updatePaymentControls() {
        boolean isCash = isCashPaymentSelected();

        txtPaidAmount.setEnabled(isCash);
        txtPaidAmount.setEditable(isCash);

        if (!isCash) {
            txtPaidAmount.setText("");
        }

        updateTotals();
    }

    private void updateDocumentControls() {
        boolean isTicket = isTicketSelected();

        txtCustomerName.setEnabled(!isTicket);
        txtCustomerName.setEditable(!isTicket);

        cmbCustomerDocumentType.setEnabled(!isTicket);

        txtCustomerDocumentNumber.setEnabled(!isTicket);
        txtCustomerDocumentNumber.setEditable(!isTicket);

        if (isTicket) {
            txtCustomerName.setText("");
            cmbCustomerDocumentType.setSelectedIndex(0);
            txtCustomerDocumentNumber.setText("");
        }
    }

    private BigDecimal calculateCartTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.values()) {
            total = total.add(
                    item.product.getPrice().multiply(BigDecimal.valueOf(item.quantity))
            );
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private void confirmSale() {
        try {
            if (cart.isEmpty()) {
                throw new Exception("Agregue productos al carrito.");
            }

            int idPaymentMethod = selectedId(cmbPaymentMethod);

            if (idPaymentMethod <= 0) {
                throw new Exception("Seleccione un método de pago.");
            }

            BigDecimal total = calculateCartTotal();
            BigDecimal paidAmount;

            if (isCashPaymentSelected()) {
                paidAmount = parseMoney(txtPaidAmount.getText());

                if (paidAmount.compareTo(total) < 0) {
                    throw new Exception("El monto pagado no puede ser menor al total.");
                }
            } else {
                paidAmount = total;
            }

            String documentKind = (String) cmbDocumentKind.getSelectedItem();

            String customerName = null;
            Integer customerDocumentTypeId = null;
            String customerDocumentNumber = null;

            if (!isTicketSelected()) {
                if (txtCustomerName.getText() == null || txtCustomerName.getText().trim().isEmpty()) {
                    throw new Exception("Ingrese el nombre del cliente.");
                }

                int selectedDocumentTypeId = selectedId(cmbCustomerDocumentType);

                if (selectedDocumentTypeId <= 0) {
                    throw new Exception("Seleccione el tipo de documento del cliente.");
                }

                if (txtCustomerDocumentNumber.getText() == null || txtCustomerDocumentNumber.getText().trim().isEmpty()) {
                    throw new Exception("Ingrese el número de documento del cliente.");
                }

                customerName = txtCustomerName.getText().trim();
                customerDocumentTypeId = selectedDocumentTypeId;
                customerDocumentNumber = txtCustomerDocumentNumber.getText().trim();
            }

            Sale sale = new Sale();
            sale.setIdUser(idUser);
            sale.setIdPaymentMethod(idPaymentMethod);
            sale.setDocumentKind(documentKind);
            sale.setCustomerName(customerName);
            sale.setCustomerDocumentTypeId(customerDocumentTypeId);
            sale.setCustomerDocumentNumber(customerDocumentNumber);
            sale.setPaidAmount(paidAmount);

            List<SaleDetail> details = new ArrayList<>();

            for (CartItem item : cart.values()) {
                SaleDetail detail = new SaleDetail();
                detail.setIdProduct(item.product.getIdProduct());
                detail.setProductName(item.product.getName());
                detail.setQuantity(item.quantity);
                detail.setDiscountType("NONE");
                detail.setDiscountValue(BigDecimal.ZERO);
                details.add(detail);
            }

            sale.setDetails(details);

            Sale result = controller.createSale(sale);

            JOptionPane.showMessageDialog(
                    this,
                    "Venta registrada correctamente.\nComprobante: " + result.getVoucherCode()
                    + "\nTotal: S/ " + money(result.getTotal())
                    + "\nVuelto: S/ " + money(result.getChangeAmount()),
                    "Venta registrada",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cart.clear();
            txtPaidAmount.setText("");
            txtCustomerName.setText("");
            txtCustomerDocumentNumber.setText("");
            cmbDocumentKind.setSelectedItem("TICKET");
            cmbCustomerDocumentType.setSelectedIndex(0);

            loadProducts();
            refreshCart();
            updatePaymentControls();
            updateDocumentControls();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private BigDecimal parseMoney(String value) throws Exception {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }

            return new BigDecimal(value.trim()).setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            throw new Exception("Ingrese un monto válido.");
        }
    }

    private BigDecimal parseMoneySafe(String value) {
        try {
            return parseMoney(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String money(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private String shorten(String value, int length) {
        if (value == null) {
            return "";
        }

        if (value.length() <= length) {
            return value;
        }

        return value.substring(0, length) + "...";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class CartItem {

        private final SaleProductItem product;
        private int quantity;

        public CartItem(SaleProductItem product) {
            this.product = product;
            this.quantity = 1;
        }
    }
    
    private static class WrapLayout extends FlowLayout {

        public WrapLayout() {
            super();
        }

        public WrapLayout(int align) {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(java.awt.Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(java.awt.Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= getHgap() + 1;
            return minimum;
        }

        private Dimension layoutSize(java.awt.Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();

                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();

                java.awt.Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + hgap * 2;
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);

                int rowWidth = 0;
                int rowHeight = 0;

                int componentCount = target.getComponentCount();

                for (int i = 0; i < componentCount; i++) {
                    java.awt.Component component = target.getComponent(i);

                    if (component.isVisible()) {
                        Dimension d = preferred ? component.getPreferredSize() : component.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }

                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);

            if (dim.height > 0) {
                dim.height += getVgap();
            }

            dim.height += rowHeight;
        }
    }
}