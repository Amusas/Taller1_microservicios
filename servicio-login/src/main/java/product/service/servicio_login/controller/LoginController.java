package product.service.servicio_login.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import product.service.servicio_login.dto.LoginRequest;
import product.service.servicio_login.util.JwtUtils;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(jwtUtils.generateToken(loginRequest));
    }

}
