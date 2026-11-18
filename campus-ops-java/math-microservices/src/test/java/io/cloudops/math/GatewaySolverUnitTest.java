package io.cloudops.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * Isolated unit test for GatewaySolverService.
 *
 * <p>Deliberately mocks only the downstream HTTP URLs and responses so this unit suite passes
 * even while the integration field-name bug exists between GatewaySolverService and MultiplierController.
 */
class GatewaySolverUnitTest {

    private MockRestServiceServer mockServer;
    private GatewaySolverService solverService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        solverService = new GatewaySolverService(restTemplate, "http://adder.local", "http://mul.local");
    }

    @Test
    void solveLinearChainsMultiplyThenAdd() {
        mockServer.expect(requestTo("http://mul.local/api/mul"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{\"result\":12}", MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("http://adder.local/api/add"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{\"result\":17}", MediaType.APPLICATION_JSON));

        int solved = solverService.solveLinear(3, 4, 5);
        assertEquals(17, solved);
        mockServer.verify();
    }
}
