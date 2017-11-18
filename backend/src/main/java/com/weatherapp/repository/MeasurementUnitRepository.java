package com.weatherapp.repository;

import com.weatherapp.model.entity.MeasurementUnit;
import org.springframework.data.repository.CrudRepository;

public interface MeasurementUnitRepository extends CrudRepository<MeasurementUnit, Long> {

    MeasurementUnit findByMeasurementUnitId(final long measurementUnitId);

    MeasurementUnit findByQuantity(final String quantity);
}
