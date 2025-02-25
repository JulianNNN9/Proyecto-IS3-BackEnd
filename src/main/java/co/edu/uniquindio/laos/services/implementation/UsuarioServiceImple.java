package co.edu.uniquindio.laos.services.implementation;

import co.edu.uniquindio.laos.config.JWTUtils;
import co.edu.uniquindio.laos.dto.TokenDTO;
import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImple implements UsuarioService {

    private final JWTUtils jwtUtils;

    public TokenDTO refreshToken(String expiredToken) {
        return new TokenDTO(jwtUtils.refreshToken(expiredToken));
    }

}
