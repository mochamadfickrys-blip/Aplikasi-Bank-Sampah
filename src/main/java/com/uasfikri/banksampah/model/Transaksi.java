package com.uasfikri.banksampah.model;

import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private int id;
    private String kodeTransaksi;
    private int nasabahId;
    private String nasabahNama; // Helper field for UI display
    private String tipeTransaksi; // 'SETOR' or 'TARIK'
    private double totalNominal;
    private String tanggal;
    private List<DetailTransaksi> details = new ArrayList<>();

    public Transaksi() {}

    public Transaksi(int id, String kodeTransaksi, int nasabahId, String tipeTransaksi, double totalNominal, String tanggal) {
        this.id = id;
        this.kodeTransaksi = kodeTransaksi;
        this.nasabahId = nasabahId;
        this.tipeTransaksi = tipeTransaksi;
        this.totalNominal = totalNominal;
        this.tanggal = tanggal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeTransaksi() {
        return kodeTransaksi;
    }

    public void setKodeTransaksi(String kodeTransaksi) {
        this.kodeTransaksi = kodeTransaksi;
    }

    public int getNasabahId() {
        return nasabahId;
    }

    public void setNasabahId(int nasabahId) {
        this.nasabahId = nasabahId;
    }

    public String getNasabahNama() {
        return nasabahNama;
    }

    public void setNasabahNama(String nasabahNama) {
        this.nasabahNama = nasabahNama;
    }

    public String getTipeTransaksi() {
        return tipeTransaksi;
    }

    public void setTipeTransaksi(String tipeTransaksi) {
        this.tipeTransaksi = tipeTransaksi;
    }

    public double getTotalNominal() {
        return totalNominal;
    }

    public void setTotalNominal(double totalNominal) {
        this.totalNominal = totalNominal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public List<DetailTransaksi> getDetails() {
        return details;
    }

    public void setDetails(List<DetailTransaksi> details) {
        this.details = details;
    }

    public void addDetail(DetailTransaksi detail) {
        this.details.add(detail);
    }
}
