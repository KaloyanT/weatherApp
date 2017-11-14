package com.weatherapp.model.entity;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity(name = "Forecast")
@Table(name = "forecast")
public class Forecast {

    @Id
    @GenericGenerator(name = "forecastIdGenerator", strategy = "increment")
    @GeneratedValue(generator = "forecastIdGenerator")
    @Column(name = "forecastId")
    private long forecastId;

    @Basic(optional = false)
    @Column(name = "temperature")
    private double temperature;

    @Basic
    @Column(name = "minTemperature")
    private double minTemperature;

    @Basic
    @Column(name = "maxTemperature")
    private double maxTemperature;

    @Basic
    @Column(name = "humidity")
    private double humidity;

    @Basic
    @Column(name = "dewPoint")
    private double dewPoint;

    @Basic
    @Column(name = "pressure")
    private double pressure;

    @Basic
    @Column(name = "windSpeed")
    private double windSpeed;

    @Basic
    @Column(name = "cloudCover")
    private double cloudCover;

    @Basic
    @Column(name = "visibility")
    private double visibility;

    @Basic(optional = false)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @Column(name = "icon")
    private String icon;

    // in UNIX Time
    @Basic
    @Column(name = "timestamp")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_cityId")
    private City city;


    public long getForecastId() {
        return forecastId;
    }

    public void setForecastId(long forecastId) {
        this.forecastId = forecastId;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    /**
     * Override the default equals method for consistency
     */
    @Override
    public boolean equals(Object o) {

        if(this == o) {
            return true;
        }
        if(! (o instanceof Forecast) ) {
            return false;
        }

        return (this.forecastId != 0) &&
                (this.forecastId == ((Forecast) o).forecastId);
    }

    /**
     * Use this hashCode for consistency
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.forecastId + this.description); // + this.timeStamp
    }
}
