package com.bsworks.bastoslog.api.controller;

import com.bsworks.bastoslog.api.assembler.EntregaAssembler;
import com.bsworks.bastoslog.api.model.EntregaModel;
import com.bsworks.bastoslog.api.model.input.EntregaInput;
import com.bsworks.bastoslog.domain.model.Entrega;
import com.bsworks.bastoslog.domain.repository.EntregaRepository;
import com.bsworks.bastoslog.domain.service.FinalizacaoEntregaService;
import com.bsworks.bastoslog.domain.service.SolicitacaoEntregaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/entregas")
public class EntregaController {

    private final EntregaRepository entregaRepository;
    private final SolicitacaoEntregaService solicitacaoEntregaService;
    private final EntregaAssembler entregaAssembler;
    private final FinalizacaoEntregaService finalizacaoEntregaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntregaModel criar(@RequestBody @Validated EntregaInput entregaInput) {
        Entrega novaEntrega = entregaAssembler.toEntity(entregaInput);
        Entrega entregaSolicitada = solicitacaoEntregaService.solicitar(novaEntrega);
        return entregaAssembler.toModel(entregaSolicitada);
    }

    @PutMapping("/{id}/finalizacao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void finalizar(@PathVariable("id") Long entregaId) {
        finalizacaoEntregaService.finalizar(entregaId);
    }

    @GetMapping
    public List<EntregaModel> listar() {
        List<Entrega> entregas = entregaRepository.findAll();
        return entregaAssembler.toCollectionModel(entregas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregaModel> buscar(@PathVariable("id") Long clientId) {
        return entregaRepository.findById(clientId)
                .map(entrega -> ResponseEntity.ok(entregaAssembler.toModel(entrega)))
                .orElse(ResponseEntity.notFound().build());
    }
}
