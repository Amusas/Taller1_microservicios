package com.uniquindio.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true) //Activar anotaciones personalizadas
public class ServicioLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioLoginApplication.class, args);
    }

}
