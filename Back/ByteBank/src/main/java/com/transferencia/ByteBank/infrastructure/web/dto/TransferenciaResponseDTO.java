package com.transferencia.ByteBank.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaResponseDTO(String id, BigDecimal valor, String destino, LocalDateTime data) {}
