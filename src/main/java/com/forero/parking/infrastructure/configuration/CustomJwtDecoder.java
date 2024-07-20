package com.forero.parking.infrastructure.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String LOGGER_PREFIX = String.format("[%s] ", CustomJwtDecoder.class.getSimpleName());

    @Override
    public Jwt decode(final String token) throws JwtException {
        try {
            final JWT parsedJwt = JWTParser.parse(token);
            final Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
            final Map<String, Object> claims = new LinkedHashMap<>(parsedJwt.getJWTClaimsSet().getClaims());

            if (claims.get(JwtClaimNames.IAT) instanceof final Date dateIat) {
                final Instant iat = dateIat.toInstant();
                claims.put(JwtClaimNames.IAT, iat);
            }

            if (claims.get(JwtClaimNames.EXP) instanceof final Date dateExp) {
                final Instant exp = dateExp.toInstant();
                claims.put(JwtClaimNames.EXP, exp);
            }

            String realmAccessString = String.valueOf(claims.get("realm_access"));
            realmAccessString = realmAccessString.replaceAll("(\\w+)", "\"$1\"");
            ;
            realmAccessString = realmAccessString.replace("\"-\"", "-");
            realmAccessString = realmAccessString.replace("=", ":");
            final RealmAccess realmAccess = OBJECT_MAPPER.readValue(realmAccessString, RealmAccess.class);
            claims.put("custom:roles", realmAccess.getRoles());

            return Jwt.withTokenValue(parsedJwt.getParsedString())
                    .headers(h -> h.putAll(headers))
                    .claims(c -> c.putAll(claims))
                    .build();
        } catch (final ParseException | JsonProcessingException parseException) {
            log.error(LOGGER_PREFIX + "[decode]: decode the token", parseException);
            throw new RuntimeException(parseException);
        }
    }

    @Getter
    @Setter
    static class RealmAccess {
        private List<String> roles;
    }
}
