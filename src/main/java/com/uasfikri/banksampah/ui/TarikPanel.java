package com.uasfikri.banksampah.ui;

import com.uasfikri.banksampah.dao.NasabahDAO;
import com.uasfikri.banksampah.dao.TransaksiDAO;
import com.uasfikri.banksampah.model.Nasabah;
import com.uasfikri.banksampah.model.Transaksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TarikPanel extends JPanel {
    private final NasabahDAO nasabahDAO = new NasabahDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private JComboBox<Nasabah> cbNasabah;
    private JTextField txtKodeTrx;
    private JTextField txtTanggal;
    private JTextField txtNamaNasabah;
    private JTextField txtSaldoSekarang;
    private JTextField txtNominalTarik;

    public TarikPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setOpaque(false);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Transaksi Penarikan Saldo (Tarik Uang)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel centered with rounded borders
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(65, 65, 75), 1, true),
                " Formulir Penarikan Saldo ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
        ));
        formPanel.setBackground(new Color(45, 45, 55));
        formPanel.setOpaque(true);
        formPanel.setPreferredSize(new Dimension(500, 380));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Kode Trx
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        JLabel lblKode = new JLabel("Kode Transaksi:");
        lblKode.setForeground(Color.WHITE);
        formPanel.add(lblKode, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtKodeTrx = new JTextField();
        txtKodeTrx.setEditable(false);
        txtKodeTrx.setBackground(new Color(60, 60, 70));
        txtKodeTrx.setForeground(new Color(200, 200, 200));
        formPanel.add(txtKodeTrx, gbc);

        // Row 1: Tanggal
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        JLabel lblTgl = new JLabel("Tanggal:");
        lblTgl.setForeground(Color.WHITE);
        formPanel.add(lblTgl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTanggal = new JTextField();
        txtTanggal.setEditable(false);
        txtTanggal.setBackground(new Color(60, 60, 70));
        txtTanggal.setForeground(new Color(200, 200, 200));
        formPanel.add(txtTanggal, gbc);

        // Row 2: Pilih Nasabah
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        JLabel lblNsb = new JLabel("Pilih Nasabah:");
        lblNsb.setForeground(Color.WHITE);
        formPanel.add(lblNsb, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel nsbPanel = new JPanel(new BorderLayout(5, 0));
        nsbPanel.setOpaque(false);
        cbNasabah = new JComboBox<>();
        cbNasabah.addActionListener(e -> updateNasabahDetail());
        nsbPanel.add(cbNasabah, BorderLayout.CENTER);
        
        JButton btnRefreshNsb = new JButton("↻");
        btnRefreshNsb.setToolTipText("Refresh nasabah");
        btnRefreshNsb.addActionListener(e -> loadNasabahList());
        nsbPanel.add(btnRefreshNsb, BorderLayout.EAST);
        
        formPanel.add(nsbPanel, gbc);

        // Row 3: Nama Lengkap (Read Only display)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        JLabel lblNama = new JLabel("Nama Lengkap:");
        lblNama.setForeground(Color.WHITE);
        formPanel.add(lblNama, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNamaNasabah = new JTextField();
        txtNamaNasabah.setEditable(false);
        txtNamaNasabah.setBackground(new Color(60, 60, 70));
        txtNamaNasabah.setForeground(new Color(200, 200, 200));
        formPanel.add(txtNamaNasabah, gbc);

        // Row 4: Saldo Sekarang
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0;
        JLabel lblSaldo = new JLabel("Saldo Tersedia:");
        lblSaldo.setForeground(Color.WHITE);
        formPanel.add(lblSaldo, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSaldoSekarang = new JTextField();
        txtSaldoSekarang.setEditable(false);
        txtSaldoSekarang.setBackground(new Color(60, 60, 70));
        txtSaldoSekarang.setForeground(new Color(46, 204, 113));
        txtSaldoSekarang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(txtSaldoSekarang, gbc);

        // Row 5: Nominal Tarik
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0;
        JLabel lblTarik = new JLabel("Jumlah Penarikan (Rp):");
        lblTarik.setForeground(Color.WHITE);
        formPanel.add(lblTarik, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNominalTarik = new JTextField();
        txtNominalTarik.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtNominalTarik.putClientProperty("JTextField.placeholderText", "Ketik nominal penarikan...");
        formPanel.add(txtNominalTarik, gbc);

        // Row 6: Submit Button
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.insets = new Insets(15, 15, 10, 15);
        JButton btnTarik = new JButton(" PROSES PENARIKAN ");
        btnTarik.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTarik.setBackground(new Color(192, 57, 43));
        btnTarik.setForeground(Color.WHITE);
        btnTarik.addActionListener(e -> prosesPenarikan());
        formPanel.add(btnTarik, gbc);

        mainContainer.add(formPanel);
        add(mainContainer, BorderLayout.CENTER);

        // Load data initially
        loadNasabahList();
        resetForm();
    }

    public void initPanel() {
        loadNasabahList();
        resetForm();
    }

    private void loadNasabahList() {
        cbNasabah.removeAllItems();
        List<Nasabah> list = nasabahDAO.getAll();
        for (Nasabah n : list) {
            cbNasabah.addItem(n);
        }
        updateNasabahDetail();
    }

    private void updateNasabahDetail() {
        Nasabah n = (Nasabah) cbNasabah.getSelectedItem();
        if (n != null) {
            txtNamaNasabah.setText(n.getNama());
            txtSaldoSekarang.setText("Rp " + String.format("%,.2f", n.getSaldo()));
        } else {
            txtNamaNasabah.setText("");
            txtSaldoSekarang.setText("Rp 0");
        }
    }

    private void resetForm() {
        txtKodeTrx.setText(transaksiDAO.generateNewKodeTransaksi("TARIK"));
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        txtNominalTarik.setText("");
    }

    private void prosesPenarikan() {
        Nasabah n = (Nasabah) cbNasabah.getSelectedItem();
        if (n == null) {
            JOptionPane.showMessageDialog(this, "Nasabah belum dipilih!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nominalStr = txtNominalTarik.getText().trim();
        if (nominalStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nominal penarikan tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double nominal;
        try {
            nominal = Double.parseDouble(nominalStr);
            if (nominal <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal penarikan harus berupa angka positif!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate balance in frontend
        if (n.getSaldo() < nominal) {
            JOptionPane.showMessageDialog(this, 
                    "Penarikan GAGAL!\nSaldo nasabah saat ini (Rp " + String.format("%,.0f", n.getSaldo()) + ") " +
                    "tidak mencukupi untuk melakukan penarikan sebesar Rp " + String.format("%,.0f", nominal),
                    "Saldo Kurang", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Transaksi tx = new Transaksi();
        tx.setKodeTransaksi(txtKodeTrx.getText());
        tx.setNasabahId(n.getId());
        tx.setTipeTransaksi("TARIK");
        tx.setTotalNominal(nominal);
        tx.setTanggal(txtTanggal.getText());

        if (transaksiDAO.insertTarik(tx)) {
            JOptionPane.showMessageDialog(this,
                    "Penarikan Saldo Berhasil!\n" +
                    "Kode Transaksi: " + tx.getKodeTransaksi() + "\n" +
                    "Nasabah: " + n.getNama() + "\n" +
                    "Jumlah Ditarik: Rp " + String.format("%,.0f", nominal) + "\n" +
                    "Sisa Saldo: Rp " + String.format("%,.0f", (n.getSaldo() - nominal)),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            loadNasabahList(); // To refresh the selected item's balance
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan database saat memproses penarikan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
