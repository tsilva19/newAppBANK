# Bytebank FRONT

Projeto  gerado na versao angular 10.1.6  , para funcionar  precisa utilizar o node versao 12(nvm install 12  | nvm use 12)  e instalar  o angular versao 10 [Angular CLI](https://github.com/angular/angular-cli) version 10.1.6.

## Development server

Run `ng serve` in path Front/APP-BANK for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

# ByteBank Transferência - API REST  BACK

Este é um projeto de API REST para realizar transferências bancárias, desenvolvido com **Java 21** e **Spring Boot**.  
O sistema garante **idempotência** nas transferências: impede que o mesmo valor seja transferido para o mesmo destino mais de uma vez no intervalo de 24 horas, utilizando **Redis** como cache.

## 🛠️ Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Redis
- Docker e Docker Compose
- Lombok
- Spring Web
- Spring Validation

## 🚀 Funcionalidades

- ✅ Realizar transferência entre contas
- ✅ Persistência de transferências no MySQL
- ✅ Idempotência via Redis: bloqueio de transferências repetidas por 24 horas
- ✅ Tratamento de exceções com códigos HTTP adequados (`422 Unprocessable Entity`)
- ✅ CORS habilitado para integração com Frontend Angular

## 📦 Estrutura do Projeto

- Para instalar  o banco  de dados redis , rodar docker-compose up -d
- rodar a aplicaçao Backend



