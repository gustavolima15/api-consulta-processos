package com.apiconsultaprocessos.model;

public record ProcessoResultado(
    String numeroProcesso,
    String link,
    String status,
    String data
) {}