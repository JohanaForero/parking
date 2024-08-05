package com.forero.parking.infrastructure.util;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.JwtException;

@UtilityClass
public class JwtUtil {
    public static String getClaimFromToken(final String token, final String claimName) throws JwtException {
        try {
            final JWT jwt = JWTParser.parse(token);
            final JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            final Object claim = claimsSet.getClaim(claimName);
            return claim != null ? claim.toString() : null;
        } catch (Exception e) {
            throw new JwtException("Error extracting claim from JWT", e);
        }
    }
}
