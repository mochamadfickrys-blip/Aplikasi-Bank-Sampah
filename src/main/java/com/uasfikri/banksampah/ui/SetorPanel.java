package com.uasfikri.banksampah.ui;

import com.uasfikri.banksampah.dao.KategoriSampahDAO;
import com.uasfikri.banksampah.dao.NasabahDAO;
import com.uasfikri.banksampah.dao.TransaksiDAO;
import com.uasfikri.banksampah.model.DetailTransaksi;
import com.uasfikri.banksampah.model.KategoriSampah;
import com.uasfikri.banksampah.model.Nasabah;
import com.uasfikri.banksampah.model.Transaksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SetorPanel extends JPanel {
    private final NasabahDAO nasabahDAO = new NasabahDAO();
    private final KategoriSampahDAO kategoriDAO = new KategoriSampahDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private JComboBox<Nasabah> cbNasabah;
    private JComboBox<KategoriSampah> cbKategori;
    
    private JTextField txtKodeTrx;
    private JTextField txtTanggal;
    private JTextField txtNasabahSaldo;
    private JTextField txtBerat;
    private JTextField txtHargaSatuan;
    private JLabel lblTotalTrx;

    private JTable tableItems;
    private DefaultTableModel tableModel;
    
    private List<DetailTransaksi> currentDetails = new ArrayList<>();
    private double totalNominal = 0.0;

    public SetorPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setOpaque(false);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Transaksi Setor Sampah");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel: Split into Left (Inputs) and Right (Table of added items)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(400);
        splitPane.setBorder(null);

        // Left Container (Forms)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        // Subpanel 1: Meta Transaksi & Nasabah Selection
        JPanel metaPanel = new JPanel(new GridBagLayout());
        metaPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(65, 65, 75), 1, true),
                " Informasi Transaksi ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13),
                Color.WHITE
        ));
        metaPanel.setBackground(new Color(45, 45, 55));
        metaPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Kode TRX
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblTrx = new JLabel("Kode Transaksi:");
        lblTrx.setForeground(Color.WHITE);
        metaPanel.add(lblTrx, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtKodeTrx = new JTextField();
        txtKodeTrx.setEditable(false);
        txtKodeTrx.setBackground(new Color(60, 60, 70));
        txtKodeTrx.setForeground(new Color(200, 200, 200));
        metaPanel.add(txtKodeTrx, gbc);

        // Row 1: Tanggal
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        JLabel lblTgl = new JLabel("Tanggal:");
        lblTgl.setForeground(Color.WHITE);
        metaPanel.add(lblTgl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTanggal = new JTextField();
        txtTanggal.setEditable(false);
        txtTanggal.setBackground(new Color(60, 60, 70));
        txtTanggal.setForeground(new Color(200, 200, 200));
        metaPanel.add(txtTanggal, gbc);

        // Row 2: Pilih Nasabah
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        JLabel lblNsb = new JLabel("Nasabah:");
        lblNsb.setForeground(Color.WHITE);
        metaPanel.add(lblNsb, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel nsbSelectorPanel = new JPanel(new BorderLayout(5, 0));
        nsbSelectorPanel.setOpaque(false);
        cbNasabah = new JComboBox<>();
        cbNasabah.addActionListener(e -> updateNasabahSaldo());
        nsbSelectorPanel.add(cbNasabah, BorderLayout.CENTER);
        JButton btnRefreshNsb = new JButton("↻");
        btnRefreshNsb.setToolTipText("Refresh daftar nasabah");
        btnRefreshNsb.addActionListener(e -> loadNasabahList());
        nsbSelectorPanel.add(btnRefreshNsb, BorderLayout.EAST);
        metaPanel.add(nsbSelectorPanel, gbc);

        // Row 3: Saldo Sekarang
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        JLabel lblSaldo = new JLabel("Saldo Saat Ini:");
        lblSaldo.setForeground(Color.WHITE);
        metaPanel.add(lblSaldo, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNasabahSaldo = new JTextField();
        txtNasabahSaldo.setEditable(false);
        txtNasabahSaldo.setBackground(new Color(60, 60, 70));
        txtNasabahSaldo.setForeground(new Color(200, 200, 200));
        metaPanel.add(txtNasabahSaldo, gbc);

        leftPanel.add(metaPanel, BorderLayout.NORTH);

        // Subpanel 2: Input Item Sampah
        JPanel inputItemPanel = new JPanel(new GridBagLayout());
        inputItemPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(65, 65, 75), 1, true),
                " Tambah Item Sampah ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13),
                Color.WHITE
        ));
        inputItemPanel.setBackground(new Color(45, 45, 55));
        inputItemPanel.setOpaque(true);

        // Row 0: Kategori
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        JLabel lblKat = new JLabel("Kategori Sampah:");
        lblKat.setForeground(Color.WHITE);
        inputItemPanel.add(lblKat, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel katSelectorPanel = new JPanel(new BorderLayout(5, 0));
        katSelectorPanel.setOpaque(false);
        cbKategori = new JComboBox<>();
        cbKategori.addActionListener(e -> updateHargaSatuan());
        katSelectorPanel.add(cbKategori, BorderLayout.CENTER);
        JButton btnRefreshKat = new JButton("↻");
        btnRefreshKat.setToolTipText("Refresh kategori");
        btnRefreshKat.addActionListener(e -> loadKategoriList());
        katSelectorPanel.add(btnRefreshKat, BorderLayout.EAST);
        inputItemPanel.add(katSelectorPanel, gbc);

        // Row 1: Harga Satuan
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        JLabel lblHarga = new JLabel("Harga per Satuan:");
        lblHarga.setForeground(Color.WHITE);
        inputItemPanel.add(lblHarga, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtHargaSatuan = new JTextField();
        txtHargaSatuan.setEditable(false);
        txtHargaSatuan.setBackground(new Color(60, 60, 70));
        txtHargaSatuan.setForeground(new Color(200, 200, 200));
        inputItemPanel.add(txtHargaSatuan, gbc);

        // Row 2: Berat (kg)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        JLabel lblBerat = new JLabel("Berat / Jumlah:");
        lblBerat.setForeground(Color.WHITE);
        inputItemPanel.add(lblBerat, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtBerat = new JTextField();
        txtBerat.putClientProperty("JTextField.placeholderText", "Contoh: 2.5");
        inputItemPanel.add(txtBerat, gbc);

        // Row 3: Tambah Item Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(10, 10, 10, 10);
        JButton btnAddItem = new JButton(" Tambah Item ke Tabel ");
        btnAddItem.addActionListener(e -> addItemToTable());
        inputItemPanel.add(btnAddItem, gbc);

        leftPanel.add(inputItemPanel, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Right Container (Table & Final Actions)
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setOpaque(false);

        // Item Table
        String[] columns = {"Kategori", "Harga per kg (Rp)", "Berat (kg)", "Subtotal (Rp)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableItems = new JTable(tableModel);
        tableItems.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(tableItems);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions Panel
        JPanel actionsContainer = new JPanel(new BorderLayout(10, 10));
        actionsContainer.setOpaque(false);

        // Left of bottom actions: Delete selected item from table
        JButton btnDeleteItem = new JButton("Hapus Item Terpilih");
        btnDeleteItem.addActionListener(e -> deleteSelectedItem());
        actionsContainer.add(btnDeleteItem, BorderLayout.WEST);

        // Right of bottom actions: Total display & Simpan button
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        
        JLabel lblTotalTitle = new JLabel("Total Nominal: ");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalTitle.setForeground(Color.WHITE);
        
        lblTotalTrx = new JLabel("Rp 0");
        lblTotalTrx.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalTrx.setForeground(new Color(46, 204, 113));
        
        JButton btnSimpan = new JButton(" Simpan Transaksi ");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimpan.setBackground(new Color(39, 174, 96));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpanTransaksi());

        summaryPanel.add(lblTotalTitle);
        summaryPanel.add(lblTotalTrx);
        summaryPanel.add(btnSimpan);
        
        actionsContainer.add(summaryPanel, BorderLayout.EAST);
        rightPanel.add(actionsContainer, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Initial setup data
        loadNasabahList();
        loadKategoriList();
        resetForm();
    }

    public void initPanel() {
        // Public callback to trigger recalculating next code and date when panel is selected
        loadNasabahList();
        loadKategoriList();
        resetForm();
    }

    private void loadNasabahList() {
        cbNasabah.removeAllItems();
        List<Nasabah> list = nasabahDAO.getAll();
        for (Nasabah n : list) {
            cbNasabah.addItem(n);
        }
        updateNasabahSaldo();
    }

    private void loadKategoriList() {
        cbKategori.removeAllItems();
        List<KategoriSampah> list = kategoriDAO.getAll();
        for (KategoriSampah k : list) {
            cbKategori.addItem(k);
        }
        updateHargaSatuan();
    }

    private void updateNasabahSaldo() {
        Nasabah n = (Nasabah) cbNasabah.getSelectedItem();
        if (n != null) {
            txtNasabahSaldo.setText("Rp " + String.format("%,.2f", n.getSaldo()));
        } else {
            txtNasabahSaldo.setText("Rp 0");
        }
    }

    private void updateHargaSatuan() {
        KategoriSampah k = (KategoriSampah) cbKategori.getSelectedItem();
        if (k != null) {
            txtHargaSatuan.setText("Rp " + String.format("%,.0f", k.getHargaPerKg()) + " / " + k.getSatuan());
        } else {
            txtHargaSatuan.setText("Rp 0");
        }
    }

    private void resetForm() {
        txtKodeTrx.setText(transaksiDAO.generateNewKodeTransaksi("SETOR"));
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        txtBerat.setText("");
        
        tableModel.setRowCount(0);
        currentDetails.clear();
        totalNominal = 0.0;
        lblTotalTrx.setText("Rp 0");
    }

    private void addItemToTable() {
        KategoriSampah k = (KategoriSampah) cbKategori.getSelectedItem();
        if (k == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih kategori sampah terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String beratStr = txtBerat.getText().trim();
        if (beratStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Berat tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double berat;
        try {
            berat = Double.parseDouble(beratStr);
            if (berat <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Berat harus berupa angka positif!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotal = k.getHargaPerKg() * berat;

        // Check if item of same category is already in list, if so, merge them
        boolean exists = false;
        for (int i = 0; i < currentDetails.size(); i++) {
            DetailTransaksi dt = currentDetails.get(i);
            if (dt.getKategoriId() == k.getId()) {
                dt.setBerat(dt.getBerat() + berat);
                dt.setSubtotal(dt.getSubtotal() + subtotal);
                
                // Update table row
                tableModel.setValueAt(String.format("%.2f", dt.getBerat()), i, 2);
                tableModel.setValueAt(String.format("%,.0f", dt.getSubtotal()), i, 3);
                exists = true;
                break;
            }
        }

        if (!exists) {
            DetailTransaksi dt = new DetailTransaksi();
            dt.setKategoriId(k.getId());
            dt.setKategoriNama(k.getNama());
            dt.setBerat(berat);
            dt.setHargaSaatIni(k.getHargaPerKg());
            dt.setSubtotal(subtotal);
            currentDetails.add(dt);

            tableModel.addRow(new Object[]{
                    k.getNama(),
                    String.format("%,.0f", k.getHargaPerKg()),
                    String.format("%.2f", berat),
                    String.format("%,.0f", subtotal)
            });
        }

        calculateTotal();
        txtBerat.setText("");
    }

    private void deleteSelectedItem() {
        int selectedRow = tableItems.getSelectedRow();
        if (selectedRow != -1) {
            currentDetails.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            calculateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris item di tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void calculateTotal() {
        totalNominal = 0.0;
        for (DetailTransaksi dt : currentDetails) {
            totalNominal += dt.getSubtotal();
        }
        lblTotalTrx.setText("Rp " + String.format("%,.0f", totalNominal));
    }

    private void simpanTransaksi() {
        Nasabah n = (Nasabah) cbNasabah.getSelectedItem();
        if (n == null) {
            JOptionPane.showMessageDialog(this, "Nasabah belum dipilih!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tabel setoran kosong! Silakan tambahkan item sampah terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Transaksi tx = new Transaksi();
        tx.setKodeTransaksi(txtKodeTrx.getText());
        tx.setNasabahId(n.getId());
        tx.setTipeTransaksi("SETOR");
        tx.setTotalNominal(totalNominal);
        tx.setTanggal(txtTanggal.getText());
        tx.setDetails(currentDetails);

        if (transaksiDAO.insertSetor(tx)) {
            JOptionPane.showMessageDialog(this, 
                    "Transaksi Setoran Berhasil!\n" +
                    "Kode: " + tx.getKodeTransaksi() + "\n" +
                    "Nasabah: " + n.getNama() + "\n" +
                    "Total Saldo Bertambah: Rp " + String.format("%,.0f", totalNominal), 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload list and reset
            loadNasabahList(); // to update saldo view
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi. Terjadi kesalahan pada database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
