package com.transferencia.ByteBank.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferencia.ByteBank.domain.model.Transferencia;
import com.transferencia.ByteBank.domain.port.in.BuscarTransferenciaUseCase;
import com.transferencia.ByteBank.domain.port.in.CriarTransferenciaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferenciaController.class)
class TransferenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CriarTransferenciaUseCase criarUseCase;

    @MockitoBean
    private BuscarTransferenciaUseCase buscarUseCase;

    private Transferencia transferencia;

    @BeforeEach
    void setUp() {
        transferencia = new Transferencia("id-1", new BigDecimal("100.00"), "João", LocalDateTime.of(2026, 1, 10, 12, 0));
    }

    // -------------------------------------------------------------------------
    // POST /transferencias
    // -------------------------------------------------------------------------

    @Test
    void post_quandoDadosValidos_deveRetornar200ComCorpo() throws Exception {
        when(criarUseCase.criar(any())).thenReturn(transferencia);

        String body = """
                { "valor": 100.00, "destino": "João" }
                """;

        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.valor").value(100.00))
                .andExpect(jsonPath("$.destino").value("João"));
    }

    @Test
    void post_quandoTransferenciaDuplicada_deveRetornar422() throws Exception {
        when(criarUseCase.criar(any())).thenThrow(
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "não é possível transferir o mesmo valor para o mesmo destino, aguarde 24 horas")
        );

        String body = """
                { "valor": 100.00, "destino": "João" }
                """;

        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void post_quandoBodySemContentType_deveRetornar415() throws Exception {
        mockMvc.perform(post("/transferencias")
                        .content("{ \"valor\": 100.00, \"destino\": \"João\" }"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // -------------------------------------------------------------------------
    // GET /transferencias
    // -------------------------------------------------------------------------

    @Test
    void get_deveRetornar200ComListaDeTransferencias() throws Exception {
        when(buscarUseCase.buscarTodas()).thenReturn(List.of(transferencia));

        mockMvc.perform(get("/transferencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("id-1"))
                .andExpect(jsonPath("$[0].valor").value(100.00))
                .andExpect(jsonPath("$[0].destino").value("João"));
    }

    @Test
    void get_quandoNaoHaTransferencias_deveRetornar200ComListaVazia() throws Exception {
        when(buscarUseCase.buscarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/transferencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void get_deveRetornarMultiplasTransferencias() throws Exception {
        Transferencia outra = new Transferencia("id-2", new BigDecimal("250.50"), "Maria", LocalDateTime.now());
        when(buscarUseCase.buscarTodas()).thenReturn(List.of(transferencia, outra));

        mockMvc.perform(get("/transferencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].destino").value("Maria"));
    }

    // -------------------------------------------------------------------------
    // GET /transferencias/{id}
    // -------------------------------------------------------------------------

    @Test
    void getById_quandoEncontrado_deveRetornar200ComTransferencia() throws Exception {
        when(buscarUseCase.buscarPorId("id-1")).thenReturn(transferencia);

        mockMvc.perform(get("/transferencias/id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.valor").value(100.00))
                .andExpect(jsonPath("$.destino").value("João"));
    }

    @Test
    void getById_quandoNaoEncontrado_deveRetornar404ComMensagem() throws Exception {
        when(buscarUseCase.buscarPorId("id-inexistente"))
                .thenThrow(new RuntimeException("Transferência não encontrada com id: id-inexistente"));

        mockMvc.perform(get("/transferencias/id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Transferência não encontrada com id: id-inexistente"));
    }
}
