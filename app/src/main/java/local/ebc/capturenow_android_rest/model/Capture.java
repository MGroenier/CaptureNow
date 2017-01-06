package local.ebc.capturenow_android_rest.model;

import com.google.gson.annotations.Expose;

/**
 * Capture model for recyclerview.
 *  @author Emil Claussen on 15.12.2016.
 */

public class Capture  {

    @Expose private int id;
    @Expose private String title;
    @Expose private Double longitude;
    @Expose private Double latitude;
    @Expose private byte[] imgcapture;

    public Capture(String title, Double longitude, Double latitude, byte[] imgcapture, String description) {
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.imgcapture = imgcapture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public byte[] getImgcapture() {
        return imgcapture;
    }

    public void setImgcapture(byte[] imgcapture) {
        this.imgcapture = imgcapture;
    }

   }
