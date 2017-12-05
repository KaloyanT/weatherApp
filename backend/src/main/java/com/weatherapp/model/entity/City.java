package com.weatherapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    // private TimeZone timeZone;
    @Basic(optional = false)
    @Column(name = "timezone")
    @NotNull
    private String timezone;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<DarkSkyForecast> darkSkyForecasts = new HashSet<DarkSkyForecast>();

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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Set<DarkSkyForecast> getDarkSkyForecasts() {
        return darkSkyForecasts;
    }

    public void setDarkSkyForecasts(Set<DarkSkyForecast> darkSkyForecasts) {
        this.darkSkyForecasts = darkSkyForecasts;
    }

    public void addForecast(DarkSkyForecast darkSkyForecast) {
        darkSkyForecasts.add(darkSkyForecast);
        darkSkyForecast.setCity(this);
    }

    public void removeForecast(DarkSkyForecast darkSkyForecast) {
        darkSkyForecasts.remove(darkSkyForecast);
        darkSkyForecast.setCity(null);
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
