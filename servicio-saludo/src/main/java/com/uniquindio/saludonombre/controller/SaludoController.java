package com.uniquindio.saludonombre.controller;

import com.uniquindio.saludonombre.service.SaludoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SaludoController {

    private final SaludoService saludoService;

    @GetMapping("/saludo")
    public ResponseEntity<String> generarSaludo(
            @RequestParam String nombre) {

        log.info("Generando saludo");
        return ResponseEntity.ok(saludoService.getSaludo(nombre));
    }
}
