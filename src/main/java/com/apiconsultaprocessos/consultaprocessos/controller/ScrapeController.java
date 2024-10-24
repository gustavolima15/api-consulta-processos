package com.apiconsultaprocessos.consultaprocessos.controller;

import com.apiconsultaprocessos.consultaprocessos.service.ScrapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScrapeController {

    @Autowired
    private ScrapeService scrapeService;

    // Endpoint para iniciar o scraping
    @PostMapping("/scrape")
    public List<Map<String, String>> scrape(@RequestBody List<String> processos) {
        try {
            return scrapeService.scrapeProcessos(processos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao realizar o scraping: " + e.getMessage());
        }
    }
}
