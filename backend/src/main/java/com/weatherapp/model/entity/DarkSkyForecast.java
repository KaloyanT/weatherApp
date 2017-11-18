package com.weatherapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.util.Objects;

@Entity(name = "DarkSkyForecast")
@Table(name = "darkSkyForecast")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DarkSkyForecast {

    @Id
    @GenericGenerator(name = "darkSkyForecastIdGenerator", strategy = "increment")
    @GeneratedValue(generator = "darkSkyForecastIdGenerator")
    @Column(name = "darkSkyForecastId")
    private long darkSkyForecastId;

    @Basic(optional = false)
    @Column(name = "temperatureLow")
    private double temperatureLow;

    @Basic(optional  = false)
    @Column(name = "temperatureHigh")
    private double temperatureHigh;

    @Basic(optional  = false)
    @Column(name = "apparentTemperatureLow")
    private double apparentTemperatureLow;

    @Basic(optional  = false)
    @Column(name = "apparentTemperatureHigh")
    private double apparentTemperatureHigh;

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

    @Basic
    @Column(name = "precipProbability")
    private double precipProbability;

    @Basic
    @Column(name = "precipIntensity")
    private double precipIntensity;

    @Basic
    @Column(name = "precipType")
    private String precipType;

    @Basic(optional = false)
    @Column(name = "summary")
    private String summary;

    @Basic(optional = false)
    @Column(name = "icon")
    private String icon;

    // in UNIX Time
    @Basic
    @Column(name = "timestamp")
    //@NotNull
    //@Temporal(TemporalType.TIMESTAMP)
    //private Date timeStamp;
    private long timeStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_cityId")
    @JsonIgnore
    private City city;


    public long getDarkSkyForecastId() {
        return darkSkyForecastId;
    }

    public void setDarkSkyForecastId(long darkSkyForecastId) {
        this.darkSkyForecastId = darkSkyForecastId;
    }

    public double getTemperatureLow() {
        return temperatureLow;
    }

    public void setTemperatureLow(double temperatureLow) {
        this.temperatureLow = temperatureLow;
    }

    public double getTemperatureHigh() {
        return temperatureHigh;
    }

    public void setTemperatureHigh(double temperatureHigh) {
        this.temperatureHigh = temperatureHigh;
    }

    public double getApparentTemperatureLow() {
        return apparentTemperatureLow;
    }

    public void setApparentTemperatureLow(double apparentTemperatureLow) {
        this.apparentTemperatureLow = apparentTemperatureLow;
    }

    public double getApparentTemperatureHigh() {
        return apparentTemperatureHigh;
    }

    public void setApparentTemperatureHigh(double apparentTemperatureHigh) {
        this.apparentTemperatureHigh = apparentTemperatureHigh;
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

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public String getPrecipType() {
        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
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
        if(! (o instanceof DarkSkyForecast) ) {
            return false;
        }

        return (this.darkSkyForecastId != 0) &&
                (this.darkSkyForecastId == ((DarkSkyForecast) o).darkSkyForecastId);
    }

    /**
     * Use this hashCode for consistency
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.darkSkyForecastId + this.summary + this.timeStamp);
    }
}
