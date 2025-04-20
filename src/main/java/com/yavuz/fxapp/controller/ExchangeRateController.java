package com.yavuz.fxapp.controller;

import com.yavuz.fxapp.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    public String getExchangeRate(@RequestParam String from, @RequestParam String to) {
        double rate = exchangeRateService.getExchangeRate(from, to);
        return "Exchange rate from " + from + " to " + to + " is " + rate;
    }
}
