package com.weatherapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/protected")
public class SampleController {

    // hasRole requires the roles to be saved in the form ROLE_userRole, i.e. with a ROLE_ prefix
    @RequestMapping(value = "/protected/message", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMessage() {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", "authorized");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/another", method = RequestMethod.GET)
    public ResponseEntity<?> getAnotherMessage() {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", "anonymous");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
