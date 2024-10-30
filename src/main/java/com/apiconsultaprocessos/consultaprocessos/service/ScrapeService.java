package com.apiconsultaprocessos.consultaprocessos.service;

import com.apiconsultaprocessos.model.ProcessoResultado;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScrapeService {

    public List<ProcessoResultado> scrapeWebsite(List<String> processArray, String uf) throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);
    
        List<ProcessoResultado> resultados = new ArrayList<>();
    
        try {
            for (String processo : processArray) {
                driver.get("https://processual.trf1.jus.br/consultaProcessual/numeroProcessoOriginario.php?secao=TRF1");
    
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("uf"))).sendKeys(uf);
    
                WebElement campoProc = driver.findElement(By.id("proc"));
                campoProc.click();
                campoProc.sendKeys(processo);
                campoProc.sendKeys(Keys.ENTER);
    
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error")));
                    resultados.add(new ProcessoResultado(processo, "-", "Não encontrado", "-"));
                } catch (TimeoutException e) {
                    WebElement tabela = driver.findElement(By.cssSelector("div.span-19 table"));
                    List<WebElement> linhas = tabela.findElements(By.tagName("tr"));
    
                    for (WebElement linha : linhas) {
                        WebElement link = linha.findElement(By.tagName("a"));
                        String textoLink = link.getText();
    
                        if (textoLink.contains("RPV") || textoLink.contains("PRC")) {
                            link.click();
                            for (String handle : driver.getWindowHandles()) {
                                driver.switchTo().window(handle);
                            }
    
                            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ui-id-4"))).click();
                            WebElement ultimaMovimentacaoElement = driver.findElement(
                                    By.cssSelector("#aba-movimentacao table tbody tr td:nth-child(2)"));
                            String ultimaMovimentacao = ultimaMovimentacaoElement.getText();
    
                            String status = switch (ultimaMovimentacao) {
                                case "40920" -> "OFÍCIO INFORMANDO TRANSFERÊNCIA DO(S) VALOR(ES) AO TESOURO NACIONAL";
                                case "40910" -> "OFÍCIO INFORMANDO SAQUE(S) DO(S) VALOR(ES)";
                                case "40900" -> "OFÍCIO INFORMANDO VALOR DEPOSITado";
                                case "40510" -> "VALOR DEPOSITADO";
                                default -> "AINDA NÃO HOUVE DEPÓSITO";
                            };
    
                            String data = driver.findElement(
                                    By.cssSelector("#aba-movimentacao table tbody tr td:nth-child(1)")).getText();
                            resultados.add(new ProcessoResultado(processo, textoLink, status, data));
                        }
                    }
                }
            }
        } finally {
            driver.quit();
        }
    
        gerarRelatorioExcel(resultados, "relatorio_de_saida.xlsx");
        return resultados;
    }
    private void gerarRelatorioExcel(List<ProcessoResultado> dados, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relatório");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Número do Processo", "Link", "Status", "Data"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (ProcessoResultado resultado : dados) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(resultado.numeroProcesso());
            row.createCell(1).setCellValue(resultado.link());
            row.createCell(2).setCellValue(resultado.status());
            row.createCell(3).setCellValue(resultado.data());
        }

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }

        workbook.close();
        System.out.println("Relatório salvo como " + fileName);
    }
}
