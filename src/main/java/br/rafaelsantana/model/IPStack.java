package br.rafaelsantana.model;

import com.google.gson.annotations.SerializedName;

public class IPStack {

    @SerializedName("ip")
    public final String ip;
    @SerializedName("latitude")
    public final Float latitude;
    @SerializedName("longitude")
    public final Float longitude;
    @SerializedName("country_name")
    public final String country;
    @SerializedName("region_name")
    public final String region;
    @SerializedName("city")
    public final String city;

    public IPStack(String ip,
                   Float latitude,
                   Float longitude,
                   String country,
                   String region,
                   String city) {
        this.ip = ip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.region = region;
        this.city = city;
    }

    @Override
    public String toString() {
        return "IPStack{" +
                "ip='" + ip + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
