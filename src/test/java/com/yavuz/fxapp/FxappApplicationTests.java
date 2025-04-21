package com.yavuz.fxapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FxappApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void test_invalidFromCurrencyCode() throws Exception {
        mockMvc.perform(get("/api/rate?from=INVALID&to=EUR"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_invalidToCurrencyCode() throws Exception {
        mockMvc.perform(get("/api/rate?from=USD&to=INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_emptyCurrencyCodes() throws Exception {
        mockMvc.perform(get("/api/rate?from=&to="))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_negativeAmountConversion() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "amount": -100,
                                "from": "USD",
                                "to": "EUR"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_bothInvalidCurrenciesConversion() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "amount": 100,
                                "from": "XXX",
                                "to": "YYY"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_missingToCurrencyConversion() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "amount": 100,
                                "from": "USD"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_invalidDateFormatHistory() throws Exception {
        mockMvc.perform(get("/api/history?date=20-04-2025"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_nonexistentTransactionIdHistory() throws Exception {
        mockMvc.perform(get("/api/history?transactionId=nonexistent-id"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void test_emptyParamsHistory() throws Exception {
        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_emptyCsvUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", "".getBytes());
        mockMvc.perform(multipart("/api/bulk-convert")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_invalidCsvContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bad.csv", "text/csv", "amount,from\nwrong,data".getBytes());
        mockMvc.perform(multipart("/api/bulk-convert")
                        .file(file))
                .andExpect(status().isBadRequest());
    }
}
