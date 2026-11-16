package com.exemplo.lojahardware.dto;

import com.exemplo.lojahardware.model.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public final class ProdutoDto {

    private ProdutoDto() {
    }

    public record Entrada(
            @NotBlank(message = "Informe o nome do produto.")
            @Size(max = 120, message = "O nome deve ter até 120 caracteres.") String nome,
            @NotBlank(message = "Informe o SKU do produto.")
            @Size(max = 40, message = "O SKU deve ter até 40 caracteres.") String sku,
            @NotNull(message = "Informe o preço.")
            @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
            @Digits(integer = 8, fraction = 2, message = "O preço deve ter até 8 dígitos inteiros e 2 casas decimais.")
            BigDecimal preco,
            @NotNull(message = "Informe a quantidade disponível em estoque.")
            @PositiveOrZero(message = "O estoque deve ser zero ou positivo.") Integer estoque,
            @NotNull(message = "Informe o ID da categoria.")
            @Positive(message = "O ID da categoria deve ser positivo.") Long categoriaId) {
    }

    public record Resposta(Long id, String nome, String sku, BigDecimal preco, int estoque, Long categoriaId) {

        public static Resposta de(Produto produto) {
            return new Resposta(produto.getId(), produto.getNome(), produto.getSku(), produto.getPreco(),
                    produto.getEstoque(), produto.getCategoria().getId());
        }
    }
}