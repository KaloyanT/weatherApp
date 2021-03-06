package com.weatherapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weatherapp.model.entity.User;
import com.weatherapp.model.jwt.JwtAuthenticationRequest;
import com.weatherapp.model.jwt.JwtAuthenticationResponse;
import com.weatherapp.model.security.JwtUser;
import com.weatherapp.repository.UserRepository;
import com.weatherapp.security.JwtTokenUtil;
import com.weatherapp.util.CustomErrorType;
import com.weatherapp.util.StringVerifier;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    private final Logger logger = Logger.getLogger(this.getClass());

    @Value("${weatherapp.token.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> authenticationRequest(@RequestBody JwtAuthenticationRequest jwtAuthenticationRequest, Device device) throws AuthenticationException {

        // Perform the authentication
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        jwtAuthenticationRequest.getUsername(),
                        jwtAuthenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-authentication so we can generate token
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtAuthenticationRequest.getUsername());
        String token = this.jwtTokenUtil.generateToken(userDetails, device);

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> authenticationRequest(HttpServletRequest request) {

        if(request == null) {
            return ResponseEntity.badRequest().body(null);
        }

        String token = request.getHeader(this.tokenHeader);

        if(!StringVerifier.validString(token)) {
            return ResponseEntity.badRequest().body(null);
        }

        // Strip the Bearer prefix if the token is not null or empty
        token = token.substring(7);

        String username = this.jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) this.userDetailsService.loadUserByUsername(username);
        if (this.jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = this.jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public ResponseEntity<?> userSignUp(@RequestBody User newUser) {

        if(newUser == null) {
            return new ResponseEntity<>(new CustomErrorType("Invalid new user"), HttpStatus.BAD_REQUEST);
        }

        // Do RegEx validation
        if(!StringVerifier.validateUsername(newUser.getUsername())) {
            return new ResponseEntity<>(new CustomErrorType("Invalid username"), HttpStatus.BAD_REQUEST);
        }
        
        if(!StringVerifier.validatePassword(newUser.getPassword())) {
            return new ResponseEntity<>(new CustomErrorType("Invalid password"), HttpStatus.BAD_REQUEST);
        }

        if(!StringVerifier.validateEmail(newUser.getEmail())) {
            return new ResponseEntity<>(new CustomErrorType("Invalid email"), HttpStatus.BAD_REQUEST);
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh::mm:ss");
        Date currentDate = new Date();
        String currentDateAsString = dateFormatter.format(currentDate);
        try {
            currentDate = dateFormatter.parse(currentDateAsString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String password = bCryptPasswordEncoder.encode(newUser.getPassword());

        newUser.setLastPasswordResetDate(currentDate);
        newUser.setEnabled(true);
        newUser.setPassword(password);

        userRepository.save(newUser);

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
