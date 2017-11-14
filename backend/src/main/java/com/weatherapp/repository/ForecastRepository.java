package com.weatherapp.repository;

import com.weatherapp.model.entity.City;
import com.weatherapp.model.entity.Forecast;
import org.springframework.data.repository.CrudRepository;
import java.util.Set;

public interface ForecastRepository extends CrudRepository<Forecast, Long> {

    Set<Forecast> getForecastByCity(final City city);
}
