package com.uasfikri.banksampah.ui;

import com.uasfikri.banksampah.dao.TransaksiDAO;
import com.uasfikri.banksampah.model.DetailTransaksi;
import com.uasfikri.banksampah.model.Transaksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class RiwayatPanel extends JPanel {
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtSearch;

    public RiwayatPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setOpaque(false);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Riwayat & Laporan Transaksi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Cari: "));
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Ketik kode, nama, tipe...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        searchPanel.add(txtSearch);
        
        JButton btnRefresh = new JButton("↻ Refresh");
        btnRefresh.addActionListener(e -> loadData());
        searchPanel.add(btnRefresh);

        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Kode Transaksi", "Tipe", "Nama Nasabah", "Total Nominal (Rp)", "Tanggal"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide ID Column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions - Detail Dialog Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        JButton btnDetail = new JButton(" Lihat Detail Transaksi ");
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDetail.addActionListener(e -> showDetailDialog());
        bottomPanel.add(btnDetail);

        add(bottomPanel, BorderLayout.SOUTH);

        loadData();
    }

    public void initPanel() {
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Transaksi> list = transaksiDAO.getAll();
        for (Transaksi t : list) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getKodeTransaksi(),
                    t.getTipeTransaksi(),
                    t.getNasabahNama(),
                    String.format("%,.0f", t.getTotalNominal()),
                    t.getTanggal()
            });
        }
    }

    private void filterTable() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void showDetailDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih transaksi dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int txId = (int) tableModel.getValueAt(modelRow, 0);

        Transaksi tx = transaksiDAO.getById(txId);
        if (tx == null) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create Custom Dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Transaksi: " + tx.getKodeTransaksi(), true);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // Header Info Panel
        JPanel headerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        headerPanel.setBorder(new EmptyBorder(15, 20, 5, 20));
        headerPanel.setBackground(new Color(45, 45, 55));

        JLabel lblKode = new JLabel("Kode Transaksi : " + tx.getKodeTransaksi());
        lblKode.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblKode.setForeground(Color.WHITE);

        JLabel lblTipe = new JLabel("Jenis Transaksi   : " + tx.getTipeTransaksi() + " SAMPAH");
        lblTipe.setForeground(Color.WHITE);

        JLabel lblNasabah = new JLabel("Nama Nasabah  : " + tx.getNasabahNama());
        lblNasabah.setForeground(Color.WHITE);

        JLabel lblTgl = new JLabel("Tanggal              : " + tx.getTanggal());
        lblTgl.setForeground(Color.WHITE);

        headerPanel.add(lblKode);
        headerPanel.add(lblTipe);
        headerPanel.add(lblNasabah);
        headerPanel.add(lblTgl);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Details)
        if (tx.getTipeTransaksi().equals("SETOR")) {
            // Table for SETOR details
            String[] detailColumns = {"Kategori Sampah", "Harga per kg", "Berat (kg)", "Subtotal"};
            DefaultTableModel detailModel = new DefaultTableModel(detailColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (DetailTransaksi dt : tx.getDetails()) {
                detailModel.addRow(new Object[]{
                        dt.getKategoriNama(),
                        "Rp " + String.format("%,.0f", dt.getHargaSaatIni()),
                        String.format("%.2f", dt.getBerat()),
                        "Rp " + String.format("%,.0f", dt.getSubtotal())
                });
            }

            JTable detailTable = new JTable(detailModel);
            detailTable.setRowHeight(22);
            JScrollPane detailScroll = new JScrollPane(detailTable);
            detailScroll.setBorder(new EmptyBorder(0, 20, 0, 20));
            dialog.add(detailScroll, BorderLayout.CENTER);
        } else {
            // Receipt description for TARIK
            JPanel withdrawPanel = new JPanel(new GridBagLayout());
            withdrawPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            withdrawPanel.setBackground(new Color(35, 35, 45));

            JLabel lblTarikDesc = new JLabel("<html><body style='text-align: center; width: 300px;'>" +
                    "<h2>Penarikan Tunai Saldo</h2>" +
                    "<p style='font-size: 13px;'>Nasabah telah menarik dana tabungan sampah sebesar:</p>" +
                    "<h1 style='color: #e74c3c;'>Rp " + String.format("%,.0f", tx.getTotalNominal()) + "</h1>" +
                    "<p style='font-size: 11px; color: #95a5a6;'>Dana diserahkan secara tunai oleh petugas Bank Sampah.</p>" +
                    "</body></html>");
            lblTarikDesc.setForeground(Color.WHITE);
            withdrawPanel.add(lblTarikDesc);
            dialog.add(withdrawPanel, BorderLayout.CENTER);
        }

        // Bottom Total Panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JLabel lblTotal = new JLabel("TOTAL NOMINAL: Rp " + String.format("%,.0f", tx.getTotalNominal()));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setForeground(tx.getTipeTransaksi().equals("SETOR") ? new Color(46, 204, 113) : new Color(231, 76, 60));
        footerPanel.add(lblTotal, BorderLayout.WEST);

        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener(e -> dialog.dispose());
        footerPanel.add(btnClose, BorderLayout.EAST);

        dialog.add(footerPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
