package com.uasfikri.banksampah.ui;

import com.uasfikri.banksampah.dao.KategoriSampahDAO;
import com.uasfikri.banksampah.model.KategoriSampah;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class KategoriPanel extends JPanel {
    private final KategoriSampahDAO kategoriDAO = new KategoriSampahDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Form fields
    private JTextField txtId; // Hidden ID
    private JTextField txtNama;
    private JTextField txtSatuan;
    private JTextField txtHarga;
    private JTextField txtSearch;

    // Buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnClear;

    public KategoriPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setOpaque(false);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Kelola Kategori & Harga Sampah");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Cari: "));
        txtSearch = new JTextField(18);
        txtSearch.putClientProperty("JTextField.placeholderText", "Ketik nama kategori...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        searchPanel.add(txtSearch);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center split layout: Table on left, Form on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(650);
        splitPane.setBorder(null);

        // Table Panel (Left)
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        String[] columns = {"ID", "Nama Kategori", "Satuan", "Harga per Satuan (Rp)"};
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
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(tableContainer);

        // Form Panel (Right)
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(65, 65, 75), 1, true),
                " Form Kategori Sampah ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
        ));
        formPanel.setBackground(new Color(45, 45, 55));
        formPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setVisible(false);

        // Row 0: Nama Kategori
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNama = new JLabel("Nama Kategori:");
        lblNama.setForeground(Color.WHITE);
        formPanel.add(lblNama, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNama = new JTextField();
        formPanel.add(txtNama, gbc);

        // Row 1: Satuan
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        JLabel lblSatuan = new JLabel("Satuan:");
        lblSatuan.setForeground(Color.WHITE);
        formPanel.add(lblSatuan, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSatuan = new JTextField("kg");
        formPanel.add(txtSatuan, gbc);

        // Row 2: Harga
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        JLabel lblHarga = new JLabel("Harga per Satuan:");
        lblHarga.setForeground(Color.WHITE);
        formPanel.add(lblHarga, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtHarga = new JTextField();
        txtHarga.putClientProperty("JTextField.placeholderText", "Contoh: 2500");
        formPanel.add(txtHarga, gbc);

        // Buttons Panel (Row 3)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(15, 12, 8, 12);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setOpaque(false);

        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Simpan Edit");
        btnDelete = new JButton("Hapus");
        btnClear = new JButton("Reset Form");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        formPanel.add(buttonPanel, gbc);

        formContainer.add(formPanel, BorderLayout.NORTH);
        splitPane.setRightComponent(formContainer);

        add(splitPane, BorderLayout.CENTER);

        // Table Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelection();
            }
        });

        // Button Action Listeners
        btnAdd.addActionListener(e -> addKategori());
        btnEdit.addActionListener(e -> editKategori());
        btnDelete.addActionListener(e -> deleteKategori());
        btnClear.addActionListener(e -> resetForm());

        // Initial setup
        loadData();
        resetForm();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<KategoriSampah> list = kategoriDAO.getAll();
        for (KategoriSampah k : list) {
            tableModel.addRow(new Object[]{
                    k.getId(),
                    k.getNama(),
                    k.getSatuan(),
                    String.format("%,.0f", k.getHargaPerKg())
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

    private void populateFormFromSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = table.convertRowIndexToModel(selectedRow);

            txtId.setText(tableModel.getValueAt(modelRow, 0).toString());
            txtNama.setText(tableModel.getValueAt(modelRow, 1).toString());
            txtSatuan.setText(tableModel.getValueAt(modelRow, 2).toString());
            
            // Strip formatting from price
            String rawHarga = tableModel.getValueAt(modelRow, 3).toString().replace(".", "").replace(",", "");
            txtHarga.setText(rawHarga);

            btnAdd.setEnabled(false);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    private void resetForm() {
        txtId.setText("");
        txtNama.setText("");
        txtSatuan.setText("kg");
        txtHarga.setText("");
        table.clearSelection();

        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void addKategori() {
        String nama = txtNama.getText().trim();
        String satuan = txtSatuan.getText().trim();
        String hargaStr = txtHarga.getText().trim();

        if (nama.isEmpty() || satuan.isEmpty() || hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
            if (harga <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka positif!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KategoriSampah k = new KategoriSampah();
        k.setNama(nama);
        k.setSatuan(satuan);
        k.setHargaPerKg(harga);

        if (kategoriDAO.insert(k)) {
            JOptionPane.showMessageDialog(this, "Kategori sampah baru berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kategori. Pastikan nama belum terdaftar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editKategori() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText().trim();
        String satuan = txtSatuan.getText().trim();
        String hargaStr = txtHarga.getText().trim();

        if (nama.isEmpty() || satuan.isEmpty() || hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
            if (harga <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka positif!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KategoriSampah k = new KategoriSampah();
        k.setId(id);
        k.setNama(nama);
        k.setSatuan(satuan);
        k.setHargaPerKg(harga);

        if (kategoriDAO.update(k)) {
            JOptionPane.showMessageDialog(this, "Kategori sampah berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui kategori.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteKategori() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus kategori '" + nama + "'?\nMenghapus kategori mungkin berdampak pada data transaksi yang merujuk kategori ini.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (kategoriDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kategori.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
