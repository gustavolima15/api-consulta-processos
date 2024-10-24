package com.apiconsultaprocessos.consultaprocessos.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.stereotype.Service;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ScrapeService {
    public List<Map<String, String>> scrapeProcessos(List<String> processos) {
        // Configura o ChromeDriver e as opções do navegador
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        // **Garantir que o modo headless NÃO está ativado**
      // Isso garante que a janela seja exibida
        options.addArguments("--start-maximized");  // Abrir o navegador maximizado
        options.addArguments("--remote-allow-origins=*");  // Evitar erros com permissões

        WebDriver driver = new ChromeDriver(options);
        List<Map<String, String>> resultados = new ArrayList<>();

        try {
            for (String processo : processos) {
                // Navegar para a página de consulta
                driver.get("https://processual.trf1.jus.br/consultaProcessual/numeroProcessoOriginario.php?secao=TRF1");

                // Selecionar a UF e preencher o número do processo
                WebElement ufSelect = driver.findElement(By.id("uf"));
                ufSelect.sendKeys("BA");

                WebElement inputProcesso = driver.findElement(By.id("proc"));
                inputProcesso.sendKeys(processo);
                inputProcesso.submit();

                // Capturar o resultado
                WebElement resultado = driver.findElement(By.className("resultado"));
                String status = resultado.getText();

                // Adicionar o resultado à lista
                Map<String, String> dadosProcesso = new HashMap<>();
                dadosProcesso.put("processo", processo);
                dadosProcesso.put("status", status);
                resultados.add(dadosProcesso);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();  // Fecha o navegador
        }

        return resultados;
    }
}   