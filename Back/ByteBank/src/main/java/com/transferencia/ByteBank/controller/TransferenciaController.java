package com.transferencia.ByteBank.controller;

import com.transferencia.ByteBank.entity.Transferencia;
import com.transferencia.ByteBank.service.TransferenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transferencias")
@CrossOrigin(origins = "*")
public class TransferenciaController {

    private final TransferenciaService service;

    public TransferenciaController(TransferenciaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transferencia> criarTransferencia(@RequestBody Transferencia transferencia) {
        Transferencia salva = service.salvar(transferencia);
        return ResponseEntity.ok(salva);
    }

    @GetMapping
    public ResponseEntity<List<Transferencia>> listarTodas() {
        List<Transferencia> transferencias = service.buscarTodas();
        return ResponseEntity.ok(transferencias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transferencia> buscarPorId(@PathVariable String id) {
        Transferencia transferencia = service.buscarPorId(id);
        return ResponseEntity.ok(transferencia);
    }


}

