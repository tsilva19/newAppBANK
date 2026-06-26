package com.transferencia.ByteBank.domain.port.out;

import java.time.Duration;

public interface TransferenciaCachePort {
    boolean existeChave(String chave);
    void salvarComTTL(String chave, Duration ttl);
}
