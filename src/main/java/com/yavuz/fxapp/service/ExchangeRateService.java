package com.yavuz.fxapp.service;

import com.yavuz.fxapp.dto.ConversionResponse;
import com.yavuz.fxapp.dto.ExchangeRateResponse;
import com.yavuz.fxapp.exception.BadRequestException;
import com.yavuz.fxapp.model.Conversion;
import com.yavuz.fxapp.repository.ConversionRepository;
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
    private final ConversionRepository conversionRepository;

    public ExchangeRateService(ConversionRepository conversionRepository) {
        this.conversionRepository = conversionRepository;
    }

    public double getExchangeRate(String from, String to) {
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            throw new BadRequestException("Currency codes must not be empty.");
        }

        from = from.toUpperCase();
        to = to.toUpperCase();

        String url = "https://data.fixer.io/api/latest?access_key=" + apiKey + "&symbols=" + from + "," + to;
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

        if (response == null || response.getRates() == null) {
            throw new RuntimeException("Invalid response from Fixer API");
        }

        Map<String, Double> rates = response.getRates();

        if (!rates.containsKey(from) || !rates.containsKey(to)) {
            throw new BadRequestException("Invalid currency codes");
        }

        double eurToFrom = rates.get(from);
        double eurToTo = rates.get(to);

        return eurToTo / eurToFrom;
    }

    public ConversionResponse convert(double amount, String from, String to) {
        if (amount <= 0) {
            throw new BadRequestException("Amount must be greater than zero.");
        }

        double rate = getExchangeRate(from, to);
        double converted = amount * rate;
        String transactionId = UUID.randomUUID().toString();

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
