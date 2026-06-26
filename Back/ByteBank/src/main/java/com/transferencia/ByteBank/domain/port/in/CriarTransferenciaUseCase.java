package com.transferencia.ByteBank.domain.port.in;

import com.transferencia.ByteBank.domain.model.Transferencia;

public interface CriarTransferenciaUseCase {
    Transferencia criar(Transferencia transferencia);
}
