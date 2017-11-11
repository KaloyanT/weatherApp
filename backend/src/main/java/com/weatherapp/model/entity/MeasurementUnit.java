package com.weatherapp.model.entity;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "MeasurementUnit")
@Table(name = "measurementUnit")
public class MeasurementUnit {

    @Id
    @GenericGenerator(name = "measurementUnitIdGenerator", strategy = "increment")
    @GeneratedValue(generator = "measurementUnitIdGenerator")
    @Column(name = "measurementUnitId")
    private long measurementUnitId;

    @Basic
    @Column(name = "measurementUnitName")
    private String measurementUnitName;

    @Basic
    @Column(name = "measurementUnit")
    private String measurementUnit;

    // private Set<String> measurementUnits = new HashSet<String>();

    public long getMeasurementUnitId() {
        return measurementUnitId;
    }

    public void setMeasurementUnitId(long measurementUnitId) {
        this.measurementUnitId = measurementUnitId;
    }

    public String getMeasurementUnitName() {
        return measurementUnitName;
    }

    public void setMeasurementUnitName(String measurementUnitName) {
        this.measurementUnitName = measurementUnitName;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    /**
     * Override the default equals method for consistency
     */
    @Override
    public boolean equals(Object o) {

        if(this == o) {
            return true;
        }
        if(! (o instanceof MeasurementUnit) ) {
            return false;
        }

        return (this.measurementUnitId != 0) &&
                (this.measurementUnitId == ((MeasurementUnit) o).measurementUnitId);
    }

    /**
     * Use this hashCode for consistency
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.measurementUnitId + this.measurementUnitName); // + this.measurementUnits
    }
}
