package com.training.microservices.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    private CurrencyExchangeProxy proxy;

    @Autowired
    public CurrencyConversionController(CurrencyExchangeProxy proxy) {
        this.proxy = proxy;
    }

    @GetMapping("currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable("from") String from,
                                                          @PathVariable("to") String to,
                                                          @PathVariable("quantity") BigDecimal quantity) {

        HashMap<String , String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().
                getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                        CurrencyConversion.class, uriVariables);

        CurrencyConversion currencyConversion = responseEntity.getBody();
//        return new CurrencyConversion(10001L, from, to, BigDecimal.ONE, BigDecimal.ONE,BigDecimal.ONE, "");
        return new CurrencyConversion(currencyConversion.getId(),
                        from, to,
                        quantity, currencyConversion.getConversionMultiple(),
                        quantity.multiply(currencyConversion.getConversionMultiple()),
                        currencyConversion.getEnvironment());
    }

    @GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable("from") String from,
                                                          @PathVariable("to") String to,
                                                          @PathVariable("quantity") BigDecimal quantity) {


        CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

        return new CurrencyConversion(currencyConversion.getId(),
                from, to,
                quantity, currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " "+ "feign");
    }
}
