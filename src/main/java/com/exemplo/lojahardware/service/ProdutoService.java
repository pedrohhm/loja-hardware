package com.exemplo.lojahardware.service;

import com.exemplo.lojahardware.dto.ProdutoDto.Entrada;
import com.exemplo.lojahardware.model.Categoria;
import com.exemplo.lojahardware.model.Produto;
import com.exemplo.lojahardware.repository.CategoriaRepository;
import com.exemplo.lojahardware.repository.ProdutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository produtos;
    private final CategoriaRepository categorias;

    public ProdutoService(ProdutoRepository produtos, CategoriaRepository categorias) {
        this.produtos = produtos;
        this.categorias = categorias;
    }

    public List<Produto> listar() {
        return produtos.findAll();
    }

    public Produto buscar(Long id) {
        return produtos.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
    }

    @Transactional
    public Produto cadastrar(Entrada entrada) {
        String sku = entrada.sku().strip();
        if (produtos.existsBySku(sku)) {
            throw new ConflitoException("Já existe um produto com esse SKU.");
        }
        Categoria categoria = buscarCategoria(entrada.categoriaId());
        return produtos.save(new Produto(entrada.nome().strip(), sku, entrada.preco().setScale(2),
                entrada.estoque(), categoria));
    }

    @Transactional
    public Produto atualizar(Long id, Entrada entrada) {
        Produto produto = buscarParaAlteracao(id);
        String sku = entrada.sku().strip();
        if (produtos.existsBySkuAndIdNot(sku, id)) {
            throw new ConflitoException("Já existe um produto com esse SKU.");
        }
        Categoria categoria = buscarCategoria(entrada.categoriaId());
        produto.atualizar(entrada.nome().strip(), sku, entrada.preco().setScale(2), entrada.estoque(), categoria);
        return produto;
    }

    @Transactional
    public void excluir(Long id) {
        produtos.delete(buscarParaAlteracao(id));
    }

    private Produto buscarParaAlteracao(Long id) {
        return produtos.buscarParaAlteracao(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
    }

    private Categoria buscarCategoria(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));
    }
}