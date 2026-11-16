package com.exemplo.lojahardware.controller;

import com.exemplo.lojahardware.service.ConflitoException;
import com.exemplo.lojahardware.service.DadosInvalidosException;
import com.exemplo.lojahardware.service.RecursoNaoEncontradoException;
import java.util.stream.Collectors;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class TratamentoDeErros {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Erro> naoEncontrado(RecursoNaoEncontradoException excecao) {
        return responder(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Erro> conflito(ConflitoException excecao) {
        return responder(HttpStatus.CONFLICT, excecao.getMessage());
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Erro> dadosInvalidos(DadosInvalidosException excecao) {
        return responder(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erro> entradaInvalida(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));
        return responder(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Erro> jsonInvalido() {
        return responder(HttpStatus.BAD_REQUEST, "Envie um JSON válido com os campos esperados.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Erro> identificadorInvalido() {
        return responder(HttpStatus.BAD_REQUEST, "Informe um identificador numérico válido.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Erro> conflitoDeDados() {
        return responder(HttpStatus.CONFLICT,
                "A operação conflita com um registro existente ou com um vínculo entre cadastros.");
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<Erro> alteracaoConcorrente() {
        return responder(HttpStatus.CONFLICT, "Outra requisição alterou esses dados. Consulte o registro e tente novamente.");
    }

    private ResponseEntity<Erro> responder(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new Erro(mensagem));
    }

    public record Erro(String mensagem) {
    }
}