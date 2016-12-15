package local.ebc.capturenow_android_rest.model;

/**
 * Capture model for recyclerview.
 *  ebc 151216
 */

public class Capture  {
    private int id;
    private String title;
    private Double longitude;
    private Double latitude;
    private byte[] imgcapture;
    private String description;

    public Capture(String title, Double longitude, Double latitude, byte[] imgcapture, String description) {
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.imgcapture = imgcapture;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
