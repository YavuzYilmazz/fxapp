package com.yavuz.fxapp.controller;

import com.yavuz.fxapp.service.ExchangeRateService;
import com.yavuz.fxapp.model.Conversion;
import com.yavuz.fxapp.repository.ConversionRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api")
public class BulkConversionController {

    private final ExchangeRateService exchangeRateService;
    private final ConversionRepository conversionRepository;

    public BulkConversionController(ExchangeRateService exchangeRateService,
                                    ConversionRepository conversionRepository) {
        this.exchangeRateService = exchangeRateService;
        this.conversionRepository = conversionRepository;
    }

    @PostMapping(value = "/bulk-convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<Conversion> bulkConvert(@RequestParam("file") MultipartFile file) {
        List<Conversion> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                double amount = Double.parseDouble(parts[0]);
                String from = parts[1].trim();
                String to = parts[2].trim();

                double rate = exchangeRateService.getExchangeRate(from, to);
                double converted = amount * rate;

                Conversion conversion = new Conversion();
                conversion.setTransactionId(UUID.randomUUID().toString());
                conversion.setAmount(amount);
                conversion.setFromCurrency(from);
                conversion.setToCurrency(to);
                conversion.setConvertedAmount(converted);
                conversion.setTransactionDate(java.time.LocalDateTime.now());

                results.add(conversion);
            }

            conversionRepository.saveAll(results);
        } catch (Exception e) {
            throw new RuntimeException("Bulk conversion failed: " + e.getMessage());
        }

        return results;
    }
}
