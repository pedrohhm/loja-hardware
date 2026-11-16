package com.exemplo.lojahardware.service;

import com.exemplo.lojahardware.dto.PedidoDto.Entrada;
import com.exemplo.lojahardware.dto.PedidoDto.ItemEntrada;
import com.exemplo.lojahardware.model.Cliente;
import com.exemplo.lojahardware.model.ItemPedido;
import com.exemplo.lojahardware.model.Pedido;
import com.exemplo.lojahardware.model.Produto;
import com.exemplo.lojahardware.repository.ClienteRepository;
import com.exemplo.lojahardware.repository.PedidoRepository;
import com.exemplo.lojahardware.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PedidoService {

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;

    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes, ProdutoRepository produtos) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.produtos = produtos;
    }

    public List<Pedido> listar() {
        return pedidos.findAll();
    }

    public Pedido buscar(Long id) {
        return pedidos.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
    }

    @Transactional
    public Pedido cadastrar(Entrada entrada) {
        Cliente cliente = buscarCliente(entrada.clienteId());
        List<ItemPedido> itens = ajustarEstoque(List.of(), entrada.itens());
        return pedidos.save(new Pedido(cliente, LocalDateTime.now(), itens, calcularTotal(itens)));
    }

    @Transactional
    public Pedido atualizar(Long id, Entrada entrada) {
        Pedido pedido = buscarParaAlteracao(id);
        Cliente cliente = buscarCliente(entrada.clienteId());
        List<ItemPedido> itens = ajustarEstoque(pedido.getItens(), entrada.itens());
        pedido.atualizar(cliente, itens, calcularTotal(itens));
        return pedido;
    }

    @Transactional
    public void excluir(Long id) {
        Pedido pedido = buscarParaAlteracao(id);
        ajustarEstoque(pedido.getItens(), List.of());
        pedidos.delete(pedido);
    }

    private Pedido buscarParaAlteracao(Long id) {
        return pedidos.buscarParaAlteracao(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
    }

    private Cliente buscarCliente(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    private List<ItemPedido> ajustarEstoque(List<ItemPedido> itensAnteriores, List<ItemEntrada> itensSolicitados) {
        Map<Long, Integer> quantidades = quantidadesPorProduto(itensSolicitados);
        Map<Long, ItemPedido> anteriores = new HashMap<>();
        for (ItemPedido item : itensAnteriores) {
            anteriores.put(item.getProduto().getId(), item);
        }

        // Bloqueia os produtos na mesma ordem quando dois pedidos disputam o estoque.
        Set<Long> ids = new TreeSet<>(anteriores.keySet());
        ids.addAll(quantidades.keySet());
        Map<Long, Produto> produtosBloqueados = new HashMap<>();
        for (Long id : ids) {
            Produto produto = produtos.buscarParaAlteracao(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto " + id + " não encontrado."));
            ItemPedido anterior = anteriores.get(id);
            int quantidadeAnterior = anterior == null ? 0 : anterior.getQuantidade();
            int quantidadeNova = quantidades.getOrDefault(id, 0);
            long estoqueAtualizado = (long) produto.getEstoque() + quantidadeAnterior - quantidadeNova;

            if (estoqueAtualizado < 0) {
                throw new ConflitoException("Estoque insuficiente para o produto " + produto.getSku() + ".");
            }
            if (estoqueAtualizado > Integer.MAX_VALUE) {
                throw new ConflitoException("A reposição excede o limite de estoque do produto " + produto.getSku() + ".");
            }

            produto.setEstoque((int) estoqueAtualizado);
            produtosBloqueados.put(id, produto);
        }

        return quantidades.entrySet().stream().map(entrada -> {
            Produto produto = produtosBloqueados.get(entrada.getKey());
            ItemPedido anterior = anteriores.get(entrada.getKey());
            BigDecimal preco = anterior == null ? produto.getPreco() : anterior.getPrecoUnitario();
            return new ItemPedido(produto, entrada.getValue(), preco);
        }).toList();
    }

    private Map<Long, Integer> quantidadesPorProduto(List<ItemEntrada> itens) {
        Map<Long, Integer> quantidades = new LinkedHashMap<>();
        for (ItemEntrada item : itens) {
            if (quantidades.putIfAbsent(item.produtoId(), item.quantidade()) != null) {
                throw new DadosInvalidosException("Informe cada produto uma única vez no pedido.");
            }
        }
        return quantidades;
    }

    private BigDecimal calcularTotal(List<ItemPedido> itens) {
        return itens.stream().map(ItemPedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}