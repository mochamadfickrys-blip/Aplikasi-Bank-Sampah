package com.uasfikri.banksampah.ui;

import com.uasfikri.banksampah.dao.TransaksiDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    
    private CardPanel cardNasabah;
    private CardPanel cardSaldo;
    private CardPanel cardSampah;
    private CardPanel cardTransaksi;
    
    private JTextArea infoArea;

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setOpaque(false);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Dashboard Bank Sampah");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Ikhtisar data dan statistik operasional Bank Sampah");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(180, 180, 190));
        
        JPanel titleTextPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        titleTextPanel.setOpaque(false);
        titleTextPanel.add(titleLabel);
        titleTextPanel.add(subtitleLabel);
        headerPanel.add(titleTextPanel, BorderLayout.WEST);

        // Refresh Button
        JButton btnRefresh = new JButton("↻ Refresh Data");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshData());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Cards Grid (2x2)
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setOpaque(false);

        cardNasabah = new CardPanel("TOTAL NASABAH", "0 Warga", "👥", new Color(41, 128, 185), new Color(109, 213, 250));
        cardSaldo = new CardPanel("TOTAL SALDO BEREDAR", "Rp 0", "💰", new Color(39, 174, 96), new Color(86, 204, 242));
        cardSampah = new CardPanel("TOTAL SAMPAH TERKUMPUL", "0 kg", "♻️", new Color(142, 68, 173), new Color(224, 86, 253));
        cardTransaksi = new CardPanel("TOTAL TRANSAKSI", "0 Transaksi", "📝", new Color(211, 84, 0), new Color(243, 156, 18));

        gridPanel.add(cardNasabah);
        gridPanel.add(cardSaldo);
        gridPanel.add(cardSampah);
        gridPanel.add(cardTransaksi);

        // Quick Guide / Tips Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        
        JLabel infoTitle = new JLabel("Informasi & Panduan Operasional");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoTitle.setForeground(Color.WHITE);
        infoTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoArea.setForeground(new Color(210, 210, 215));
        infoArea.setBackground(new Color(45, 45, 55));
        infoArea.setMargin(new java.awt.Insets(15, 15, 15, 15));
        infoArea.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 75), 1, true));
        infoArea.setText(
                "💡 TIPS OPERASIONAL HARI INI:\n" +
                "1. Pastikan harga kategori sampah selalu diperbarui sesuai harga pasar agar nasabah tetap tertarik.\n" +
                "2. Transaksi setor sampah secara otomatis menambahkan saldo nasabah berdasarkan berat dan jenis sampah.\n" +
                "3. Transaksi penarikan uang hanya dapat diproses apabila saldo nasabah mencukupi.\n" +
                "4. Gunakan fitur Ekspor/Laporan di menu Riwayat Transaksi jika diperlukan untuk pembukuan bulanan."
        );
        
        infoPanel.add(infoTitle, BorderLayout.NORTH);
        infoPanel.add(infoArea, BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout(20, 20));
        centerContainer.setOpaque(false);
        centerContainer.add(gridPanel, BorderLayout.CENTER);
        centerContainer.add(infoPanel, BorderLayout.SOUTH);

        add(centerContainer, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        Map<String, Object> metrics = transaksiDAO.getSummaryMetrics();
        
        int totalNasabah = (int) metrics.get("total_nasabah");
        double totalSaldo = (double) metrics.get("total_saldo");
        double totalSampah = (double) metrics.get("total_sampah");
        int totalTransaksi = (int) metrics.get("total_transaksi");

        cardNasabah.setValue(totalNasabah + " Warga");
        cardSaldo.setValue("Rp " + String.format("%,.0f", totalSaldo));
        cardSampah.setValue(String.format("%.2f kg", totalSampah));
        cardTransaksi.setValue(totalTransaksi + " Transaksi");
    }

    // Custom Subclass for Premium Cards
    private static class CardPanel extends JPanel {
        private final String title;
        private final JLabel valueLabel;
        private final String icon;
        private final Color startColor;
        private final Color endColor;

        public CardPanel(String title, String initialValue, String icon, Color startColor, Color endColor) {
            this.title = title;
            this.icon = icon;
            this.startColor = startColor;
            this.endColor = endColor;
            
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setPreferredSize(new Dimension(220, 120));

            // Content Panel to place title and value vertically
            JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            textPanel.setOpaque(false);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(new Color(255, 255, 255, 200));
            
            valueLabel = new JLabel(initialValue);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            valueLabel.setForeground(Color.WHITE);

            textPanel.add(titleLabel);
            textPanel.add(valueLabel);

            // Icon Panel
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Black", Font.PLAIN, 40));
            iconLabel.setForeground(new Color(255, 255, 255, 90));
            iconLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

            add(textPanel, BorderLayout.CENTER);
            add(iconLabel, BorderLayout.EAST);
        }

        public void setValue(String value) {
            valueLabel.setText(value);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw Gradient rounded background
            GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            
            g2.dispose();
        }
    }
}
