package com.yavuz.fxapp.controller;

import com.yavuz.fxapp.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;
import com.yavuz.fxapp.dto.ConversionRequest;
import com.yavuz.fxapp.dto.ConversionResponse;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    public String getExchangeRate(@RequestParam String from, @RequestParam String to) {
        from = from.toUpperCase();
        to = to.toUpperCase();
        double rate = exchangeRateService.getExchangeRate(from, to);
        return "Exchange rate from " + from + " to " + to + " is " + rate;
    }

    @PostMapping("/convert")
    public ConversionResponse convertCurrency(@RequestBody ConversionRequest request) {
        return exchangeRateService.convert(request.getAmount(), request.getFrom(), request.getTo());
    }



}
