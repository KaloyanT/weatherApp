package com.weatherapp.repository;

import com.weatherapp.model.entity.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City, Long> {

    City findByCityId(final long cityId);

    City findByNameAndCountry(final String cityName, final String countryName);

    City findByLatitudeAndLongitude(final double latitude, final double longtitude);
}
