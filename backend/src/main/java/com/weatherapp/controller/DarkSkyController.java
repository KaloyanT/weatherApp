package com.weatherapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weatherapp.model.entity.City;
import com.weatherapp.model.entity.DarkSkyForecast;
import com.weatherapp.model.entity.MeasurementUnit;
import com.weatherapp.repository.CityRepository;
import com.weatherapp.repository.MeasurementUnitRepository;
import com.weatherapp.util.CustomErrorType;
import com.weatherapp.util.DBClient;
import com.weatherapp.util.StringVerifier;
import com.weatherapp.util.WeatherAppDarkSkyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;;
import tk.plogitech.darksky.forecast.model.DailyDataPoint;
import tk.plogitech.darksky.forecast.model.Forecast;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/darksky")
public class DarkSkyController {

    @Autowired
    private WeatherAppDarkSkyClient wadsClient;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Autowired
    private DBClient dbClient;

    // Like with the WeatherAppDarkskyClient, load the measurement units in memory on start up
    // in order to minimize database access
    private static Iterable<MeasurementUnit> measurementUnits;

    @PostConstruct
    private void init() {
        measurementUnits = measurementUnitRepository.findAll();
    }


    // hasRole requires the roles to be saved in the form ROLE_userRole, i.e. with a ROLE_ prefix
    // @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/daily/current-day/{cityName}/{country}", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentDayDailyForecastForCity(@PathVariable("cityName") String cityName, @PathVariable("country") String country) {

        if(!StringVerifier.validString(cityName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validString(country)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        City city = cityRepository.findByNameAndCountry(cityName, country);

        if(city == null) {
            return new ResponseEntity<>(new CustomErrorType("The city doesn't exist in the database!"), HttpStatus.BAD_REQUEST);
        }

        Forecast forecast = wadsClient.getDarkSkyDailyForecast(city);

        if(forecast == null) {
            return new ResponseEntity<>(new CustomErrorType("Cannot get daily "
                    + "forecast for city: " + cityName + " at the moment!"), HttpStatus.NO_CONTENT);
        }

        // Convert some of the units to percentage, i.e. from 0.32 to 32
        wadsClient.convertUnitsDaily(forecast, true);

        // When the daily forecast for a given city  is requested from the API,
        // save or update the forecast for the current day
        DarkSkyForecast darkSkyForecast = dbClient.saveDailyForecast(city, forecast.getDaily().getData().get(0));

        // Add measurement units to the response
        ObjectNode response = wadsClient.buildJsonForDarkSkyForecastWithUnits(darkSkyForecast);
        response.put("timezone", city.getTimezone());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/daily/7-days/{cityName}/{country}", method = RequestMethod.GET)
    public ResponseEntity<?> get7DaysDailyForecastForCity(@PathVariable("cityName") String cityName, @PathVariable("country") String country) {

        if(!StringVerifier.validString(cityName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validString(country)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        City city = cityRepository.findByNameAndCountry(cityName, country);

        if(city == null) {
            return new ResponseEntity<>(new CustomErrorType("The city doesn't exist in the database!"), HttpStatus.BAD_REQUEST);
        }

        Forecast forecast = wadsClient.getDarkSkyDailyForecast(city);

        if(forecast == null) {
            return new ResponseEntity<>(new CustomErrorType("Cannot get daily "
                    + "forecast for city: " + cityName + " at the moment!"), HttpStatus.NO_CONTENT);
        }

        // Convert some of the measurement units to percentage, i.e. from 0.32 to 32
        wadsClient.convertUnitsDaily(forecast, false);

        // When the daily forecast for a given city  is requested from the API,
        // save or update the forecast for the current day
        dbClient.saveDailyForecast(city, forecast.getDaily().getData().get(0));

        List<ObjectNode> forecastList = new ArrayList<ObjectNode>();

        // Add the daily forecast for the current day and the next 7 days with measurement
        // units to the response. Do not save the forecast for the next days since it might
        // be inaccurate. Instead use the scheduled method to get the daily forecast for
        // each city, everyday and save it in the database
        for(int i = 0; i <= 7; i++) {

            // Create a JSON directly from the DailyDataPoint instead of converting to
            // DarkSkyForeacstObject and then creating a JSON for it, because with
            // the 7 day forecast some of the properties might be null...
            DailyDataPoint dailyDataPoint = forecast.getDaily().getData().get(i);
            forecastList.add(wadsClient.buildJsonForDailyDataPoint(dailyDataPoint));
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode forecastArray = mapper.valueToTree(forecastList);
        ArrayNode unitsArray = mapper.valueToTree(measurementUnits);

        ObjectNode response = mapper.createObjectNode();
        response.putArray("forecast").addAll(forecastArray);
        response.put("timezone", city.getTimezone());
        // Add the units array to the JSON
        response.putArray("units").addAll(unitsArray);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
     * Get the current forecast from the Dark Sky API, convert some of the numbers to
     * percentage as with the daily forecast, add the measurement units and send the
     * forecast to the front-end. This is definitely not the ideal solution, because
     * we have server load for something that can be done by the front-end, but if
     * only the Dark Sky API Key or the complete URL for the request is sent to
     * the front-end, the API Key will be exposed. Furthermore, the front-end can't
     * make an API call to the Dark Sky API on its own, because Dark Sky requires
     * the GPS coordinates of the location and not a city name...
    */
    @RequestMapping(value = "/currently/{cityName}/{country}", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentForecastForCity(@PathVariable("cityName") String cityName, @PathVariable("country") String country) {

        if(!StringVerifier.validString(cityName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validString(country)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        City city = cityRepository.findByNameAndCountry(cityName, country);

        if(city == null) {
            return new ResponseEntity<>(new CustomErrorType("The city doesn't exist in the database!"), HttpStatus.BAD_REQUEST);
        }

        Forecast forecast = wadsClient.getDarkSkyCurrentForecast(city);

        if(forecast == null) {
            return new ResponseEntity<>(new CustomErrorType("Cannot get current "
                    + "forecast for city: " + cityName + " at the moment!"), HttpStatus.NO_CONTENT);
        }

        /*
         * Convert units. possibly wre-write the method to support daily and currently
         */

        ObjectNode currentForecast = wadsClient.buildJsonForCurrentForecast(forecast);
        currentForecast.put("timezone", city.getTimezone());

        return new ResponseEntity<>(currentForecast, HttpStatus.OK);
    }

}
