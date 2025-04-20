package com.yavuz.fxapp.service;

import com.yavuz.fxapp.dto.ConversionResponse;
import com.yavuz.fxapp.dto.ExchangeRateResponse;
import com.yavuz.fxapp.model.Conversion;
import com.yavuz.fxapp.repository.ConversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class ExchangeRateService {

    @Value("${fixer.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ConversionRepository conversionRepository;

    public double getExchangeRate(String from, String to) {
        from = from.toUpperCase();
        to = to.toUpperCase();

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

        // Save conversion to database
        Conversion conversion = new Conversion();
        conversion.setTransactionId(transactionId);
        conversion.setAmount(amount);
        conversion.setFromCurrency(from.toUpperCase());
        conversion.setToCurrency(to.toUpperCase());
        conversion.setConvertedAmount(converted);
        conversion.setTransactionDate(LocalDateTime.now());

        conversionRepository.save(conversion);

        return new ConversionResponse(transactionId, converted);
    }
}
