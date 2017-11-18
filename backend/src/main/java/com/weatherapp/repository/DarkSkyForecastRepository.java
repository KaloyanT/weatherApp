package com.weatherapp.repository;

import com.weatherapp.model.entity.City;
import com.weatherapp.model.entity.DarkSkyForecast;
import org.springframework.data.repository.CrudRepository;
import java.util.Set;

public interface DarkSkyForecastRepository extends CrudRepository<DarkSkyForecast, Long> {

    DarkSkyForecast getByDarkSkyForecastId(final long darkSkyForecastId);

    DarkSkyForecast getByCityAndTimeStamp(final City city, final long timeStamp);

    Set<DarkSkyForecast> getAllByCity(final City city);

    Set<DarkSkyForecast> getByTimeStamp(final long timeStamp);

    Set<DarkSkyForecast> getAllByTimeStampBetween(final long start, final long end);

    Set<DarkSkyForecast> getAllByCityAndTimeStampBetween(final City city, final long start, final long end);
}
