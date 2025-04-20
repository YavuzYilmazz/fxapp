package com.yavuz.fxapp.service;

import com.yavuz.fxapp.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.yavuz.fxapp.dto.ConversionResponse;

import java.util.Map;
import java.util.UUID;

@Service
public class ExchangeRateService {

    @Value("${fixer.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double getExchangeRate(String from, String to) {
        String url = "https://data.fixer.io/api/latest?access_key=" + apiKey + "&symbols=" + from + "," + to;

        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

        if (response == null || response.getRates() == null) {
            throw new RuntimeException("Invalid response from Fixer API");
        }

        Map<String, Double> rates = response.getRates();

        if (!rates.containsKey(from) || !rates.containsKey(to)) {
            throw new RuntimeException("Exchange rate not found for one or both currencies");
        }

        double eurToFrom = rates.get(from);
        double eurToTo = rates.get(to);

        return eurToTo / eurToFrom;
    }

    public ConversionResponse convert(double amount, String from, String to) {
        double rate = getExchangeRate(from, to);
        double converted = amount * rate;
        String transactionId = UUID.randomUUID().toString();

        return new ConversionResponse(transactionId, converted);
    }


}
