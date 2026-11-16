package com.exemplo.lojahardware.dto;

import com.exemplo.lojahardware.model.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ClienteDto {

    private ClienteDto() {
    }

    public record Entrada(
            @NotBlank(message = "Informe o nome do cliente.")
            @Size(max = 100, message = "O nome deve ter até 100 caracteres.") String nome,
            @NotBlank(message = "Informe o e-mail do cliente.")
            @Email(message = "Informe um e-mail válido.")
            @Size(max = 150, message = "O e-mail deve ter até 150 caracteres.") String email) {
    }

    public record Resposta(Long id, String nome, String email) {

        public static Resposta de(Cliente cliente) {
            return new Resposta(cliente.getId(), cliente.getNome(), cliente.getEmail());
        }
    }
}