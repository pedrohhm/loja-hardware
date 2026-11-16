package com.exemplo.lojahardware.dto;

import com.exemplo.lojahardware.model.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CategoriaDto {

    private CategoriaDto() {
    }

    public record Entrada(
            @NotBlank(message = "Informe o nome da categoria.")
            @Size(max = 80, message = "O nome deve ter até 80 caracteres.") String nome,
            @Size(max = 250, message = "A descrição deve ter até 250 caracteres.") String descricao) {
    }

    public record Resposta(Long id, String nome, String descricao) {

        public static Resposta de(Categoria categoria) {
            return new Resposta(categoria.getId(), categoria.getNome(), categoria.getDescricao());
        }
    }
}