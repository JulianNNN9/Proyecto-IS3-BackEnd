package co.edu.uniquindio.laos.services.interfaces;

import co.edu.uniquindio.laos.dto.TokenDTO;

public interface UsuarioService {

    TokenDTO refreshToken(String expiredToken);

}
