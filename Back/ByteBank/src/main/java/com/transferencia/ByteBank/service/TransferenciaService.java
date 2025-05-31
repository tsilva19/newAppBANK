package com.transferencia.ByteBank.service;

import com.transferencia.ByteBank.entity.Transferencia;
import com.transferencia.ByteBank.repository.TransferenciaRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
public class TransferenciaService {

    private final TransferenciaRepository repository;

    private final RedisService redisService;


    public Transferencia salvar(Transferencia transferencia) {
        String chave = gerarChave(transferencia);

        if (redisService.existeChave(chave)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "não é possível transferir o mesmo valor para o mesmo destino, aguarde 24 horas");
        }

        redisService.salvarChaveComTTL(chave, Duration.ofHours(24));

        return repository.save(transferencia);
    }

    public List<Transferencia> buscarTodas() {
        return repository.findAll();
    }

    public Transferencia buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transferência não encontrada com id: " + id));
    }

    private String gerarChave(Transferencia transferencia) {
        return transferencia.getValor() + ":" + transferencia.getDestino();
    }
}

