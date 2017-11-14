package com.weatherapp.util;

import com.weatherapp.model.entity.City;
import com.weatherapp.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient;
import tk.plogitech.darksky.forecast.APIKey;
import tk.plogitech.darksky.forecast.ForecastException;
import tk.plogitech.darksky.forecast.ForecastRequest;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder;
import tk.plogitech.darksky.forecast.GeoCoordinates;
import tk.plogitech.darksky.forecast.Latitude;
import tk.plogitech.darksky.forecast.Longitude;
import tk.plogitech.darksky.forecast.model.Forecast;

@Component
public class WeatherAppDarkSkyClient {

    @Value("${weatherapp.darksky.api.key}")
    private String DARKSKY_API_KEY;

    @Autowired
    private CityRepository cityRepository;

    public Forecast getDarkSkyForecast(String cityName, String country) {

        City city = cityRepository.findByNameAndCountry(cityName, country);

        if(city == null) {
            return null;
        }

        ForecastRequest request = new ForecastRequestBuilder()
                .key(new APIKey(DARKSKY_API_KEY))
                .units(ForecastRequestBuilder.Units.si)
                .language(ForecastRequestBuilder.Language.en)
                .location(new GeoCoordinates(new Longitude(city.getLongitude()), new Latitude(city.getLatitude())))
                .build();

        DarkSkyJacksonClient client = new DarkSkyJacksonClient();

        Forecast forecast = null;
        try {
            forecast = client.forecast(request);
        } catch (ForecastException e) {
            e.printStackTrace();
        }

        return forecast;
    }
}
