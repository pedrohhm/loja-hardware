package com.exemplo.lojahardware.dto;

import com.exemplo.lojahardware.model.ItemPedido;
import com.exemplo.lojahardware.model.Pedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class PedidoDto {

    private PedidoDto() {
    }

    public record Entrada(
            @NotNull(message = "Informe o ID do cliente.")
            @Positive(message = "O ID do cliente deve ser positivo.") Long clienteId,
            @NotEmpty(message = "Informe ao menos um item no pedido.")
            List<@NotNull(message = "Informe os dados de cada item.") @Valid ItemEntrada> itens) {
    }

    public record ItemEntrada(
            @NotNull(message = "Informe o ID do produto de cada item.")
            @Positive(message = "O ID do produto deve ser positivo.") Long produtoId,
            @NotNull(message = "Informe a quantidade de cada item.")
            @Positive(message = "A quantidade de cada item deve ser positiva.") Integer quantidade) {
    }

    public record Resposta(Long id, Long clienteId, LocalDateTime dataPedido, BigDecimal total,
            List<ItemResposta> itens) {

        public static Resposta de(Pedido pedido) {
            return new Resposta(pedido.getId(), pedido.getCliente().getId(), pedido.getDataPedido(),
                    pedido.getTotal(), pedido.getItens().stream().map(ItemResposta::de).toList());
        }
    }

    public record ItemResposta(Long produtoId, int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {

        private static ItemResposta de(ItemPedido item) {
            return new ItemResposta(item.getProduto().getId(), item.getQuantidade(), item.getPrecoUnitario(),
                    item.getSubtotal());
        }
    }
}