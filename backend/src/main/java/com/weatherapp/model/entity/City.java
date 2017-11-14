package com.weatherapp.model.entity;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

    @Basic
    @Column(name = "openWeatherMapId", unique = true)
    private long openWeatherMapId;

    // private TimeZone timeZone;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Forecast> forecasts = new HashSet<Forecast>();

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

    public long getOpenWeatherMapId() {
        return openWeatherMapId;
    }

    public void setOpenWeatherMapId(long openWeatherMapId) {
        this.openWeatherMapId = openWeatherMapId;
    }

    public Set<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(Set<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public void addForecast(Forecast forecast) {
        forecasts.add(forecast);
        forecast.setCity(this);
    }

    public void removeForecast(Forecast forecast) {
        forecasts.remove(forecast);
        forecast.setCity(null);
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
