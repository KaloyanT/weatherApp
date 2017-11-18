package com.weatherapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weatherapp.model.entity.City;
import com.weatherapp.repository.CityRepository;
import com.weatherapp.util.CustomErrorType;
import com.weatherapp.util.StringVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @RequestMapping(value = "/get/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCities() {

        Iterable<City> cities = cityRepository.findAll();

        if(cities == null) {
            return new ResponseEntity<>(new CustomErrorType("No cities found found!"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResponseEntity<?> insertCity(@RequestBody City newCity) {

        if(newCity == null || !StringVerifier.validString(newCity.getName()) || !StringVerifier.validString(newCity.getCountry())) {
            return new ResponseEntity<>(new CustomErrorType("Bad JSON Formatting!"), HttpStatus.NO_CONTENT);
        }

        // Check to see if the city already exists. In this case just update the entry
        City city = cityRepository.findByNameAndCountry(newCity.getName(), newCity.getCountry());

        if(city == null) {
            city = new City();
        }

        city.setName(newCity.getName());
        city.setCountry(newCity.getCountry());
        city.setLatitude(newCity.getLatitude());
        city.setLongitude(newCity.getLongitude());

        cityRepository.save(city);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    // Handle empty POST Body
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> resolveException() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        response.put("Exception! Reason", "Empty Body");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
