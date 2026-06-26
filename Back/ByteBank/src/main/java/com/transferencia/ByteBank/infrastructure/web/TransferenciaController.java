package com.transferencia.ByteBank.infrastructure.web;

import com.transferencia.ByteBank.domain.model.Transferencia;
import com.transferencia.ByteBank.domain.port.in.BuscarTransferenciaUseCase;
import com.transferencia.ByteBank.domain.port.in.CriarTransferenciaUseCase;
import com.transferencia.ByteBank.infrastructure.web.dto.TransferenciaRequestDTO;
import com.transferencia.ByteBank.infrastructure.web.dto.TransferenciaResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transferencias")
@CrossOrigin(origins = "*")
public class TransferenciaController {

    private final CriarTransferenciaUseCase criarUseCase;
    private final BuscarTransferenciaUseCase buscarUseCase;

    public TransferenciaController(CriarTransferenciaUseCase criarUseCase, BuscarTransferenciaUseCase buscarUseCase) {
        this.criarUseCase = criarUseCase;
        this.buscarUseCase = buscarUseCase;
    }

    @PostMapping
    public ResponseEntity<TransferenciaResponseDTO> criarTransferencia(@RequestBody TransferenciaRequestDTO request) {
        Transferencia transferencia = new Transferencia();
        transferencia.setValor(request.valor());
        transferencia.setDestino(request.destino());

        Transferencia salva = criarUseCase.criar(transferencia);
        return ResponseEntity.ok(toResponse(salva));
    }

    @GetMapping
    public ResponseEntity<List<TransferenciaResponseDTO>> listarTodas() {
        List<TransferenciaResponseDTO> transferencias = buscarUseCase.buscarTodas()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transferencias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaResponseDTO> buscarPorId(@PathVariable String id) {
        Transferencia transferencia = buscarUseCase.buscarPorId(id);
        return ResponseEntity.ok(toResponse(transferencia));
    }

    private TransferenciaResponseDTO toResponse(Transferencia transferencia) {
        return new TransferenciaResponseDTO(
                transferencia.getId(),
                transferencia.getValor(),
                transferencia.getDestino(),
                transferencia.getData()
        );
    }
}
