package br.rafaelsantana.model;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;

public class IPStack {

    @SerializedName("client_id")
    public final String clientId;
    @SerializedName("time_stamp")
    public Long timeStamp;
    @SerializedName("ip")
    public final String ip;
    @SerializedName("latitude")
    public Float latitude;
    @SerializedName("longitude")
    public Float longitude;
    @SerializedName("country_name")
    public String country;
    @SerializedName("region_name")
    public String region;
    @SerializedName("city")
    public String city;

    public IPStack(
            String clientId,
            Long timeStamp,
            String ip,
            Float latitude,
            Float longitude,
            String country,
            String region,
            String city
    ) {
        this.clientId = clientId;
        this.timeStamp = timeStamp;
        this.ip = ip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.region = region;
        this.city = city;
    }

    public IPStack(
            String clientId,
            Long timeStamp,
            String ip
    ) {
       this(clientId, timeStamp, ip, null, null, null, null, null);
    }

    public void completeWithApiResponse(IPStack apiResponse) {
        this.latitude = apiResponse.latitude;
        this.longitude = apiResponse.longitude;
        this.country = apiResponse.country;
        this.region = apiResponse.region;
        this.city = apiResponse.city;
        this.timeStamp = Instant.now().getEpochSecond();
    }

    @Override
    public String toString() {
        return "IPStack{" +
                "clientId='" + clientId + '\'' +
                ", timeStamp=" + timeStamp +
                ", ip='" + ip + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
