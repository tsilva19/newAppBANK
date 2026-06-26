package com.transferencia.ByteBank.domain.port.out;

import com.transferencia.ByteBank.domain.model.Transferencia;

import java.util.List;
import java.util.Optional;

public interface TransferenciaRepositoryPort {
    Transferencia salvar(Transferencia transferencia);
    List<Transferencia> buscarTodas();
    Optional<Transferencia> buscarPorId(String id);
}
