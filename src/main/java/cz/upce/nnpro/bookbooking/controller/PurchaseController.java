package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.service.PurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService service;

}

