package com.forero.parking.infrastructure.util;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.JwtException;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@UtilityClass
public class JwtUtil {
    public static String getClaimFromToken(final String token, final String claimName) throws JwtException {
        try {
            final JWT jwt = JWTParser.parse(token);
            final JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            final Object claim = claimsSet.getClaim(claimName);
            return claim != null ? claim.toString() : null;
        } catch (final Exception e) {
            throw new JwtException("Error extracting claim from JWT", e);
        }
    }

    public static List<String> getRolesFromToken(final String token) {
        try {
            final JWT parsedJwt = JWTParser.parse(token);
            final Map<String, Object> claims = parsedJwt.getJWTClaimsSet().getClaims();
            @SuppressWarnings("unchecked") final Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            if (realmAccess != null) {
                @SuppressWarnings("unchecked") final List<String> roles = (List<String>) realmAccess.get("roles");
                return roles != null ? roles : Collections.emptyList();
            }
            return Collections.emptyList();
        } catch (final ParseException e) {
            throw new JwtException("Error parsing JWT", e);
        }
    }

    public static boolean isUserPartner(final String token) {
        final List<String> roles = getRolesFromToken(token);
        return roles.contains("PARTNER");
    }
}
