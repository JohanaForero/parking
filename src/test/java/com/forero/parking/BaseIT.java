package com.forero.parking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.forero.parking.configuration.CustomTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(CustomTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIT {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    protected static final String AUTHORIZATION = "Authorization";

    protected static final String BEARER = "Bearer ";

    protected static final String ADMIN_TOKEN =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IklGdXFsT1JhbFhtcGlycWxfbnV5ODU" +
                    "5bDNrNk9tZVNVZlJPeTA1Z0dqd3MifQ.eyJleHAiOjk3MjE0NTAzMjksImlhdCI6MTcyMTQyODcyOSwianRpIjoiNjg4ZjYzMTItZWR" +
                    "jMi00NzFkLTlkMTctZmQ1YTVjM2M1MTM4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9wYXJraW5nIiwiYXVkIj" +
                    "oiYWNjb3VudCIsInN1YiI6IjIyZjU5MGNlLTAxMzEtNDdiZS05NWI3LTM4MWJlZjQyYmJlMiIsInR5cCI6IkJlYXJlciIsImF6cCI6I" +
                    "nBhcmtpbmctY2xpZW50Iiwic2lkIjoiMzJlYWExYzktMDk4NC00ODcyLWI3ZjItNWUzMzA5NWFiNGUyIiwiYWNyIjoiMSIsInJlYWxt" +
                    "X2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtcGFya2luZyIsInVtYV9hdXRob3JpemF0aW9" +
                    "uIiwiQURNSU4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLW" +
                    "FjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6dHJ1Z" +
                    "SwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiIiwiZmFtaWx5X25hbWUiOiIiLCJlbWFpbCI6ImFkbWlu" +
                    "QG1haWwuY29tIn0.ApJ9uj4uJcs8zi9QZSd8CeQq34FIiOk0pYGbkTAM5vUHdyNUVCem6QowhaSEdbuer6aldMs8f0NTyAhcKOUeKeP" +
                    "P--qNAwJUeZUrMh4-kVusHM_Y86qtRwskH6imLZf4vlKtl7USNvrrWRoJtZhZTqz7U7V23pSu1MbCFtUUwwOJ3X_JxPiWmKDaRVpn34" +
                    "h5z36Y2Zf5dd5VL1V47Zv5CWt011J-qJ-UTIkP3jcdKiiu2UkMhvQ3v78AsOZIhEC7uV1FmU3OgOXnOJ6vNJHUr6Gz9c_UhB0rFJ154" +
                    "4pvi5FLR3m5SK5-BjXHXm9hKoSb9G3aIAbL3DfuvD_btkD35A";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        this.jdbcTemplate.update("DELETE FROM parking_lot");
        this.jdbcTemplate.update("DELETE FROM history");
        this.jdbcTemplate.update("DELETE FROM vehicle");
        this.jdbcTemplate.update("DELETE FROM Detail_register_vehicles");
    }
}
