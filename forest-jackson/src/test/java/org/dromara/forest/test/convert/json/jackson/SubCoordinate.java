package org.dromara.forest.test.convert.json.jackson;

public class SubCoordinate extends ParentCoordinate {

    private String latitude;

    public SubCoordinate(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
