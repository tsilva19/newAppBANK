# ByteBank — Arquitetura

## Visão Geral

O projeto segue **Clean Architecture**, onde as dependências sempre apontam de fora para dentro. O domínio não conhece nenhum framework — Spring, JPA e Redis ficam isolados na camada de infrastructure.

```
infrastructure → application → domain
```

---

## Estrutura de Pacotes

```
com.transferencia.ByteBank
├── domain/
│   ├── model/                  # Entidades puras (sem anotações de framework)
│   └── port/
│       ├── in/                 # Contratos de entrada (casos de uso)
│       └── out/                # Contratos de saída (repositório, cache)
├── application/
│   └── usecase/                # Implementação das regras de negócio
└── infrastructure/
    ├── persistence/            # JPA entity, Spring Data repository, adapter
    ├── cache/                  # Adapter do Redis
    └── web/                    # Controller REST e DTOs
```

---

## Fluxo de uma Requisição

Exemplo: **POST /transferencias**

### 1. Controller `infrastructure/web/TransferenciaController`

Ponto de entrada da aplicação. Recebe a requisição HTTP, converte o `TransferenciaRequestDTO` em um domain model `Transferencia` e delega para o use case através de uma interface.

```
POST /transferencias
Body: { "valor": 100.00, "destino": "João" }
```

O controller não sabe como a transferência é salva. Ele apenas entrega e recebe.

---

### 2. Input Port `domain/port/in/CriarTransferenciaUseCase`

Contrato de entrada. É uma interface que o controller conhece. O Spring injeta automaticamente quem a implementa.

```java
public interface CriarTransferenciaUseCase {
    Transferencia criar(Transferencia transferencia);
}
```

---

### 3. Use Case `application/usecase/TransferenciaUseCaseImpl`

O cérebro da aplicação. Aqui fica a regra de negócio:

1. Gera uma chave `"100.00:João"`
2. Consulta o cache: essa chave já existe?
   - **Sim** → lança exceção 422 (transferência duplicada)
   - **Não** → salva a chave no cache com TTL de 24 horas
3. Persiste a transferência no repositório
4. Retorna o objeto salvo

Não sabe que o cache é Redis nem que o banco é MySQL. Fala apenas com interfaces (output ports).

---

### 4. Output Ports `domain/port/out/`

Contratos de saída definidos dentro do domínio. O use case pede, os adapters entregam.

- `TransferenciaCachePort` — verifica e salva chaves no cache
- `TransferenciaRepositoryPort` — persiste e busca transferências

---

### 5. Adapters `infrastructure/`

Implementam os output ports e fazem o trabalho concreto com os frameworks.

- `RedisAdapter` → implementa `TransferenciaCachePort`, usa `StringRedisTemplate`
- `TransferenciaRepositoryAdapter` → implementa `TransferenciaRepositoryPort`, converte `Transferencia` (domain) ↔ `TransferenciaJpaEntity` (JPA) e delega para o `TransferenciaJpaRepository`

---

## Diagrama do Fluxo

```
[HTTP Request]
      │
      ▼
TransferenciaController          ← infrastructure/web
      │  converte DTO → domain model
      │  chama interface (input port)
      ▼
CriarTransferenciaUseCase        ← domain/port/in  (contrato)
      │
      ▼
TransferenciaUseCaseImpl         ← application/usecase  (regra de negócio)
      │                    │
      ▼                    ▼
TransferenciaCachePort    TransferenciaRepositoryPort    ← domain/port/out (contratos)
      │                    │
      ▼                    ▼
 RedisAdapter       TransferenciaRepositoryAdapter       ← infrastructure
      │                    │
      ▼                    ▼
   [Redis]             [MySQL via JPA]
      │                    │
      └──────────┬──────────┘
                 │  resultado sobe pelo mesmo caminho
                 ▼
TransferenciaController
      │  converte domain model → DTO
      ▼
[HTTP Response 200]
```

---

## Regra de Dependência

As dependências sempre apontam para dentro. O domínio não importa nada de fora.

| Camada         | Pode depender de         | Não pode depender de         |
|----------------|--------------------------|------------------------------|
| `domain`       | ninguém                  | application, infrastructure  |
| `application`  | domain                   | infrastructure               |
| `infrastructure` | application, domain    | —                            |

---

## Benefício Principal

Se amanhã o banco de dados mudar de MySQL para PostgreSQL, ou o cache mudar de Redis para Memcached, **apenas o adapter na infrastructure muda**. O domínio e os casos de uso não são tocados.

---

## Tecnologias

- Java 21
- Spring Boot 3.5
- Spring Data JPA + MySQL
- Spring Data Redis
- Flyway
- Lombok
