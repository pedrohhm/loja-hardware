package com.exemplo.lojahardware.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

final class RespostaCriada {

    private RespostaCriada() {
    }

    static <T> ResponseEntity<T> com(Long id, T resposta) {
        URI local = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(local).body(resposta);
    }
}