package com.exemplo.lojahardware.controller;

import com.exemplo.lojahardware.dto.PedidoDto.Entrada;
import com.exemplo.lojahardware.dto.PedidoDto.Resposta;
import com.exemplo.lojahardware.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Resposta> listar() {
        return service.listar().stream().map(Resposta::de).toList();
    }

    @GetMapping("/{id}")
    public Resposta buscar(@PathVariable Long id) {
        return Resposta.de(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Resposta> cadastrar(@Valid @RequestBody Entrada entrada) {
        Resposta resposta = Resposta.de(service.cadastrar(entrada));
        return RespostaCriada.com(resposta.id(), resposta);
    }

    @PutMapping("/{id}")
    public Resposta atualizar(@PathVariable Long id, @Valid @RequestBody Entrada entrada) {
        return Resposta.de(service.atualizar(id, entrada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}