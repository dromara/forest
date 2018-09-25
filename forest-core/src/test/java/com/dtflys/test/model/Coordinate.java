package com.dtflys.test.model;

import java.math.BigDecimal;

/**
 * @author gongjun
 * @since  2016-06-01
 */
public class Coordinate {

    private BigDecimal longitude;

    private BigDecimal latitude;

    public Coordinate(BigDecimal longitude, BigDecimal latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Coordinate(String longitude, String latitude) {
        this.longitude = new BigDecimal(longitude);
        this.latitude = new BigDecimal(latitude);
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}
