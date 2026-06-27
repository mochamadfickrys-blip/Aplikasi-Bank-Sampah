package com.uasfikri.banksampah.dao;

import com.uasfikri.banksampah.model.KategoriSampah;
import com.uasfikri.banksampah.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriSampahDAO {

    public List<KategoriSampah> getAll() {
        List<KategoriSampah> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori_sampah ORDER BY nama ASC;";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToKategori(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public KategoriSampah getById(int id) {
        String sql = "SELECT * FROM kategori_sampah WHERE id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKategori(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(KategoriSampah kategori) {
        String sql = "INSERT INTO kategori_sampah (nama, satuan, harga_per_kg) VALUES (?, ?, ?);";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategori.getNama());
            pstmt.setString(2, kategori.getSatuan());
            pstmt.setDouble(3, kategori.getHargaPerKg());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KategoriSampah kategori) {
        String sql = "UPDATE kategori_sampah SET nama = ?, satuan = ?, harga_per_kg = ? WHERE id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategori.getNama());
            pstmt.setString(2, kategori.getSatuan());
            pstmt.setDouble(3, kategori.getHargaPerKg());
            pstmt.setInt(4, kategori.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM kategori_sampah WHERE id = ?;";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private KategoriSampah mapResultSetToKategori(ResultSet rs) throws SQLException {
        return new KategoriSampah(
                rs.getInt("id"),
                rs.getString("nama"),
                rs.getString("satuan"),
                rs.getDouble("harga_per_kg")
        );
    }
}
