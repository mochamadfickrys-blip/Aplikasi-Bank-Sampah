package com.uasfikri.banksampah.model;

public class KategoriSampah {
    private int id;
    private String nama;
    private String satuan;
    private double hargaPerKg;

    public KategoriSampah() {}

    public KategoriSampah(int id, String nama, String satuan, double hargaPerKg) {
        this.id = id;
        this.nama = nama;
        this.satuan = satuan;
        this.hargaPerKg = hargaPerKg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getHargaPerKg() {
        return hargaPerKg;
    }

    public void setHargaPerKg(double hargaPerKg) {
        this.hargaPerKg = hargaPerKg;
    }

    @Override
    public String toString() {
        return nama + " (Rp " + String.format("%,.0f", hargaPerKg) + "/" + satuan + ")";
    }
}
