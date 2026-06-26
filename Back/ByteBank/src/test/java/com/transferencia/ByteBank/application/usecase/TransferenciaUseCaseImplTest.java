package com.transferencia.ByteBank.application.usecase;

import com.transferencia.ByteBank.domain.model.Transferencia;
import com.transferencia.ByteBank.domain.port.out.TransferenciaCachePort;
import com.transferencia.ByteBank.domain.port.out.TransferenciaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaUseCaseImplTest {

    @Mock
    private TransferenciaRepositoryPort repositoryPort;

    @Mock
    private TransferenciaCachePort cachePort;

    @InjectMocks
    private TransferenciaUseCaseImpl useCase;

    private Transferencia transferencia;

    @BeforeEach
    void setUp() {
        transferencia = new Transferencia("id-1", new BigDecimal("100.00"), "João", LocalDateTime.now());
    }

    // -------------------------------------------------------------------------
    // criar
    // -------------------------------------------------------------------------

    @Test
    void criar_quandoTransferenciaValida_deveSalvarERetornar() {
        when(cachePort.existeChave("100.00:João")).thenReturn(false);
        when(repositoryPort.salvar(transferencia)).thenReturn(transferencia);

        Transferencia resultado = useCase.criar(transferencia);

        assertThat(resultado).isEqualTo(transferencia);
        verify(cachePort).salvarComTTL("100.00:João", Duration.ofHours(24));
        verify(repositoryPort).salvar(transferencia);
    }

    @Test
    void criar_quandoTransferenciaDuplicada_deveLancarExcecao422() {
        when(cachePort.existeChave("100.00:João")).thenReturn(true);

        assertThatThrownBy(() -> useCase.criar(transferencia))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não é possível transferir o mesmo valor para o mesmo destino");

        verify(cachePort, never()).salvarComTTL(any(), any());
        verify(repositoryPort, never()).salvar(any());
    }

    @Test
    void criar_quandoTransferenciaValida_deveSalvarCacheComTTLDe24Horas() {
        when(cachePort.existeChave(any())).thenReturn(false);
        when(repositoryPort.salvar(any())).thenReturn(transferencia);

        useCase.criar(transferencia);

        verify(cachePort).salvarComTTL(eq("100.00:João"), eq(Duration.ofHours(24)));
    }

    @Test
    void criar_chaveDeveSerCompostaPorValorEDestino() {
        Transferencia outra = new Transferencia("id-2", new BigDecimal("250.50"), "Maria", LocalDateTime.now());
        when(cachePort.existeChave("250.50:Maria")).thenReturn(false);
        when(repositoryPort.salvar(outra)).thenReturn(outra);

        useCase.criar(outra);

        verify(cachePort).existeChave("250.50:Maria");
    }

    // -------------------------------------------------------------------------
    // buscarTodas
    // -------------------------------------------------------------------------

    @Test
    void buscarTodas_deveRetornarListaDoRepositorio() {
        List<Transferencia> lista = List.of(transferencia);
        when(repositoryPort.buscarTodas()).thenReturn(lista);

        List<Transferencia> resultado = useCase.buscarTodas();

        assertThat(resultado).hasSize(1).contains(transferencia);
    }

    @Test
    void buscarTodas_quandoNaoHaTransferencias_deveRetornarListaVazia() {
        when(repositoryPort.buscarTodas()).thenReturn(List.of());

        List<Transferencia> resultado = useCase.buscarTodas();

        assertThat(resultado).isEmpty();
    }

    // -------------------------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------------------------

    @Test
    void buscarPorId_quandoEncontrado_deveRetornarTransferencia() {
        when(repositoryPort.buscarPorId("id-1")).thenReturn(Optional.of(transferencia));

        Transferencia resultado = useCase.buscarPorId("id-1");

        assertThat(resultado.getId()).isEqualTo("id-1");
        assertThat(resultado.getValor()).isEqualByComparingTo("100.00");
        assertThat(resultado.getDestino()).isEqualTo("João");
    }

    @Test
    void buscarPorId_quandoNaoEncontrado_deveLancarRuntimeException() {
        when(repositoryPort.buscarPorId("id-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId("id-inexistente"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("id-inexistente");
    }
}
