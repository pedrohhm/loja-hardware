package com.exemplo.lojahardware.service;

import com.exemplo.lojahardware.dto.CategoriaDto.Entrada;
import com.exemplo.lojahardware.model.Categoria;
import com.exemplo.lojahardware.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categorias;

    public CategoriaService(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    public List<Categoria> listar() {
        return categorias.findAll();
    }

    public Categoria buscar(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));
    }

    @Transactional
    public Categoria cadastrar(Entrada entrada) {
        String nome = entrada.nome().strip();
        if (categorias.existsByNome(nome)) {
            throw new ConflitoException("Já existe uma categoria com esse nome.");
        }
        return categorias.save(new Categoria(nome, entrada.descricao()));
    }

    @Transactional
    public Categoria atualizar(Long id, Entrada entrada) {
        Categoria categoria = buscar(id);
        String nome = entrada.nome().strip();
        if (categorias.existsByNomeAndIdNot(nome, id)) {
            throw new ConflitoException("Já existe uma categoria com esse nome.");
        }
        categoria.atualizar(nome, entrada.descricao());
        return categoria;
    }

    @Transactional
    public void excluir(Long id) {
        categorias.delete(buscar(id));
    }
}