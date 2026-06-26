package com.transferencia.ByteBank.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transferencia {

    private String id;
    private BigDecimal valor;
    private String destino;
    private LocalDateTime data;

    public Transferencia() {}

    public Transferencia(String id, BigDecimal valor, String destino, LocalDateTime data) {
        this.id = id;
        this.valor = valor;
        this.destino = destino;
        this.data = data;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
