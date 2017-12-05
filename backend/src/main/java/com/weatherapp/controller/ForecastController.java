package com.weatherapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weatherapp.model.entity.City;
import com.weatherapp.model.entity.DarkSkyForecast;
import com.weatherapp.model.entity.MeasurementUnit;
import com.weatherapp.repository.CityRepository;
import com.weatherapp.repository.DarkSkyForecastRepository;
import com.weatherapp.repository.MeasurementUnitRepository;
import com.weatherapp.util.CustomErrorType;
import com.weatherapp.util.StringVerifier;
import com.weatherapp.util.WeatherAppDarkSkyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("/forecast")
public class ForecastController {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DarkSkyForecastRepository darkSkyForecastRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Autowired
    private WeatherAppDarkSkyClient wadsClient;

    // Like with the WeatherAppDarkskyClient, load the measurement units in memory on start up
    // in order to minimize database access
    private static Iterable<MeasurementUnit> measurementUnits;

    @PostConstruct
    private void init() {
        measurementUnits = measurementUnitRepository.findAll();
    }

    @RequestMapping(value = "/get/all/{cityName}/{country}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllForecastsForCity(@PathVariable("cityName") String cityName, @PathVariable("country") String country) {

        if(!StringVerifier.validString(cityName)) {
            return new ResponseEntity<>(new CustomErrorType("Invalid city name!"), HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validString(country)) {
            return new ResponseEntity<>(new CustomErrorType("Invalid country name!"), HttpStatus.BAD_REQUEST);
        }

        City city = cityRepository.findByNameAndCountry(cityName, country);

        if(city == null) {
            return new ResponseEntity<>(new CustomErrorType("The city doesn't exist in the database!"), HttpStatus.BAD_REQUEST);
        }

        // Add Units to the response, but instead of appending them to each value like
        // with a single Current or Daily Forecast, add all units as an array and
        // append them only if needed in the front-end. Adding a unit to each value
        // from each forecast will be very inefficient
        Set<DarkSkyForecast> forecastSet = darkSkyForecastRepository.getAllByCity(city);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        ArrayNode forecastsArray = mapper.valueToTree(forecastSet);
        ArrayNode unitsArray = mapper.valueToTree(measurementUnits);

        response.putArray("forecasts").addAll(forecastsArray);
        response.put("timezone", city.getTimezone());
        response.putArray("units").addAll(unitsArray);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/get/by-date/{cityName}/{country}")
    public ResponseEntity<?> getForecastForCityForDate(@PathVariable("cityName") String cityName,
                                                                      @PathVariable("country") String country,
                                                                      @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        if(!StringVerifier.validString(cityName)) {
            return new ResponseEntity<>(new CustomErrorType("Invalid city name!"), HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validString(country)) {
            return new ResponseEntity<>(new CustomErrorType("Invalid country name!"), HttpStatus.BAD_REQUEST);
        }

        if(date == null) {
            return new ResponseEntity<>(new CustomErrorType("Invalid date!"), HttpStatus.BAD_REQUEST);
        }

        City city = cityRepository.findByNameAndCountry(cityName, country);

        if(city == null) {
            return new ResponseEntity<>(new CustomErrorType("The city doesn't exist in the database!"), HttpStatus.BAD_REQUEST);
        }

        DarkSkyForecast darkSkyForecast = darkSkyForecastRepository.getByCityAndTimeStamp(city, date.toInstant().getEpochSecond());

        // Since we have only the forecast for 1 day and 1 city, we can add units to the data
        ObjectNode response = wadsClient.buildJsonForDarkSkyForecastWithUnits(darkSkyForecast);
        response.put("timezone", city.getTimezone());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/get/between-dates/{cityName}/{country}")
    public ResponseEntity<?> getForecastForCityBetweenStartAndEndDate(@PathVariable("cityName") String cityName,
                                                                      @PathVariable("country") String country,
                                                                      @RequestParam("start") @DateTimeFormat(pattern = "dd.MM.yyyy") Date start,
                                                                      @RequestParam("end") @DateTimeFormat(pattern = "dd.MM.yyyy") Date end) {

        if(!StringVerifier.validString(cityName)) {
            return new ResponseEntity<>(new CustomErrorType("Invalid city name!"), HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validString(country)) {
            return new ResponseEntity<>(new CustomErrorType("Invalid country name!"), HttpStatus.BAD_REQUEST);
        }

        if(start == null || end == null) {
            return new ResponseEntity<>(new CustomErrorType("Invalid start or end date!"), HttpStatus.BAD_REQUEST);
        }

        City city = cityRepository.findByNameAndCountry(cityName, country);

        if(city == null) {
            return new ResponseEntity<>(new CustomErrorType("The city doesn't exist in the database!"), HttpStatus.BAD_REQUEST);
        }

        Set<DarkSkyForecast> forecastSet =
                darkSkyForecastRepository.getAllByCityAndTimeStampBetween(city, start.toInstant().getEpochSecond(), end.toInstant().getEpochSecond());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        ArrayNode forecastsArray = mapper.valueToTree(forecastSet);
        ArrayNode unitsArray = mapper.valueToTree(measurementUnits);

        response.putArray("forecasts").addAll(forecastsArray);
        response.put("timezone", city.getTimezone());
        response.putArray("units").addAll(unitsArray);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // Handle missing RequestParam
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> resolveException() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        response.put("Exception! Reason", "Missing Request Parameter");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
