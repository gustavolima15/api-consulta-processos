package com.apiconsultaprocessos.consultaprocessos.controller;

import com.apiconsultaprocessos.consultaprocessos.service.ScrapeService;
import com.apiconsultaprocessos.model.ProcessoResultado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scrape")
public class ScrapeController {

    private final ScrapeService scrapeService;

    @Autowired
    public ScrapeController(ScrapeService scrapeService) {
        this.scrapeService = scrapeService;
    }

    @PostMapping
    public ResponseEntity<List<ProcessoResultado>> iniciarScrape(@RequestBody Map<String, Object> request) {
        try {
            List<String> processos = (List<String>) request.get("processos");
            String uf = (String) request.get("uf");

            if (processos == null || uf == null) {
                return ResponseEntity.badRequest().body(null);
            }

            List<ProcessoResultado> resultados = scrapeService.scrapeWebsite(processos, uf);
            return ResponseEntity.ok(resultados);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}