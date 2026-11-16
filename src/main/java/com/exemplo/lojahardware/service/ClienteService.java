package com.exemplo.lojahardware.service;

import com.exemplo.lojahardware.dto.ClienteDto.Entrada;
import com.exemplo.lojahardware.model.Cliente;
import com.exemplo.lojahardware.repository.ClienteRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clientes;

    public ClienteService(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> listar() {
        return clientes.findAll();
    }

    public Cliente buscar(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    @Transactional
    public Cliente cadastrar(Entrada entrada) {
        String email = entrada.email().strip().toLowerCase(Locale.ROOT);
        if (clientes.existsByEmail(email)) {
            throw new ConflitoException("Já existe um cliente com esse e-mail.");
        }
        return clientes.save(new Cliente(entrada.nome().strip(), email));
    }

    @Transactional
    public Cliente atualizar(Long id, Entrada entrada) {
        Cliente cliente = buscar(id);
        String email = entrada.email().strip().toLowerCase(Locale.ROOT);
        if (clientes.existsByEmailAndIdNot(email, id)) {
            throw new ConflitoException("Já existe um cliente com esse e-mail.");
        }
        cliente.atualizar(entrada.nome().strip(), email);
        return cliente;
    }

    @Transactional
    public void excluir(Long id) {
        clientes.delete(buscar(id));
    }
}