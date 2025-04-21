package com.yavuz.fxapp.controller;

import com.yavuz.fxapp.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;
import com.yavuz.fxapp.dto.ConversionRequest;
import com.yavuz.fxapp.dto.ConversionResponse;
import com.yavuz.fxapp.model.Conversion;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.yavuz.fxapp.model.Conversion;
import com.yavuz.fxapp.repository.ConversionRepository;


@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ConversionRepository conversionRepository;

    public ExchangeRateController(ExchangeRateService exchangeRateService,
                                  ConversionRepository conversionRepository) {
        this.exchangeRateService = exchangeRateService;
        this.conversionRepository = conversionRepository;
    }

    @GetMapping("/rate")
    public String getExchangeRate(@RequestParam String from, @RequestParam String to) {
        double rate = exchangeRateService.getExchangeRate(from, to);
        return "Exchange rate from " + from + " to " + to + " is " + rate;
    }

    @PostMapping("/convert")
    public ConversionResponse convertCurrency(@RequestBody ConversionRequest request) {
        return exchangeRateService.convert(request.getAmount(), request.getFrom(), request.getTo());
    }

    @GetMapping("/history")
    public List<Conversion> getHistory(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (transactionId != null) {
            return conversionRepository.findById(transactionId)
                    .map(List::of)
                    .orElse(List.of());
        } else if (date != null) {
            return conversionRepository.findAllByTransactionDateBetween(
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            );
        } else {
            return conversionRepository.findAll();
        }
    }
}

