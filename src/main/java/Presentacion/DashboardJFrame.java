package Presentacion;

import Controllers.UserController;
import Paneles.EmployeeJPanel;
import Paneles.UserJPanel;
import Paneles.SupplierJPanel;
import Paneles.LocationJPanel;
import Paneles.SaleHistoryJPanel;
import Paneles.ProductJPanel;
import Paneles.CatalogJPanel;
import Paneles.SaleJPanel;
import Paneles.ClientJPanel;
import Paneles.ProfileJPanel;
import Paneles.DashboardJPanel;
import Paneles.RoleJPanel;
import Paneles.DocumentTypeJPanel;
import Paneles.StockMovementJPanel;
import Paneles.InventoryJPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;
import Models.User;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class DashboardJFrame extends JFrame {

    private static final int FRAME_WIDTH = 1920;
    private static final int FRAME_HEIGHT = 1080;
    private static final int SIDEBAR_WIDTH = 300;

    private final UserController userController = new UserController();
    private User currentUser;
    private String userName;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel menuContainer;
    private JPanel configSubMenuPanel;
    private CircularAvatar sidebarAvatar;
    private JLabel lblSidebarUser;
    private JLabel lblSidebarUserName;
    private JLabel lblDashboardWelcome;
    private DashboardJPanel dashboardPanel;

    private SideMenuItem itemDashboard;
    private SideMenuItem itemAlertas;
    private SideMenuItem itemVentas;
    private SideMenuItem itemHistorialVentas;
    private SideMenuItem itemProductos;
    private SideMenuItem itemInventario;
    private SideMenuItem itemMovimientosStock;
    private SideMenuItem itemClientes;
    private SideMenuItem itemProveedores;
    private SideMenuItem itemEmpleados;
    private SideMenuItem itemUsuarios;
    private SideMenuItem itemReportes;
    private SideMenuItem itemConfiguracion;

    private SideMenuItem itemTipoDocumentos;
    private SideMenuItem itemRoles;
    private SideMenuItem itemUbicacion;
    private SideMenuItem itemCatalogo;
    private SideMenuItem itemPerfil;

    private final List<SideMenuItem> menuItems = new ArrayList<>();

    private boolean configExpanded = false;
    private Timer configTimer;

    private final int configFullHeight = 260;

    private final Color sidebarTopColor = new Color(9, 26, 48);
    private final Color sidebarBottomColor = new Color(7, 21, 39);
    private final Color activeBlue = new Color(28, 137, 255);
    private final Color hoverBlue = new Color(24, 60, 96);
    private final Color whiteText = new Color(240, 245, 250);
    private final Color mutedText = new Color(180, 195, 210);
    private final Color backgroundColor = new Color(245, 247, 250);
    private final Color cardColor = Color.WHITE;
    private final Color darkText = new Color(33, 33, 33);

    static {
        IconFontSwing.register(FontAwesome.getIconFont());
    }

    public DashboardJFrame() {
        this.currentUser = null;
        this.userName = "Usuario Demo";
        initUI();
    }

    public DashboardJFrame(String userName) {
        this.currentUser = null;
        this.userName = userName == null || userName.trim().isEmpty()
                ? "Usuario Demo"
                : userName.trim();
        initUI();
    }

    public DashboardJFrame(User user) {
        this.currentUser = user;

        if (user != null && user.getUserName() != null && !user.getUserName().trim().isEmpty()) {
            this.userName = user.getUserName().trim();
        } else {
            this.userName = "Usuario Demo";
        }

        initUI();
    }

    private int getSafeUserId() {
        if (currentUser != null && currentUser.getIdUser() > 0) {
            return currentUser.getIdUser();
        }

        return 1;
    }

    private String getSafeUserName() {
        if (currentUser != null && currentUser.getUserName() != null && !currentUser.getUserName().trim().isEmpty()) {
            return currentUser.getUserName().trim();
        }

        if (userName != null && !userName.trim().isEmpty()) {
            return userName.trim();
        }

        return "Usuario Demo";
    }

    private String getSafeDisplayName() {
        if (currentUser != null) {
            return currentUser.getDisplayName();
        }

        if (userName != null && !userName.trim().isEmpty()) {
            return userName.trim();
        }

        return "Usuario Demo";
    }

    private void initUI() {
        setTitle("KMLLogistics - Sistema de Ventas y Facturacion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        mainPanel.add(createSidebar(), BorderLayout.WEST);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

        setContentPane(mainPanel);

        showSection("DASHBOARD", itemDashboard);
    }

    private JPanel createSidebar() {
        GradientSidebarPanel sidebar = new GradientSidebarPanel();
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, FRAME_HEIGHT));
        sidebar.setLayout(new BorderLayout());

        sidebar.add(createSidebarHeader(), BorderLayout.NORTH);
        sidebar.add(createMenuScrollPane(), BorderLayout.CENTER);
        sidebar.add(createSidebarFooter(), BorderLayout.SOUTH);

        return sidebar;
    }
    
    private JScrollPane createMenuScrollPane() {
        JPanel menuPanel = createMenuContainer();

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(255, 255, 255, 70);
                this.trackColor = new Color(0, 0, 0, 0);
            }

            @Override
            protected javax.swing.JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected javax.swing.JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private javax.swing.JButton createZeroButton() {
                javax.swing.JButton button = new javax.swing.JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                return button;
            }
        });

        return scrollPane;
    }

    private JPanel createSidebarHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(35, 20, 25, 20));

        LogoLabel logo = new LogoLabel();
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setPreferredSize(new Dimension(240, 48));
        logo.setMaximumSize(new Dimension(240, 48));

        sidebarAvatar = new CircularAvatar(getSafeDisplayName(), loadProfileImage());
        sidebarAvatar.setAlignmentX(CENTER_ALIGNMENT);
        sidebarAvatar.setPreferredSize(new Dimension(88, 88));
        sidebarAvatar.setMaximumSize(new Dimension(88, 88));
        sidebarAvatar.setMinimumSize(new Dimension(88, 88));

        lblSidebarUser = new JLabel(getSafeDisplayName());
        lblSidebarUser.setAlignmentX(CENTER_ALIGNMENT);
        lblSidebarUser.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSidebarUser.setForeground(whiteText);

        lblSidebarUserName = new JLabel("@" + getSafeUserName());
        lblSidebarUserName.setAlignmentX(CENTER_ALIGNMENT);
        lblSidebarUserName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSidebarUserName.setForeground(mutedText);

        JPanel onlinePanel = new JPanel();
        onlinePanel.setOpaque(false);
        onlinePanel.setLayout(new BoxLayout(onlinePanel, BoxLayout.X_AXIS));
        onlinePanel.setAlignmentX(CENTER_ALIGNMENT);

        OnlineDot dot = new OnlineDot();
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setMaximumSize(new Dimension(10, 10));

        JLabel lblOnline = new JLabel(" En linea");
        lblOnline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblOnline.setForeground(mutedText);

        onlinePanel.add(dot);
        onlinePanel.add(lblOnline);

        header.add(logo);
        header.add(Box.createVerticalStrut(22));
        header.add(sidebarAvatar);
        header.add(Box.createVerticalStrut(10));
        header.add(lblSidebarUser);
        header.add(Box.createVerticalStrut(3));
        header.add(lblSidebarUserName);
        header.add(Box.createVerticalStrut(4));
        header.add(onlinePanel);

        return header;
    }

    private JPanel createMenuContainer() {
        menuContainer = new JPanel();
        menuContainer.setOpaque(false);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));

        itemDashboard = createMenuItem(FontAwesome.HOME, "Dashboard", false);
        itemAlertas = createMenuItem(FontAwesome.BELL, "Alertas", false);
        itemVentas = createMenuItem(FontAwesome.SHOPPING_CART, "Ventas", false);
        itemHistorialVentas = createMenuItem(FontAwesome.LIST_ALT, "Historial Ventas", false);
        itemProductos = createMenuItem(FontAwesome.CUBE, "Productos", false);
        itemInventario = createMenuItem(FontAwesome.ARCHIVE, "Inventario", false);
        itemMovimientosStock = createMenuItem(FontAwesome.EXCHANGE, "Movimientos Stock", false);
        itemClientes = createMenuItem(FontAwesome.USERS, "Clientes", false);
        itemProveedores = createMenuItem(FontAwesome.TRUCK, "Proveedores", false);
        itemEmpleados = createMenuItem(FontAwesome.USER, "Empleados", false);
        itemUsuarios = createMenuItem(FontAwesome.USER_CIRCLE, "Usuarios", false);
        itemReportes = createMenuItem(FontAwesome.BAR_CHART, "Reportes", false);
        itemConfiguracion = createMenuItem(FontAwesome.COG, "Configuracion", true);

        itemDashboard.addClickAction(() -> showSection("DASHBOARD", itemDashboard));
        itemAlertas.addClickAction(() -> showSection("ALERTAS", itemAlertas));
        itemVentas.addClickAction(() -> showSection("VENTAS", itemVentas));
        itemHistorialVentas.addClickAction(() -> showSection("HISTORIAL_VENTAS", itemHistorialVentas));
        itemProductos.addClickAction(() -> showSection("PRODUCTOS", itemProductos));
        itemInventario.addClickAction(() -> showSection("INVENTARIO", itemInventario));
        itemMovimientosStock.addClickAction(() -> showSection("MOVIMIENTOS_STOCK", itemMovimientosStock));
        itemClientes.addClickAction(() -> showSection("CLIENTES", itemClientes));
        itemProveedores.addClickAction(() -> showSection("PROVEEDORES", itemProveedores));
        itemEmpleados.addClickAction(() -> showSection("EMPLEADOS", itemEmpleados));
        itemUsuarios.addClickAction(() -> showSection("USUARIOS", itemUsuarios));
        itemReportes.addClickAction(() -> showSection("REPORTES", itemReportes));
        itemConfiguracion.addClickAction(() -> toggleConfigMenu());

        menuContainer.add(itemDashboard);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemAlertas);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemVentas);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemHistorialVentas);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemProductos);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemInventario);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemMovimientosStock);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemClientes);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemProveedores);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemEmpleados);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemUsuarios);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemReportes);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemConfiguracion);

        configSubMenuPanel = createConfigSubMenuPanel();
        menuContainer.add(configSubMenuPanel);

        menuContainer.add(Box.createVerticalGlue());

        return menuContainer;
    }

    private JPanel createConfigSubMenuPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 18, 0, 0));

        itemTipoDocumentos = createSubMenuItem(FontAwesome.FILE_TEXT, "Tipo de Documentos");
        itemRoles = createSubMenuItem(FontAwesome.USERS, "Roles");
        itemUbicacion = createSubMenuItem(FontAwesome.MAP_MARKER, "Ubicacion");
        itemCatalogo = createSubMenuItem(FontAwesome.TAGS, "Catalogo");
        itemPerfil = createSubMenuItem(FontAwesome.USER, "Perfil");

        itemTipoDocumentos.addClickAction(() -> showSection("TIPO_DOCUMENTOS", itemTipoDocumentos));
        itemRoles.addClickAction(() -> showSection("ROLES", itemRoles));
        itemUbicacion.addClickAction(() -> showSection("UBICACION", itemUbicacion));
        itemCatalogo.addClickAction(() -> showSection("CATALOGO", itemCatalogo));
        itemPerfil.addClickAction(() -> showSection("PERFIL", itemPerfil));

        panel.add(itemTipoDocumentos);
        panel.add(Box.createVerticalStrut(8));
        panel.add(itemRoles);
        panel.add(Box.createVerticalStrut(8));
        panel.add(itemUbicacion);
        panel.add(Box.createVerticalStrut(8));
        panel.add(itemCatalogo);
        panel.add(Box.createVerticalStrut(8));
        panel.add(itemPerfil);

        Dimension zero = new Dimension(SIDEBAR_WIDTH - 24, 0);
        panel.setPreferredSize(zero);
        panel.setMinimumSize(zero);
        panel.setMaximumSize(zero);
        panel.setVisible(false);

        return panel;
    }

    private JPanel createSidebarFooter() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 16, 28, 16));

        JPanel footerContainer = new JPanel();
        footerContainer.setOpaque(false);
        footerContainer.setLayout(new BoxLayout(footerContainer, BoxLayout.Y_AXIS));

        FooterCard footer = new FooterCard();
        footer.setPreferredSize(new Dimension(SIDEBAR_WIDTH - 32, 72));
        footer.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 32, 72));
        footer.setLayout(new BorderLayout(10, 0));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        CircleLogoSmall logo = new CircleLogoSmall();
        logo.setPreferredSize(new Dimension(42, 42));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblVersion = new JLabel("KMLLogistics v1.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVersion.setForeground(Color.WHITE);

        JLabel lblSystem = new JLabel("Sistema de Ventas y Facturacion");
        lblSystem.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSystem.setForeground(mutedText);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(lblVersion);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(lblSystem);
        textPanel.add(Box.createVerticalGlue());

        footer.add(logo, BorderLayout.WEST);
        footer.add(textPanel, BorderLayout.CENTER);

        JButton btnLogout = createLogoutButton();

        footerContainer.add(footer);
        footerContainer.add(Box.createVerticalStrut(12));
        footerContainer.add(btnLogout);

        wrapper.add(footerContainer, BorderLayout.CENTER);

        return wrapper;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("Cerrar sesion", buildIcon(FontAwesome.TIMES, 16, Color.WHITE));
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        button.setPreferredSize(new Dimension(SIDEBAR_WIDTH - 32, 48));
        button.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 32, 48));
        button.setMinimumSize(new Dimension(SIDEBAR_WIDTH - 32, 48));
        button.setBackground(new Color(191, 54, 78));
        button.setForeground(Color.RED);
        button.setOpaque(true);
        button.addActionListener(e -> handleLogout());
        return button;
    }

    private SideMenuItem createMenuItem(FontAwesome icon, String text, boolean hasArrow) {
        SideMenuItem item = new SideMenuItem(
                icon,
                text,
                hasArrow,
                SIDEBAR_WIDTH - 24,
                52,
                activeBlue,
                hoverBlue,
                whiteText,
                mutedText
        );

        menuItems.add(item);
        return item;
    }

    private SideMenuItem createSubMenuItem(FontAwesome icon, String text) {
        SideMenuItem item = new SideMenuItem(
                icon,
                text,
                false,
                SIDEBAR_WIDTH - 50,
                44,
                activeBlue,
                new Color(22, 50, 78),
                whiteText,
                mutedText
        );

        menuItems.add(item);
        return item;
    }

    private JPanel createContentPanel() {
        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        dashboardPanel = new DashboardJPanel(
                getSafeDisplayName(),
                () -> showSection("HISTORIAL_VENTAS", itemHistorialVentas),
                () -> showSection("ALERTAS", itemAlertas)
        );

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(createAlertasPanel(), "ALERTAS");
        contentPanel.add(new SaleJPanel(getSafeUserId(), getSafeUserName()), "VENTAS");
        contentPanel.add(new SaleHistoryJPanel(), "HISTORIAL_VENTAS");
        contentPanel.add(new ProductJPanel(), "PRODUCTOS");
        contentPanel.add(new InventoryJPanel(), "INVENTARIO");
        contentPanel.add(new StockMovementJPanel(), "MOVIMIENTOS_STOCK");
        contentPanel.add(new ClientJPanel(), "CLIENTES");
        contentPanel.add(new SupplierJPanel(), "PROVEEDORES");
        contentPanel.add(new EmployeeJPanel(), "EMPLEADOS");
        contentPanel.add(new UserJPanel(), "USUARIOS");
        contentPanel.add(createSectionPanel("Reportes", "Aqui ira el modulo de reportes."), "REPORTES");
        contentPanel.add(new DocumentTypeJPanel(), "TIPO_DOCUMENTOS");
        contentPanel.add(new RoleJPanel(), "ROLES");
        contentPanel.add(new LocationJPanel(), "UBICACION");
        contentPanel.add(new CatalogJPanel(), "CATALOGO");
        contentPanel.add(new ProfileJPanel(getSafeUserId(), this::handleProfileUpdated), "PERFIL");

        return contentPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 25));
        panel.setBackground(backgroundColor);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);

        JLabel lblTitle = new JLabel("Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblTitle.setForeground(darkText);

        lblDashboardWelcome = new JLabel("Bienvenido, " + getSafeDisplayName());
        lblDashboardWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        lblDashboardWelcome.setForeground(new Color(90, 90, 90));

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblDashboardWelcome, BorderLayout.SOUTH);

        JPanel cards = new JPanel(new GridLayout(2, 3, 22, 22));
        cards.setBackground(backgroundColor);

        cards.add(createInfoCard("Ventas", "Resumen general de ventas."));
        cards.add(createInfoCard("Productos", "Control de productos disponibles."));
        cards.add(createInfoCard("Clientes", "Gestion de clientes registrados."));
        cards.add(createInfoCard("Empleados", "Administracion de empleados."));
        cards.add(createInfoCard("Reportes", "Reportes del sistema."));
        cards.add(createInfoCard("Configuracion", "Opciones generales del sistema."));

        panel.add(header, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlertasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        return panel;
    }

    private JPanel createSectionPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(darkText);

        JLabel lblDescription = new JLabel(description);
        lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        lblDescription.setForeground(new Color(90, 90, 90));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDescription, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 23));
        lblTitle.setForeground(darkText);

        JLabel lblDescription = new JLabel(description);
        lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDescription.setForeground(new Color(90, 90, 90));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDescription, BorderLayout.CENTER);

        return card;
    }

    private void showSection(String sectionName, SideMenuItem activeItem) {
        if (cardLayout == null || contentPanel == null) {
            return;
        }

        cardLayout.show(contentPanel, sectionName);
        refreshVisibleSection();
        setActiveMenuItem(activeItem);
    }

    private void refreshVisibleSection() {
        for (Component component : contentPanel.getComponents()) {
            if (!component.isVisible()) {
                continue;
            }

            if (component instanceof SectionRefreshable refreshable) {
                refreshable.refreshSectionData();
            }

            break;
        }
    }

    private void setActiveMenuItem(SideMenuItem activeItem) {
        for (SideMenuItem item : menuItems) {
            item.setActive(item == activeItem);
        }
    }

    private void toggleConfigMenu() {
        if (configTimer != null && configTimer.isRunning()) {
            configTimer.stop();
        }

        configExpanded = !configExpanded;
        itemConfiguracion.setArrowDown(configExpanded);

        if (configExpanded) {
            configSubMenuPanel.setVisible(true);
            animateConfigSubMenu(configSubMenuPanel.getPreferredSize().height, configFullHeight);
        } else {
            animateConfigSubMenu(configSubMenuPanel.getPreferredSize().height, 0);
        }
    }

    private void animateConfigSubMenu(int startHeight, int endHeight) {
        final int[] currentHeight = {startHeight};
        final int step = endHeight > startHeight ? 8 : -8;

        configTimer = new Timer(8, e -> {
            currentHeight[0] += step;

            boolean finishedOpening = step > 0 && currentHeight[0] >= endHeight;
            boolean finishedClosing = step < 0 && currentHeight[0] <= endHeight;

            if (finishedOpening || finishedClosing) {
                currentHeight[0] = endHeight;
                configTimer.stop();

                if (endHeight == 0) {
                    configSubMenuPanel.setVisible(false);
                }
            }

            Dimension dimension = new Dimension(SIDEBAR_WIDTH - 24, currentHeight[0]);
            configSubMenuPanel.setPreferredSize(dimension);
            configSubMenuPanel.setMinimumSize(dimension);
            configSubMenuPanel.setMaximumSize(dimension);

            menuContainer.revalidate();
            menuContainer.repaint();
        });

        configTimer.start();
    }

    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Se cerrara la sesion actual. Desea continuar?",
                "Cerrar sesion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (currentUser != null && currentUser.getIdUser() > 0) {
                userController.logoutUser(currentUser.getIdUser());
            }

            LoginJFrame loginFrame = new LoginJFrame();
            loginFrame.setVisible(true);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al cerrar sesion",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Image loadProfileImage() {
        if (currentUser != null
                && currentUser.getProfileImagePath() != null
                && !currentUser.getProfileImagePath().trim().isEmpty()) {
            try {
                File imageFile = new File(currentUser.getProfileImagePath().trim());

                if (imageFile.exists() && imageFile.isFile()) {
                    return ImageIO.read(imageFile);
                }
            } catch (Exception e) {
                System.out.println("No se pudo cargar la foto de perfil del usuario.");
            }
        }

        try {
            InputStream inputStream = getClass().getResourceAsStream("/Assets/profile.png");

            if (inputStream != null) {
                return ImageIO.read(inputStream);
            }

        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen de perfil.");
        }

        return null;
    }

    private void handleProfileUpdated(User updatedUser) {
        if (updatedUser == null) {
            return;
        }

        this.currentUser = updatedUser;
        this.userName = getSafeUserName();

        if (lblSidebarUser != null) {
            lblSidebarUser.setText(getSafeDisplayName());
        }

        if (lblSidebarUserName != null) {
            lblSidebarUserName.setText("@" + getSafeUserName());
        }

        if (lblDashboardWelcome != null) {
            lblDashboardWelcome.setText("Bienvenido, " + getSafeDisplayName());
        }

        if (dashboardPanel != null) {
            dashboardPanel.setDisplayName(getSafeDisplayName());
        }

        if (sidebarAvatar != null) {
            sidebarAvatar.setUserName(getSafeDisplayName());
            sidebarAvatar.setProfileImage(loadProfileImage());
        }
    }

    private static Icon buildIcon(FontAwesome icon, int size, Color color) {
        return IconFontSwing.buildIcon(icon, size, color);
    }

    private class GradientSidebarPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            java.awt.GradientPaint gradient = new java.awt.GradientPaint(
                    0,
                    0,
                    sidebarTopColor,
                    0,
                    getHeight(),
                    sidebarBottomColor
            );

            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    }

    private static class LogoLabel extends JLabel {

        public LogoLabel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            Font font = new Font("Segoe UI", Font.BOLD | Font.ITALIC, 28);
            g2.setFont(font);

            FontMetrics fm = g2.getFontMetrics();

            String part1 = "KML";
            String part2 = "Logistics";

            int totalWidth = fm.stringWidth(part1 + part2);
            int x = (getWidth() - totalWidth) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 6;

            g2.setColor(Color.WHITE);
            g2.drawString(part1, x, y);

            g2.setColor(new Color(82, 165, 255));
            g2.drawString(part2, x + fm.stringWidth(part1), y);

            g2.dispose();
        }
    }

    private static class CircularAvatar extends JPanel {

        private String userName;
        private Image profileImage;

        public CircularAvatar(String userName, Image profileImage) {
            this.userName = userName == null || userName.trim().isEmpty()
                    ? "U"
                    : userName.trim();

            this.profileImage = profileImage;
            setOpaque(false);
        }

        public void setUserName(String userName) {
            this.userName = userName == null || userName.trim().isEmpty()
                    ? "U"
                    : userName.trim();
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

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int size = Math.min(getWidth(), getHeight()) - 8;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            Ellipse2D circle = new Ellipse2D.Double(x, y, size, size);

            if (profileImage != null) {
                g2.setClip(circle);
                g2.drawImage(profileImage, x, y, size, size, this);
                g2.setClip(null);
            } else {
                g2.setColor(new Color(34, 139, 255));
                g2.fill(circle);

                String initials = getInitials(userName);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();

                int textWidth = fm.stringWidth(initials);
                int textX = x + (size - textWidth) / 2;
                int textY = y + (size + fm.getAscent()) / 2 - 5;

                g2.setColor(Color.WHITE);
                g2.drawString(initials, textX, textY);
            }

            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.WHITE);
            g2.draw(circle);

            g2.dispose();
        }

        private String getInitials(String name) {
            String[] parts = name.trim().split("\\s+");

            if (parts.length >= 2) {
                return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
            }

            if (name.length() >= 2) {
                return name.substring(0, 2).toUpperCase();
            }

            return name.substring(0, 1).toUpperCase();
        }
    }

    private static class OnlineDot extends JPanel {

        public OnlineDot() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(46, 204, 113));
            g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);

            g2.dispose();
        }
    }

    private static class SideMenuItem extends JPanel {

        private final Color activeColor;
        private final Color hoverColor;
        private final Color textColor;
        private final Color mutedTextColor;

        private final JLabel lblIcon;
        private final JLabel lblText;
        private final JLabel lblArrow;

        private boolean active = false;
        private boolean hover = false;
        private final boolean hasArrow;

        private Runnable clickAction;

        public SideMenuItem(
                FontAwesome icon,
                String text,
                boolean hasArrow,
                int width,
                int height,
                Color activeColor,
                Color hoverColor,
                Color textColor,
                Color mutedTextColor
        ) {
            this.activeColor = activeColor;
            this.hoverColor = hoverColor;
            this.textColor = textColor;
            this.mutedTextColor = mutedTextColor;
            this.hasArrow = hasArrow;

            setOpaque(false);
            setLayout(new BorderLayout(12, 0));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 14));

            lblIcon = new JLabel(buildIcon(icon, 19, Color.WHITE));
            lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
            lblIcon.setPreferredSize(new Dimension(24, height));

            lblText = new JLabel(text);
            lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblText.setForeground(textColor);

            lblArrow = new JLabel(hasArrow ? ">" : "");
            lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblArrow.setForeground(mutedTextColor);
            lblArrow.setHorizontalAlignment(SwingConstants.RIGHT);

            add(lblIcon, BorderLayout.WEST);
            add(lblText, BorderLayout.CENTER);
            add(lblArrow, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (clickAction != null) {
                        clickAction.run();
                    }
                }
            });
        }

        public void addClickAction(Runnable clickAction) {
            this.clickAction = clickAction;
        }

        public void setActive(boolean active) {
            this.active = active;

            if (active) {
                lblText.setForeground(Color.WHITE);
                lblArrow.setForeground(Color.WHITE);
            } else {
                lblText.setForeground(textColor);
                lblArrow.setForeground(mutedTextColor);
            }

            repaint();
        }

        public void setArrowDown(boolean down) {
            if (hasArrow) {
                lblArrow.setText(down ? "v" : ">");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (active) {
                g2.setColor(activeColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else if (hover) {
                g2.setColor(hoverColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class FooterCard extends JPanel {

        public FooterCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(255, 255, 255, 28));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.setColor(new Color(255, 255, 255, 55));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class CircleLogoSmall extends JPanel {

        public CircleLogoSmall() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillOval(x, y, size, size);

            g2.setColor(new Color(255, 255, 255, 160));
            g2.drawOval(x, y, size, size);

            g2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 15));
            String text = "K";

            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (size - fm.stringWidth(text)) / 2;
            int textY = y + (size + fm.getAscent()) / 2 - 4;

            g2.setColor(Color.WHITE);
            g2.drawString(text, textX, textY);

            g2.dispose();
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
            DashboardJFrame dashboard = new DashboardJFrame();
            dashboard.setVisible(true);
        });
    }
}
