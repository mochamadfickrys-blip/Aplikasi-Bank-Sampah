package com.uasfikri.banksampah.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel containerPanel;
    
    // Sidebar items
    private List<MenuItem> menuItems = new ArrayList<>();
    private MenuItem activeItem;

    // Sub-panels
    private DashboardPanel dashboardPanel;
    private NasabahPanel nasabahPanel;
    private KategoriPanel kategoriPanel;
    private SetorPanel setorPanel;
    private TarikPanel tarikPanel;
    private RiwayatPanel riwayatPanel;

    public MainFrame() {
        setTitle("Bank Sampah - Aplikasi Pengelolaan & Tabungan Sampah");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Main Layout: Sidebar on Left (WEST), Content on Right (CENTER)
        setLayout(new BorderLayout());

        // Create Sidebar Panel
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Create Content Panel with CardLayout
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        containerPanel.setBackground(new Color(30, 30, 35));

        // Initialize sub-panels
        dashboardPanel = new DashboardPanel();
        nasabahPanel = new NasabahPanel();
        kategoriPanel = new KategoriPanel();
        setorPanel = new SetorPanel();
        tarikPanel = new TarikPanel();
        riwayatPanel = new RiwayatPanel();

        // Add to CardLayout container
        containerPanel.add(dashboardPanel, "DASHBOARD");
        containerPanel.add(nasabahPanel, "NASABAH");
        containerPanel.add(setorPanel, "SETOR");
        containerPanel.add(tarikPanel, "TARIK");
        containerPanel.add(kategoriPanel, "KATEGORI");
        containerPanel.add(riwayatPanel, "RIWAYAT");

        add(containerPanel, BorderLayout.CENTER);

        // Set initial active menu item
        if (!menuItems.isEmpty()) {
            selectMenuItem(menuItems.get(0));
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(25, 25, 30));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(50, 50, 60)));

        // Top Brand Panel
        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel brandTitle = new JLabel("♻️ BANK SAMPAH");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandTitle.setForeground(new Color(46, 204, 113)); // Vibrant Emerald Green

        JLabel brandSubtitle = new JLabel("Sistem Informasi Mandiri");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brandSubtitle.setForeground(new Color(150, 150, 160));

        brandPanel.add(brandTitle);
        brandPanel.add(brandSubtitle);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Center Menu items List
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add menu buttons
        addMenuItem(menuPanel, "📊  Dashboard", "DASHBOARD");
        addMenuItem(menuPanel, "👥  Data Nasabah", "NASABAH");
        addMenuItem(menuPanel, "♻️  Setor Sampah", "SETOR");
        addMenuItem(menuPanel, "💸  Tarik Saldo", "TARIK");
        addMenuItem(menuPanel, "🏷️  Kategori Sampah", "KATEGORI");
        addMenuItem(menuPanel, "📜  Riwayat Transaksi", "RIWAYAT");

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Footer copyright info
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblFooter = new JLabel("© 2026 UAS Fikri");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(100, 100, 110));
        footerPanel.add(lblFooter);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addMenuItem(JPanel container, String text, String cardName) {
        MenuItem item = new MenuItem(text, cardName);
        menuItems.add(item);
        container.add(item);
        container.add(Box.createRigidArea(new Dimension(0, 8))); // vertical spacing
    }

    private void selectMenuItem(MenuItem item) {
        if (activeItem != null) {
            activeItem.setActive(false);
        }
        activeItem = item;
        activeItem.setActive(true);

        // Switch panel in CardLayout
        cardLayout.show(containerPanel, item.getCardName());

        // Refresh panels on load to keep data synced
        switch (item.getCardName()) {
            case "DASHBOARD":
                dashboardPanel.refreshData();
                break;
            case "SETOR":
                setorPanel.initPanel();
                break;
            case "TARIK":
                tarikPanel.initPanel();
                break;
            case "RIWAYAT":
                riwayatPanel.initPanel();
                break;
            case "NASABAH":
                // Optional: we can reload nasabah panel data if needed, but it has internal auto reload
                break;
            case "KATEGORI":
                break;
        }
    }

    // Custom class for Sidebar Menu Item buttons
    private class MenuItem extends JPanel {
        private final String text;
        private final String cardName;
        private final JLabel label;
        private boolean isActive = false;

        private final Color idleBg = new Color(25, 25, 30);
        private final Color hoverBg = new Color(40, 40, 48);
        private final Color activeBg = new Color(46, 204, 113, 30); // 30 is alpha for subtle translucent green
        private final Color activeBorderColor = new Color(46, 204, 113);

        public MenuItem(String text, String cardName) {
            this.text = text;
            this.cardName = cardName;
            
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(220, 42));
            setMaximumSize(new Dimension(220, 42));
            setBackground(idleBg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(new Color(200, 200, 210));
            label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) {
                        setBackground(hoverBg);
                        label.setForeground(Color.WHITE);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isActive) {
                        setBackground(idleBg);
                        label.setForeground(new Color(200, 200, 210));
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectMenuItem(MenuItem.this);
                }
            });
        }

        public String getCardName() {
            return cardName;
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setBackground(activeBg);
                label.setForeground(Color.WHITE);
                setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, activeBorderColor));
            } else {
                setBackground(idleBg);
                label.setForeground(new Color(200, 200, 210));
                setBorder(null);
            }
            repaint();
        }
    }
}
