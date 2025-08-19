package com.uniquindio.saludonombre.service.impl;

import com.uniquindio.saludonombre.exception.NameIsEmptyException;
import com.uniquindio.saludonombre.service.SaludoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SaludoServiceImpl implements SaludoService {

    @Override
    public String getSaludo(String nombre) {
        if(!nombre.isEmpty()){
            return "Hola " + nombre;
        }else{
             throw new NameIsEmptyException();
        }
    }
}
