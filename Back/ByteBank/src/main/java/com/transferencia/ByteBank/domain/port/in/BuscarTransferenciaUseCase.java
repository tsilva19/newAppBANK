package com.transferencia.ByteBank.domain.port.in;

import com.transferencia.ByteBank.domain.model.Transferencia;

import java.util.List;

public interface BuscarTransferenciaUseCase {
    List<Transferencia> buscarTodas();
    Transferencia buscarPorId(String id);
}
