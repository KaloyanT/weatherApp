package com.weatherapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weatherapp.model.entity.MeasurementUnit;
import com.weatherapp.repository.MeasurementUnitRepository;
import com.weatherapp.util.CustomErrorType;
import com.weatherapp.util.StringVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/unit")
public class MeasurementUnitController {

    @Autowired
    MeasurementUnitRepository measurementUnitRepository;

    @RequestMapping(value = "/get/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllMeasurementUnits() {

        Iterable<MeasurementUnit> measurementUnitIterable = measurementUnitRepository.findAll();

        if(measurementUnitIterable == null) {
            return new ResponseEntity<>(new CustomErrorType("No measurement units found!"), HttpStatus.NO_CONTENT);
        }

        List<MeasurementUnit> measurementUnitList = new ArrayList<MeasurementUnit>();

        // Java 8 Method Reference
        measurementUnitIterable.forEach(measurementUnitList::add);

        return new ResponseEntity<>(measurementUnitList, HttpStatus.OK);
    }


    @RequestMapping(value = "/get/{quantity}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeasurementUnitForQunatity(@PathVariable("quantity") String quantity) {

        MeasurementUnit unit = measurementUnitRepository.findByQuantity(quantity);

        if(unit == null) {
            return new ResponseEntity<>(new CustomErrorType("No measurement unit found for this quantity!"), HttpStatus.NO_CONTENT);
        }

        // Always return lists
        List<MeasurementUnit> measurementUnitList = new ArrayList<MeasurementUnit>();
        measurementUnitList.add(unit);

        return new ResponseEntity<>(measurementUnitList, HttpStatus.OK);
    }


    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResponseEntity<?> insertNewMeasurementUnit(@RequestBody MeasurementUnit newUnit) {

        if(newUnit == null || !StringVerifier.validString(newUnit.getQuantity()) || !StringVerifier.validString(newUnit.getMeasurementUnit())) {
            return new ResponseEntity<>(new CustomErrorType("Bad JSON formatting!"), HttpStatus.BAD_REQUEST);
        }

        MeasurementUnit unit = measurementUnitRepository.findByQuantity(newUnit.getQuantity());

        // Override the existing entry if a measurement unit for this quantity already exist
        if(unit != null) {
            unit.setQuantity(newUnit.getQuantity());
            unit.setMeasurementUnit(newUnit.getMeasurementUnit());
            measurementUnitRepository.save(unit);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        // Otherwise just save the new unit
        measurementUnitRepository.save(newUnit);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
     * No delete or update (PUT) methods needed for now
     */


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
