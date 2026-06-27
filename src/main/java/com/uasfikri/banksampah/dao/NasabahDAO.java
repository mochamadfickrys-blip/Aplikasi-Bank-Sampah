package com.uasfikri.banksampah.dao;

import com.uasfikri.banksampah.model.Nasabah;
import com.uasfikri.banksampah.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NasabahDAO {

    public List<Nasabah> getAll() {
        List<Nasabah> list = new ArrayList<>();
        String sql = "SELECT * FROM nasabah ORDER BY kode_nasabah ASC;";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToNasabah(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Nasabah getById(int id) {
        String sql = "SELECT * FROM nasabah WHERE id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNasabah(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Nasabah getByKode(String kode) {
        String sql = "SELECT * FROM nasabah WHERE kode_nasabah = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNasabah(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Nasabah nasabah) {
        String sql = "INSERT INTO nasabah (kode_nasabah, nama, alamat, nomor_telepon, saldo, tanggal_daftar) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nasabah.getKodeNasabah());
            pstmt.setString(2, nasabah.getNama());
            pstmt.setString(3, nasabah.getAlamat());
            pstmt.setString(4, nasabah.getNomorTelepon());
            pstmt.setDouble(5, nasabah.getSaldo());
            pstmt.setString(6, nasabah.getTanggalDaftar());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Nasabah nasabah) {
        String sql = "UPDATE nasabah SET nama = ?, alamat = ?, nomor_telepon = ? WHERE id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nasabah.getNama());
            pstmt.setString(2, nasabah.getAlamat());
            pstmt.setString(3, nasabah.getNomorTelepon());
            pstmt.setInt(4, nasabah.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSaldo(int id, double nominal) {
        String sql = "UPDATE nasabah SET saldo = saldo + ? WHERE id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, nominal);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM nasabah WHERE id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateNewKodeNasabah() {
        String sql = "SELECT kode_nasabah FROM nasabah ORDER BY id DESC LIMIT 1;";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastKode = rs.getString("kode_nasabah");
                try {
                    int num = Integer.parseInt(lastKode.substring(4)) + 1;
                    return String.format("NSB-%03d", num);
                } catch (Exception e) {
                    // Fallback if parsing fails
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NSB-001";
    }

    private Nasabah mapResultSetToNasabah(ResultSet rs) throws SQLException {
        return new Nasabah(
                rs.getInt("id"),
                rs.getString("kode_nasabah"),
                rs.getString("nama"),
                rs.getString("alamat"),
                rs.getString("nomor_telepon"),
                rs.getDouble("saldo"),
                rs.getString("tanggal_daftar")
        );
    }
}
