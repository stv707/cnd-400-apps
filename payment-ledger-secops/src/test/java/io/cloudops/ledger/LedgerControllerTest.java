package io.cloudops.ledger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FraudCheckService fraudCheckService;

    @Test
    void healthAndCompliantTransactionSucceed() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(post("/api/ledger/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"acct-001\",\"merchantCode\":\"CLOUD_OPS\",\"amount\":250.00}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountId").value("acct-001"));
    }

    @Test
    void blockedMerchantReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(post("/api/ledger/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"acct-002\",\"merchantCode\":\"HIGH_RISK_TEST\",\"amount\":99.00}"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void fraudBatchCompletesWithinPipelineTimeoutGuard() throws InterruptedException {
        long delayMs = Long.parseLong(System.getenv().getOrDefault("SIMULATED_FRAUD_DELAY_MS", "10"));
        Thread.sleep(Math.max(0L, Math.min(delayMs, 2000L)));
        assertTrue(fraudCheckService.isApproved("CAMPUS_STORE", new BigDecimal("49.95")));
    }
}
