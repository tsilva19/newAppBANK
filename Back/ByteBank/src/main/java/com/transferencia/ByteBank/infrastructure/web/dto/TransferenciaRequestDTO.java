package com.transferencia.ByteBank.infrastructure.web.dto;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(BigDecimal valor, String destino) {}
