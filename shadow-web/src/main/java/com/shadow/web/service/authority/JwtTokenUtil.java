package com.shadow.web.service.authority;

import com.shadow.web.model.authority.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_AUDIENCE = "audience";
    static final String CLAIM_KEY_CREATED = "created";

    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    private String secret = "mySecret";

    private Long expiration = 604800L;

    private static Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims == null ? null : claims.getSubject();
        } catch (Exception e) {
            log.error("JwtTokenUtil.getUsernameFromToken寮傚父:" + e.toString(), e);
            username = null;
        }
        return username;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = claims == null ? null : new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            log.error("JwtTokenUtil.getCreatedDateFromToken寮傚父:" + e.toString(), e);
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims == null ? null : claims.getExpiration();
        } catch (Exception e) {
            log.error("JwtTokenUtil.getExpirationDateFromToken寮傚父:" + e.toString(), e);
            expiration = null;
        }
        return expiration;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = getClaimsFromToken(token);
            audience = claims == null ? null : (String) claims.get(CLAIM_KEY_AUDIENCE);
        } catch (Exception e) {
            log.error("JwtTokenUtil.getAudienceFromToken寮傚父:" + e.toString(), e);
            audience = null;
        }
        return audience;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JwtTokenUtil.getClaimsFromToken:" + e.toString(), e);
            claims = null;
        }
        return claims;
    }

    private Date       generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean isCreatedBeforeLastLogout(Date created, Date lastPasswordReset) {
        return (null != lastPasswordReset && created.before(lastPasswordReset));
    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        /*claims.put(CLAIM_KEY_AUDIENCE, generateAudience(device));*/
        claims.put(CLAIM_KEY_AUDIENCE, AUDIENCE_UNKNOWN);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastLogout(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) {
        String refreshedToken = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            if (null != claims){
                claims.put(CLAIM_KEY_CREATED, new Date());
                refreshedToken = generateToken(claims);
            }
        } catch (Exception e) {
            log.error("JwtTokenUtil.refreshToken寮傚父:" + e.toString(), e);
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        return (
                username.equals(user.getUsername())
                        && !isTokenExpired(token)
                        && !isCreatedBeforeLastLogout(created, user.getlastLogoutTime()));
    }
}