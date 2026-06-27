package com.uasfikri.banksampah.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banksampah?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        // Step 1: Connect to MySQL server root and create database if not exists
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS banksampah;");
            System.out.println("Database 'banksampah' checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to check/create database 'banksampah' on MySQL server! Make sure Laragon MySQL is running.");
            e.printStackTrace();
            return;
        }

        // Step 2: Connect to the created database and build tables
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Kategori Sampah Table (MySQL Syntax)
            stmt.execute("CREATE TABLE IF NOT EXISTS kategori_sampah (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nama VARCHAR(100) NOT NULL UNIQUE," +
                    "satuan VARCHAR(20) NOT NULL DEFAULT 'kg'," +
                    "harga_per_kg DOUBLE NOT NULL" +
                    ") ENGINE=InnoDB;");

            // Create Nasabah Table
            stmt.execute("CREATE TABLE IF NOT EXISTS nasabah (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "kode_nasabah VARCHAR(50) NOT NULL UNIQUE," +
                    "nama VARCHAR(150) NOT NULL," +
                    "alamat TEXT," +
                    "nomor_telepon VARCHAR(20)," +
                    "saldo DOUBLE NOT NULL DEFAULT 0.0," +
                    "tanggal_daftar DATETIME NOT NULL" +
                    ") ENGINE=InnoDB;");

            // Create Transaksi Table
            stmt.execute("CREATE TABLE IF NOT EXISTS transaksi (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "kode_transaksi VARCHAR(50) NOT NULL UNIQUE," +
                    "nasabah_id INT NOT NULL," +
                    "tipe_transaksi VARCHAR(10) NOT NULL," +
                    "total_nominal DOUBLE NOT NULL," +
                    "tanggal DATETIME NOT NULL," +
                    "FOREIGN KEY (nasabah_id) REFERENCES nasabah(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB;");

            // Create Detail Transaksi Table
            stmt.execute("CREATE TABLE IF NOT EXISTS detail_transaksi (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "transaksi_id INT NOT NULL," +
                    "kategori_id INT NOT NULL," +
                    "berat DOUBLE NOT NULL," +
                    "harga_saat_ini DOUBLE NOT NULL," +
                    "subtotal DOUBLE NOT NULL," +
                    "FOREIGN KEY (transaksi_id) REFERENCES transaksi(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (kategori_id) REFERENCES kategori_sampah(id)" +
                    ") ENGINE=InnoDB;");

            // Seed initial data if tables are empty
            seedDefaultCategories(stmt);
            seedDefaultNasabah(stmt);

            System.out.println("MySQL database tables checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing tables in MySQL database.");
            e.printStackTrace();
        }
    }

    private static void seedDefaultCategories(Statement stmt) throws SQLException {
        java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM kategori_sampah;");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.execute("INSERT INTO kategori_sampah (nama, satuan, harga_per_kg) VALUES " +
                    "('Plastik Botol', 'kg', 2500.0)," +
                    "('Kertas HVS/Buku', 'kg', 1500.0)," +
                    "('Kardus Bekas', 'kg', 2000.0)," +
                    "('Besi & Logam', 'kg', 4500.0)," +
                    "('Kaca & Botol Kaca', 'kg', 1000.0)," +
                    "('Aluminium/Kaleng', 'kg', 6000.0);");
            System.out.println("Default waste categories seeded to MySQL.");
        }
    }

    private static void seedDefaultNasabah(Statement stmt) throws SQLException {
        java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM nasabah;");
        if (rs.next() && rs.getInt(1) == 0) {
            String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            stmt.execute("INSERT INTO nasabah (kode_nasabah, nama, alamat, nomor_telepon, saldo, tanggal_daftar) VALUES " +
                    "('NSB-001', 'Budi Santoso', 'Jl. Merdeka No. 12', '081234567890', 25000.0, '" + dateStr + "')," +
                    "('NSB-002', 'Siti Aminah', 'Jl. Mawar No. 5', '085678901234', 12500.0, '" + dateStr + "')," +
                    "('NSB-003', 'Joko Susilo', 'Jl. Pemuda No. 45', '081987654321', 0.0, '" + dateStr + "');");
            System.out.println("Default members (nasabah) seeded to MySQL.");
        }
    }
}
