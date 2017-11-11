package com.weatherapp.model.entity;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity(name = "City")
@Table(name = "city")
public class City {

    @Id
    @GenericGenerator(name = "cityIdGenerator", strategy = "increment")
    @GeneratedValue(generator = "cityIdGenerator")
    @Column(name = "cityId")
    private long cityId;

    @Basic(optional = false)
    @Column(name = "latitude")
    private double latitude;

    @Basic(optional = false)
    @Column(name = "longitude")
    private double longitude;

    @Basic(optional = false)
    @Column(name = "name")
    @NotNull
    private String name;

    @Basic(optional = false)
    @Column(name = "country")
    @NotNull
    private String country;

    @Basic
    @Column(name = "openWeatherId", unique = true)
    private long openWeatherId;

    @Basic
    @Column(name = "darkSkyId", unique = true)
    private long darkSkyId;

    // private TimeZone timeZone;

    // @OneToMany
    // private Set<Forecast> forecasts = new HashSet<Forecast>();

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getOpenWeatherId() {
        return openWeatherId;
    }

    public void setOpenWeatherId(long openWeatherId) {
        this.openWeatherId = openWeatherId;
    }

    public long getDarkSkyId() {
        return darkSkyId;
    }

    public void setDarkSkyId(long darkSkyId) {
        this.darkSkyId = darkSkyId;
    }

    /**
     * Override the default equals method for consistency
     */
    @Override
    public boolean equals(Object o) {

        if(this == o) {
            return true;
        }
        if(! (o instanceof City) ) {
            return false;
        }

        return (this.cityId != 0) &&
                (this.cityId == ((City) o).cityId);
    }

    /**
     * Use this hashCode for consistency
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.name + this.country + this.latitude + this.longitude);
    }
}
