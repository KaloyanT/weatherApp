package com.weatherapp.controller;

import com.weatherapp.util.StringVerifier;
import com.weatherapp.util.WeatherAppDarkSkyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tk.plogitech.darksky.forecast.model.Forecast;

@RestController
@RequestMapping("/darksky")
public class DarkSkyController {

    @Autowired
    private WeatherAppDarkSkyClient client;

    @RequestMapping(value = "/daily/{cityName}/{country}", method = RequestMethod.GET)
    public ResponseEntity<?> getDailyForecastForCity(@PathVariable("cityName") String cityName, @PathVariable("country") String country) {

        if(!StringVerifier.validString(cityName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validString(country)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Forecast forecast = client.getDarkSkyForecast(cityName, country);

        return new ResponseEntity<>(forecast.getDaily().getData().get(0).getTime(), HttpStatus.OK);
    }
}
