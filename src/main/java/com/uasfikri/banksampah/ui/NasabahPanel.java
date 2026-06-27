package com.uasfikri.banksampah.ui;

import com.uasfikri.banksampah.dao.NasabahDAO;
import com.uasfikri.banksampah.model.Nasabah;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NasabahPanel extends JPanel {
    private final NasabahDAO nasabahDAO = new NasabahDAO();
    
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    // Form fields
    private JTextField txtId; // Hidden / read-only ID
    private JTextField txtKode;
    private JTextField txtNama;
    private JTextField txtAlamat;
    private JTextField txtTelepon;
    private JTextField txtSaldo;
    private JTextField txtSearch;
    
    // Buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnClear;

    public NasabahPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setOpaque(false);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Kelola Nasabah Bank Sampah");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Cari: "));
        txtSearch = new JTextField(18);
        txtSearch.putClientProperty("JTextField.placeholderText", "Ketik nama atau kode...");
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
        
        String[] columns = {"ID", "Kode Nasabah", "Nama", "Alamat", "No. Telepon", "Saldo (Rp)", "Tgl Daftar"};
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
        
        // Hide ID Column from view (it's column index 0)
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
                " Form Data Nasabah ", 
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

        // ID (Hidden text field, but we can store it)
        txtId = new JTextField();
        txtId.setVisible(false);

        // Row 0: Kode
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblKode = new JLabel("Kode Nasabah:");
        lblKode.setForeground(Color.WHITE);
        formPanel.add(lblKode, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtKode = new JTextField();
        txtKode.setEditable(false);
        txtKode.setBackground(new Color(60, 60, 70));
        txtKode.setForeground(new Color(200, 200, 200));
        formPanel.add(txtKode, gbc);

        // Row 1: Nama
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        JLabel lblNama = new JLabel("Nama Lengkap:");
        lblNama.setForeground(Color.WHITE);
        formPanel.add(lblNama, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNama = new JTextField();
        formPanel.add(txtNama, gbc);

        // Row 2: Alamat
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setForeground(Color.WHITE);
        formPanel.add(lblAlamat, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtAlamat = new JTextField();
        formPanel.add(txtAlamat, gbc);

        // Row 3: Telepon
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        JLabel lblTelepon = new JLabel("No. Telepon:");
        lblTelepon.setForeground(Color.WHITE);
        formPanel.add(lblTelepon, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTelepon = new JTextField();
        formPanel.add(txtTelepon, gbc);

        // Row 4: Saldo
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0;
        JLabel lblSaldo = new JLabel("Saldo (Rp):");
        lblSaldo.setForeground(Color.WHITE);
        formPanel.add(lblSaldo, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSaldo = new JTextField("0");
        txtSaldo.setEditable(false);
        txtSaldo.setBackground(new Color(60, 60, 70));
        txtSaldo.setForeground(new Color(200, 200, 200));
        formPanel.add(txtSaldo, gbc);

        // Buttons Panel (Row 5)
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.insets = new Insets(15, 12, 8, 12);
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
        btnAdd.addActionListener(e -> addNasabah());
        btnEdit.addActionListener(e -> editNasabah());
        btnDelete.addActionListener(e -> deleteNasabah());
        btnClear.addActionListener(e -> resetForm());

        // Initial setup
        loadData();
        resetForm();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Nasabah> list = nasabahDAO.getAll();
        for (Nasabah n : list) {
            tableModel.addRow(new Object[]{
                    n.getId(),
                    n.getKodeNasabah(),
                    n.getNama(),
                    n.getAlamat(),
                    n.getNomorTelepon(),
                    String.format("%,.2f", n.getSaldo()),
                    n.getTanggalDaftar()
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
            // Adjust row index to account for sorting/filtering
            int modelRow = table.convertRowIndexToModel(selectedRow);
            
            txtId.setText(tableModel.getValueAt(modelRow, 0).toString());
            txtKode.setText(tableModel.getValueAt(modelRow, 1).toString());
            txtNama.setText(tableModel.getValueAt(modelRow, 2).toString());
            txtAlamat.setText(tableModel.getValueAt(modelRow, 3).toString());
            txtTelepon.setText(tableModel.getValueAt(modelRow, 4).toString());
            txtSaldo.setText(tableModel.getValueAt(modelRow, 5).toString());
            
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    private void resetForm() {
        txtId.setText("");
        txtKode.setText(nasabahDAO.generateNewKodeNasabah());
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        txtSaldo.setText("0");
        table.clearSelection();
        
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void addNasabah() {
        String nama = txtNama.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String telepon = txtTelepon.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Nasabah n = new Nasabah();
        n.setKodeNasabah(txtKode.getText());
        n.setNama(nama);
        n.setAlamat(alamat);
        n.setNomorTelepon(telepon);
        n.setSaldo(0.0);
        n.setTanggalDaftar(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        if (nasabahDAO.insert(n)) {
            JOptionPane.showMessageDialog(this, "Berhasil menambahkan nasabah baru!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan nasabah. Silakan coba lagi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editNasabah() {
        if (txtId.getText().isEmpty()) return;
        
        int id = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String telepon = txtTelepon.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Nasabah n = new Nasabah();
        n.setId(id);
        n.setNama(nama);
        n.setAlamat(alamat);
        n.setNomorTelepon(telepon);

        if (nasabahDAO.update(n)) {
            JOptionPane.showMessageDialog(this, "Data nasabah berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data nasabah.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNasabah() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus nasabah '" + nama + "'?\nSemua riwayat transaksi nasabah ini juga akan terhapus.", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (nasabahDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Nasabah berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus nasabah.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
