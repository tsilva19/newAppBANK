package com.transferencia.ByteBank.infrastructure.persistence;

import com.transferencia.ByteBank.domain.model.Transferencia;
import com.transferencia.ByteBank.domain.port.out.TransferenciaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TransferenciaRepositoryAdapter implements TransferenciaRepositoryPort {

    private final TransferenciaJpaRepository jpaRepository;

    public TransferenciaRepositoryAdapter(TransferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transferencia salvar(Transferencia transferencia) {
        TransferenciaJpaEntity entity = toEntity(transferencia);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Transferencia> buscarTodas() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Transferencia> buscarPorId(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private TransferenciaJpaEntity toEntity(Transferencia transferencia) {
        TransferenciaJpaEntity entity = new TransferenciaJpaEntity();
        entity.setId(transferencia.getId());
        entity.setValor(transferencia.getValor());
        entity.setDestino(transferencia.getDestino());
        entity.setData(transferencia.getData());
        return entity;
    }

    private Transferencia toDomain(TransferenciaJpaEntity entity) {
        return new Transferencia(
                entity.getId(),
                entity.getValor(),
                entity.getDestino(),
                entity.getData()
        );
    }
}
