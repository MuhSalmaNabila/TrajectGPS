package com.nabil.radeon.trajectgps.model;

import android.support.v7.widget.FitWindowsFrameLayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AndroidVersion {
    @SerializedName("no")
    @Expose
    private String no;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("profile_id")
    @Expose
    private String profile_id;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("lng")
    @Expose
    private Double lng;

    @SerializedName("lt_awal")
    @Expose
    private Double lt_awal;

    @SerializedName("lg_awal")
    @Expose
    private Double lg_awal;

    @SerializedName("lt_akhir")
    @Expose
    private Double lt_akhir;

    @SerializedName("lg_akhir")
    @Expose
    private Double lg_akhir;

    @SerializedName("kecepatan")
    @Expose
    private Double kecepatan;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("tanggal")
    @Expose
    private String tanggal;





    public String getNo() {return no;}

    public String getId() { return id; }

    public String getProfile_id() { return profile_id; }

    public String getDate() {return date;}

    public Double getLt_awal() {return lt_awal;}

    public Double getLg_awal() {return lg_awal;}

    public Double getLt_akhir() {return lt_akhir;}

    public Double getLg_akhir() {return lg_akhir;}

    public Double getKecepatan() {return kecepatan;}

    public Double getLat() {return lat;}

    public Double getLng() {return lng;}

    public String getName() {return name;}

    public String getEmail() {return email;}

    public String getPhone() {return phone;}

    public String getTanggal() {return tanggal;}

    public void setNo(String no) {this.no = no; }

    public void setId(String id) {
        this.id = id;
    }

    public void setProfile_id(String profile_id) { this.profile_id = profile_id; }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLt_awal(Double lt_awal) {
        this.lt_awal = lt_awal;
    }

    public void setLg_awal(Double lg_awal) {
        this.lg_awal = lg_awal;
    }

    public void setLt_akhir(Double lt_akhir) {
        this.lt_akhir = lt_akhir;
    }

    public void setLg_akhir(Double lg_akhir) {
        this.lg_akhir = lg_akhir;
    }

    public void setKecepatan(Double kecepatan) {
        this.kecepatan = kecepatan;
    }

    public void setLat(Double lat) {this.lat = lat; }

    public void setLng(Double lng) {this.lng = lng; }

    public void setName(String name) {this.name = name; }

    public void setEmail(String email) {this.email = email; }

    public void setPhone(String phone) {this.phone = phone; }

    public void setTanggal(String tanggal) {this.tanggal = tanggal; }





}
