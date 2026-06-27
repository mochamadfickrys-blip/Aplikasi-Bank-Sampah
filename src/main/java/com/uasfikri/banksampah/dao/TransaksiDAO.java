package com.uasfikri.banksampah.dao;

import com.uasfikri.banksampah.model.DetailTransaksi;
import com.uasfikri.banksampah.model.Transaksi;
import com.uasfikri.banksampah.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransaksiDAO {

    public List<Transaksi> getAll() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, n.nama AS nasabah_nama FROM transaksi t " +
                "JOIN nasabah n ON t.nasabah_id = n.id " +
                "ORDER BY t.tanggal DESC, t.id DESC;";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaksi t = mapResultSetToTransaksi(rs);
                t.setNasabahNama(rs.getString("nasabah_nama"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Transaksi getById(int id) {
        String sql = "SELECT t.*, n.nama AS nasabah_nama FROM transaksi t " +
                "JOIN nasabah n ON t.nasabah_id = n.id " +
                "WHERE t.id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Transaksi t = mapResultSetToTransaksi(rs);
                    t.setNasabahNama(rs.getString("nasabah_nama"));
                    t.setDetails(getDetailsForTransaksi(conn, id));
                    return t;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<DetailTransaksi> getDetailsForTransaksi(Connection conn, int transaksiId) throws SQLException {
        List<DetailTransaksi> details = new ArrayList<>();
        String sql = "SELECT dt.*, k.nama AS kategori_nama FROM detail_transaksi dt " +
                "JOIN kategori_sampah k ON dt.kategori_id = k.id " +
                "WHERE dt.transaksi_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaksiId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailTransaksi dt = new DetailTransaksi(
                            rs.getInt("id"),
                            rs.getInt("transaksi_id"),
                            rs.getInt("kategori_id"),
                            rs.getDouble("berat"),
                            rs.getDouble("harga_saat_ini"),
                            rs.getDouble("subtotal")
                    );
                    dt.setKategoriNama(rs.getString("kategori_nama"));
                    details.add(dt);
                }
            }
        }
        return details;
    }

    public boolean insertSetor(Transaksi transaksi) {
        Connection conn = null;
        PreparedStatement pstmtTx = null;
        PreparedStatement pstmtDetail = null;
        PreparedStatement pstmtNasabah = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert Transaksi parent
            String sqlTx = "INSERT INTO transaksi (kode_transaksi, nasabah_id, tipe_transaksi, total_nominal, tanggal) VALUES (?, ?, ?, ?, ?);";
            pstmtTx = conn.prepareStatement(sqlTx, Statement.RETURN_GENERATED_KEYS);
            pstmtTx.setString(1, transaksi.getKodeTransaksi());
            pstmtTx.setInt(2, transaksi.getNasabahId());
            pstmtTx.setString(3, "SETOR");
            pstmtTx.setDouble(4, transaksi.getTotalNominal());
            pstmtTx.setString(5, transaksi.getTanggal());
            pstmtTx.executeUpdate();

            // Get generated transaction ID
            int transaksiId = 0;
            try (ResultSet generatedKeys = pstmtTx.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaksiId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }

            // 2. Insert DetailTransaksi children
            String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id, kategori_id, berat, harga_saat_ini, subtotal) VALUES (?, ?, ?, ?, ?);";
            pstmtDetail = conn.prepareStatement(sqlDetail);
            for (DetailTransaksi dt : transaksi.getDetails()) {
                pstmtDetail.setInt(1, transaksiId);
                pstmtDetail.setInt(2, dt.getKategoriId());
                pstmtDetail.setDouble(3, dt.getBerat());
                pstmtDetail.setDouble(4, dt.getHargaSaatIni());
                pstmtDetail.setDouble(5, dt.getSubtotal());
                pstmtDetail.addBatch();
            }
            pstmtDetail.executeBatch();

            // 3. Update Nasabah balance (add)
            String sqlNasabah = "UPDATE nasabah SET saldo = saldo + ? WHERE id = ?;";
            pstmtNasabah = conn.prepareStatement(sqlNasabah);
            pstmtNasabah.setDouble(1, transaksi.getTotalNominal());
            pstmtNasabah.setInt(2, transaksi.getNasabahId());
            pstmtNasabah.executeUpdate();

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            closeQuietly(pstmtTx);
            closeQuietly(pstmtDetail);
            closeQuietly(pstmtNasabah);
            closeQuietly(conn);
        }
    }

    public boolean insertTarik(Transaksi transaksi) {
        Connection conn = null;
        PreparedStatement pstmtTx = null;
        PreparedStatement pstmtNasabah = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Validate balance
            String sqlCheck = "SELECT saldo FROM nasabah WHERE id = ?;";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setInt(1, transaksi.getNasabahId());
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        double currentSaldo = rs.getDouble("saldo");
                        if (currentSaldo < transaksi.getTotalNominal()) {
                            // Insufficient balance
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }

            // 1. Insert Transaksi parent
            String sqlTx = "INSERT INTO transaksi (kode_transaksi, nasabah_id, tipe_transaksi, total_nominal, tanggal) VALUES (?, ?, ?, ?, ?);";
            pstmtTx = conn.prepareStatement(sqlTx);
            pstmtTx.setString(1, transaksi.getKodeTransaksi());
            pstmtTx.setInt(2, transaksi.getNasabahId());
            pstmtTx.setString(3, "TARIK");
            pstmtTx.setDouble(4, transaksi.getTotalNominal());
            pstmtTx.setString(5, transaksi.getTanggal());
            pstmtTx.executeUpdate();

            // 2. Update Nasabah balance (subtract)
            String sqlNasabah = "UPDATE nasabah SET saldo = saldo - ? WHERE id = ?;";
            pstmtNasabah = conn.prepareStatement(sqlNasabah);
            pstmtNasabah.setDouble(1, transaksi.getTotalNominal());
            pstmtNasabah.setInt(2, transaksi.getNasabahId());
            pstmtNasabah.executeUpdate();

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            closeQuietly(pstmtTx);
            closeQuietly(pstmtNasabah);
            closeQuietly(conn);
        }
    }

    public String generateNewKodeTransaksi(String tipe) {
        String prefix = tipe.equals("SETOR") ? "TRX-S-" : "TRX-T-";
        String sql = "SELECT kode_transaksi FROM transaksi WHERE tipe_transaksi = ? ORDER BY id DESC LIMIT 1;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipe);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastKode = rs.getString("kode_transaksi");
                    try {
                        int num = Integer.parseInt(lastKode.substring(6)) + 1;
                        return String.format("%s%04d", prefix, num);
                    } catch (Exception e) {
                        // Fallback
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix + "0001";
    }

    public Map<String, Object> getSummaryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("total_nasabah", 0);
        metrics.put("total_saldo", 0.0);
        metrics.put("total_sampah", 0.0);
        metrics.put("total_transaksi", 0);

        try (Connection conn = DatabaseHelper.getConnection()) {
            // Count Nasabah
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM nasabah;")) {
                if (rs.next()) metrics.put("total_nasabah", rs.getInt(1));
            }
            // Sum Saldo
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SUM(saldo) FROM nasabah;")) {
                if (rs.next()) metrics.put("total_saldo", rs.getDouble(1));
            }
            // Sum Sampah (kg)
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SUM(berat) FROM detail_transaksi;")) {
                if (rs.next()) metrics.put("total_sampah", rs.getDouble(1));
            }
            // Count Transaksi
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM transaksi;")) {
                if (rs.next()) metrics.put("total_transaksi", rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return metrics;
    }

    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private Transaksi mapResultSetToTransaksi(ResultSet rs) throws SQLException {
        return new Transaksi(
                rs.getInt("id"),
                rs.getString("kode_transaksi"),
                rs.getInt("nasabah_id"),
                rs.getString("tipe_transaksi"),
                rs.getDouble("total_nominal"),
                rs.getString("tanggal")
        );
    }
}
