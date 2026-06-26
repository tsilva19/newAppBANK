package com.transferencia.ByteBank.application.usecase;

import com.transferencia.ByteBank.domain.model.Transferencia;
import com.transferencia.ByteBank.domain.port.in.BuscarTransferenciaUseCase;
import com.transferencia.ByteBank.domain.port.in.CriarTransferenciaUseCase;
import com.transferencia.ByteBank.domain.port.out.TransferenciaCachePort;
import com.transferencia.ByteBank.domain.port.out.TransferenciaRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@Service
public class TransferenciaUseCaseImpl implements CriarTransferenciaUseCase, BuscarTransferenciaUseCase {

    private final TransferenciaRepositoryPort repositoryPort;
    private final TransferenciaCachePort cachePort;

    public TransferenciaUseCaseImpl(TransferenciaRepositoryPort repositoryPort, TransferenciaCachePort cachePort) {
        this.repositoryPort = repositoryPort;
        this.cachePort = cachePort;
    }

    @Override
    public Transferencia criar(Transferencia transferencia) {
        String chave = gerarChave(transferencia);
        if (cachePort.existeChave(chave)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "não é possível transferir o mesmo valor para o mesmo destino, aguarde 24 horas");
        }
        cachePort.salvarComTTL(chave, Duration.ofHours(24));
        return repositoryPort.salvar(transferencia);
    }

    @Override
    public List<Transferencia> buscarTodas() {
        return repositoryPort.buscarTodas();
    }

    @Override
    public Transferencia buscarPorId(String id) {
        return repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Transferência não encontrada com id: " + id));
    }

    private String gerarChave(Transferencia transferencia) {
        return transferencia.getValor() + ":" + transferencia.getDestino();
    }
}
