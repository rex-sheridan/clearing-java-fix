package com.global.demo.web;

import com.global.demo.service.TradeService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("acceptor")
public class ClearingHouseController {
    private final TradeService tradeService;

    public ClearingHouseController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("trades", tradeService.getAllTrades());
        return "index";
    }
}
