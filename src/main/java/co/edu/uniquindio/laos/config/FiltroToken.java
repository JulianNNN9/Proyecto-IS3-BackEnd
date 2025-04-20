package co.edu.uniquindio.laos.config;

        import co.edu.uniquindio.laos.dto.MensajeDTO;
        import co.edu.uniquindio.laos.model.Rol;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import io.jsonwebtoken.Claims;
        import io.jsonwebtoken.ExpiredJwtException;
        import io.jsonwebtoken.Jws;
        import io.jsonwebtoken.MalformedJwtException;
        import io.jsonwebtoken.security.SignatureException;
        import jakarta.servlet.FilterChain;
        import jakarta.servlet.ServletException;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import lombok.RequiredArgsConstructor;
        import org.jetbrains.annotations.NotNull;
        import org.springframework.stereotype.Component;
        import org.springframework.web.filter.OncePerRequestFilter;

        import java.io.IOException;

        /**
         * Filtro de seguridad para validar tokens JWT y controlar acceso a recursos protegidos.
         * Intercepta todas las solicitudes HTTP y verifica la autenticación y autorización
         * según el rol del usuario para diferentes rutas de la API.
         */
        @Component
        @RequiredArgsConstructor
        public class FiltroToken extends OncePerRequestFilter {

            /**
             * Utilidad para manipular y validar tokens JWT
             */
            private final JWTUtils jwtUtils;

            /**
             * Método principal del filtro que se ejecuta en cada solicitud HTTP.
             * Implementa la lógica de validación de tokens y permisos según la ruta solicitada.
             */
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            @NotNull FilterChain filterChain) throws ServletException, IOException {
                // Configuración de cabeceras para CORS
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                response.addHeader("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, Authorization");

                // Para solicitudes OPTIONS (preflight), retornar OK inmediatamente
                if (request.getMethod().equals("OPTIONS")) {
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    // Obtener la URI y el token de la petición
                    String requestURI = request.getRequestURI();
                    String token = getToken(request);
                    boolean error = true;

                    try {
                        // Manejo de rutas de autenticación (/api/auth)
                        if (requestURI.startsWith("/api/auth")) {
                            // Permitir paso de token expirado en /refresh para poder emitir uno nuevo
                            if (token != null) {
                                Jws<Claims> jws = jwtUtils.parseJwt(token);
                                error = false;
                            } else {
                                crearRespuestaError("No se encontró el token para refrescar", HttpServletResponse.SC_UNAUTHORIZED, response);
                            }
                        // Rutas para clientes (/api/usuario)
                        } else if (requestURI.startsWith("/api/usuario")) {
                            if (token != null) {
                                Jws<Claims> jws = jwtUtils.parseJwt(token);
                                if (!jws.getPayload().get("rol").equals("CLIENTE")) {
                                    crearRespuestaError("No tiene permisos para acceder a este recurso: No es CLIENTE",
                                            HttpServletResponse.SC_FORBIDDEN, response);
                                } else {
                                    error = false;
                                }
                            } else {
                                crearRespuestaError("No tiene permisos para acceder a este recurso: Token NULL",
                                        HttpServletResponse.SC_FORBIDDEN, response);
                            }
                        // Rutas para estilistas (/api/estilista)
                        } else if (requestURI.startsWith("/api/estilista")) {
                            if (token != null) {
                                Jws<Claims> jws = jwtUtils.parseJwt(token);
                                if (!jws.getPayload().get("rol").equals("ESTILISTA")) {
                                    crearRespuestaError("No tiene permisos para acceder a este recurso",
                                            HttpServletResponse.SC_FORBIDDEN, response);
                                } else {
                                    error = false;
                                }
                            } else {
                                crearRespuestaError("No tiene permisos para acceder a este recurso",
                                        HttpServletResponse.SC_FORBIDDEN, response);
                            }
                        // Rutas para administradores (/api/admin)
                        } else if (requestURI.startsWith("/api/admin")) {
                            if (token != null) {
                                Jws<Claims> jws = jwtUtils.parseJwt(token);
                                if (!jws.getPayload().get("rol").equals("ADMIN")) {
                                    crearRespuestaError("No tiene permisos para acceder a este recurso",
                                            HttpServletResponse.SC_FORBIDDEN, response);
                                } else {
                                    error = false;
                                }
                            } else {
                                crearRespuestaError("No tiene permisos para acceder a este recurso",
                                        HttpServletResponse.SC_FORBIDDEN, response);
                            }
                        }
                        // Cualquier otra ruta no protegida
                        else {
                            error = false;
                        }
                    } catch (MalformedJwtException | SignatureException e) {
                        crearRespuestaError("El token es incorrecto",
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
                    } catch (ExpiredJwtException e) {
                        if (requestURI.startsWith("/api/auth")) {
                            error = false;  // Permitir token expirado en refresh
                        } else {
                            crearRespuestaError("El token esta vencido", HttpServletResponse.SC_UNAUTHORIZED, response);
                        }
                    } catch (Exception e) {
                        crearRespuestaError(e.getMessage(),
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
                    }
                    // Si no hubo errores, continuar con la cadena de filtros
                    if (!error) {
                        filterChain.doFilter(request, response);
                    }
                }
            }

            /**
             * Extrae el token JWT del encabezado de autorización.
             * @param req La solicitud HTTP
             * @return El token JWT sin el prefijo "Bearer " o null si no existe
             */
            private String getToken(HttpServletRequest req) {
                String header = req.getHeader("Authorization");
                if(header != null && header.startsWith("Bearer "))
                    return header.replace("Bearer ", "");
                return null;
            }

            /**
             * Crea y envía una respuesta HTTP con un mensaje de error.
             * @param mensaje Descripción del error
             * @param codigoError Código HTTP de error
             * @param response Objeto de respuesta HTTP
             */
            private void crearRespuestaError(String mensaje, int codigoError, HttpServletResponse
                    response) throws IOException {
                MensajeDTO<String> dto = new MensajeDTO<>(true, mensaje);
                response.setContentType("application/json");
                response.setStatus(codigoError);
                response.getWriter().write(new ObjectMapper().writeValueAsString(dto));
                response.getWriter().flush();
                response.getWriter().close();
            }

            /**
             * Valida si un token es válido y pertenece al rol especificado.
             * @param token El token JWT a validar
             * @param rol El rol requerido para acceder al recurso
             * @return true si hay error (token inválido o rol incorrecto), false si es válido
             */
            private boolean validarToken(String token, Rol rol) {
                boolean error = true;
                if (token != null) {
                    Jws<Claims> jws = jwtUtils.parseJwt(token);
                    if (Rol.valueOf(jws.getPayload().get("rol").toString()) == rol) {
                        error = false;
                    }
                }
                return error;
            }
        }