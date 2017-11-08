package com.weatherapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/protected")
public class SampleController {


    @RequestMapping(value = "/message", method = RequestMethod.GET)
    //@PreAuthorize("hasRole('ADMIN')")
    //@PreAuthorize("@securityService.hasProtectedAccess()")
    public ResponseEntity<?> getMessage() {
        return new ResponseEntity<>("message", HttpStatus.OK);
    }

    /*
    @RequestMapping(value = "/another", method = RequestMethod.GET)
    public ResponseEntity<?> getAnotherMessage() {
        return new ResponseEntity<>("message", HttpStatus.OK);
    }
    */
}
