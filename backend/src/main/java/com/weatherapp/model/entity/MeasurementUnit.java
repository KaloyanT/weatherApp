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
    @Column(name = "quantity")
    private String quantity;

    // For simplicity save only 1 measurement unit for a given quantity
    @Basic
    @Column(name = "measurementUnit")
    private String measurementUnit;

    // Potentially save the measurement units in a Set
    // private Set<String> measurementUnits = new HashSet<String>();

    public long getMeasurementUnitId() {
        return measurementUnitId;
    }

    public void setMeasurementUnitId(long measurementUnitId) {
        this.measurementUnitId = measurementUnitId;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
        return Objects.hash(this.measurementUnitId + this.measurementUnit); // + this.measurementUnits
    }

    @Override
    public String toString() {
        return this.measurementUnit;
    }
}
