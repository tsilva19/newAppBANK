package com.transferencia.ByteBank.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transferencia")
public class TransferenciaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valor;
    private String destino;
    private LocalDateTime data;

    @PrePersist
    public void prePersist() {
        this.data = LocalDateTime.now();
    }
}
