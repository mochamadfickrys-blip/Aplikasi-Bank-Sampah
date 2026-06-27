package com.uasfikri.banksampah.model;

public class Nasabah {
    private int id;
    private String kodeNasabah;
    private String nama;
    private String alamat;
    private String nomorTelepon;
    private double saldo;
    private String tanggalDaftar;

    public Nasabah() {}

    public Nasabah(int id, String kodeNasabah, String nama, String alamat, String nomorTelepon, double saldo, String tanggalDaftar) {
        this.id = id;
        this.kodeNasabah = kodeNasabah;
        this.nama = nama;
        this.alamat = alamat;
        this.nomorTelepon = nomorTelepon;
        this.saldo = saldo;
        this.tanggalDaftar = tanggalDaftar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeNasabah() {
        return kodeNasabah;
    }

    public void setKodeNasabah(String kodeNasabah) {
        this.kodeNasabah = kodeNasabah;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(String tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    @Override
    public String toString() {
        return kodeNasabah + " - " + nama;
    }
}
