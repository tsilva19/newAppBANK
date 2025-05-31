package com.transferencia.ByteBank.repository;

import com.transferencia.ByteBank.entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaRepository extends JpaRepository<Transferencia, String> {
}

