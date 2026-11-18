package io.cloudops.ordercart;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CartApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cartLifecycleWorksEndToEndOverHttp() throws Exception {
        mockMvc.perform(post("/api/carts/it-cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":\"LAB-HOODIE\",\"quantity\":2,\"unitPrice\":35.00}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.cartId").value("it-cart"))
            .andExpect(jsonPath("$.total").value(70.00));

        mockMvc.perform(get("/api/carts/it-cart"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].sku").value("LAB-HOODIE"));

        mockMvc.perform(delete("/api/carts/it-cart"))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/carts/it-cart"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void invalidPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/carts/bad-cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":\"BAD-QTY\",\"quantity\":-2,\"unitPrice\":10.00}"))
            .andExpect(status().isBadRequest());
    }
}
