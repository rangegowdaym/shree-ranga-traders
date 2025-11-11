package com.shreerangatraders.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/purchases")
    public String purchasesView() {
        return "purchases";
    }
    
    @GetMapping("/sales")
    public String salesView() {
        return "sales";
    }
    
    @GetMapping("/sales-payments")
    public String salesPaymentsView() {
        return "sales-payments";
    }
    
    @GetMapping("/purchase-payments")
    public String purchasePaymentsView() {
        return "purchase-payments";
    }
}
