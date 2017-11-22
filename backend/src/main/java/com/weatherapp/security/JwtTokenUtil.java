package com.weatherapp.security;

import com.weatherapp.model.security.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Value("${weatherapp.token.secret}")
    private String secret;

    @Value("${weatherapp.token.expiration}")
    private Long expiration;

    private final String AUDIENCE_UNKNOWN   = "unknown";
    private final String AUDIENCE_WEB       = "web";
    private final String AUDIENCE_MOBILE    = "mobile";
    private final String AUDIENCE_TABLET    = "tablet";

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            created = new Date((Long) claims.get("created"));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            audience = (String) claims.get("audience");
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    private Claims getClaimsFromToken(String token) {

        String base64EncodedSecret = TextCodec.BASE64.encode(this.secret);

        return Jwts.parser()
                 .setSigningKey(base64EncodedSecret)
                 .parseClaimsJws(token)
                 .getBody();
    }

    private Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date generateExpirationDate(Date creationDate) {
        return new Date(creationDate.getTime() + this.expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(this.generateCurrentDate());
    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = this.getAudienceFromToken(token);
        return (this.AUDIENCE_TABLET.equals(audience) || this.AUDIENCE_MOBILE.equals(audience));
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private String generateAudience(Device device) {
        String audience = this.AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            audience = this.AUDIENCE_WEB;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }

    public String generateToken(UserDetails userDetails, Device device) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("sub", userDetails.getUsername());
        claims.put("audience", this.generateAudience(device));
        claims.put("created", this.generateCurrentDate());
        return doGenerateToken(claims);
    }

    private String doGenerateToken(Map<String, Object> claims) {

        Date creationDate = generateCurrentDate();
        Date expirationDate = generateExpirationDate(creationDate);

        // Encode the secret to base64 in order to have a valid JWT Signature
        // Also MacProvider.generateKey(SignatureAlgorithm.HS512) can be used
        // to generate better secret key
        String base64EncodedSecret = TextCodec.BASE64.encode(this.secret);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, base64EncodedSecret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = this.getCreatedDateFromToken(token);
        return (!(this.isCreatedBeforeLastPasswordReset(created, lastPasswordReset)) && (!(this.isTokenExpired(token))));
    }

    public String refreshToken(String token) {
        final Claims claims = this.getClaimsFromToken(token);
        return doGenerateToken(claims);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = this.getUsernameFromToken(token);
        final Date created = this.getCreatedDateFromToken(token);
        return (username.equals(user.getUsername()) && !(this.isTokenExpired(token)) && !(this.isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())));
    }

}
