package com.uasfikri.banksampah.model;

public class DetailTransaksi {
    private int id;
    private int transaksiId;
    private int kategoriId;
    private String kategoriNama; // Helper field for UI display
    private double berat;
    private double hargaSaatIni;
    private double subtotal;

    public DetailTransaksi() {}

    public DetailTransaksi(int id, int transaksiId, int kategoriId, double berat, double hargaSaatIni, double subtotal) {
        this.id = id;
        this.transaksiId = transaksiId;
        this.kategoriId = kategoriId;
        this.berat = berat;
        this.hargaSaatIni = hargaSaatIni;
        this.subtotal = subtotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransaksiId() {
        return transaksiId;
    }

    public void setTransaksiId(int transaksiId) {
        this.transaksiId = transaksiId;
    }

    public int getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    public String getKategoriNama() {
        return kategoriNama;
    }

    public void setKategoriNama(String kategoriNama) {
        this.kategoriNama = kategoriNama;
    }

    public double getBerat() {
        return berat;
    }

    public void setBerat(double berat) {
        this.berat = berat;
    }

    public double getHargaSaatIni() {
        return hargaSaatIni;
    }

    public void setHargaSaatIni(double hargaSaatIni) {
        this.hargaSaatIni = hargaSaatIni;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
