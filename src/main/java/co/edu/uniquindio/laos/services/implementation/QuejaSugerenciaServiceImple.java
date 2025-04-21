package co.edu.uniquindio.laos.services.implementation;

                    import co.edu.uniquindio.laos.dto.quejasugerencia.EnviarQuejaSugerenciaDTO;
                    import co.edu.uniquindio.laos.dto.quejasugerencia.QuejaSugerenciaDTO;
                    import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorClienteDTO;
                    import co.edu.uniquindio.laos.dto.quejasugerencia.QuejasPorTipoDTO;
                    import co.edu.uniquindio.laos.model.EstadoPQRS;
                    import co.edu.uniquindio.laos.model.QuejaSugerencia;
                    import co.edu.uniquindio.laos.model.TipoPQRS;
                    import co.edu.uniquindio.laos.model.Usuario;
                    import co.edu.uniquindio.laos.repositories.QuejaSugerenciaRepo;
                    import co.edu.uniquindio.laos.services.interfaces.QuejaSugerenciaService;
                    import co.edu.uniquindio.laos.services.interfaces.UsuarioService;
                    import lombok.RequiredArgsConstructor;
                    import org.springframework.stereotype.Service;
                    import org.springframework.transaction.annotation.Transactional;

                    import java.time.LocalDateTime;
                    import java.util.Arrays;
                    import java.util.List;
                    import java.util.Optional;
                    import java.util.stream.Collectors;

                    /**
                     * Implementación del servicio de quejas y sugerencias que maneja la lógica de negocio
                     * para la gestión de PQRS (Peticiones, Quejas, Reclamos y Sugerencias) en el sistema.
                     *
                     * Esta clase se encarga de registrar, actualizar y consultar las quejas y sugerencias
                     * presentadas por los clientes, así como generar estadísticas sobre ellas.
                     */
                    @Service
                    @Transactional
                    @RequiredArgsConstructor
                    public class QuejaSugerenciaServiceImple implements QuejaSugerenciaService {

                        /**
                         * Repositorio para el acceso y persistencia de quejas y sugerencias
                         */
                        private final QuejaSugerenciaRepo quejaSugerenciaRepo;

                        /**
                         * Servicio para la gestión de usuarios
                         */
                        private final UsuarioService usuarioService;

                        /**
                         * Registra una nueva queja o sugerencia en el sistema
                         *
                         * Este método permite crear un nuevo registro de PQRS ya sea asociado a un
                         * cliente registrado o de manera anónima dependiendo de si se proporciona
                         * un ID de cliente.
                         *
                         * @param enviarQuejaSugerenciaDTO Datos necesarios para registrar la queja/sugerencia
                         * @return Identificador único de la queja/sugerencia creada
                         * @throws Exception Si ocurre algún error durante el proceso de registro
                         */
                        @Override
                        public String registrarQuejaSugerencia(EnviarQuejaSugerenciaDTO enviarQuejaSugerenciaDTO) throws Exception {
                            QuejaSugerencia quejaSugerencia = null;
                            if(enviarQuejaSugerenciaDTO.cliente() != null && !enviarQuejaSugerenciaDTO.cliente().isEmpty()){
                                Usuario usuario = usuarioService.obtenerUsuario(enviarQuejaSugerenciaDTO.cliente());

                                quejaSugerencia = QuejaSugerencia.builder()
                                        .tipoPQRS(TipoPQRS.valueOf(enviarQuejaSugerenciaDTO.tipo()))
                                        .usuario( usuario )
                                        .descripcion(enviarQuejaSugerenciaDTO.descripcion())
                                        .estadoPQRS(EstadoPQRS.PENDIENTE)
                                        .fechaEnvio(LocalDateTime.now())
                                        .build();
                            }else{
                                quejaSugerencia = QuejaSugerencia.builder()
                                        .tipoPQRS(TipoPQRS.valueOf(enviarQuejaSugerenciaDTO.tipo()))
                                        .descripcion(enviarQuejaSugerenciaDTO.descripcion())
                                        .estadoPQRS(EstadoPQRS.PENDIENTE)
                                        .fechaEnvio(LocalDateTime.now())
                                        .build();
                            }

                            return quejaSugerenciaRepo.save(quejaSugerencia).getId();
                        }

                        /**
                         * Actualiza el estado de una queja/sugerencia y registra su respuesta
                         *
                         * Permite al personal administrativo responder a una queja o sugerencia
                         * y cambiar su estado (por ejemplo, de PENDIENTE a RESUELTA).
                         *
                         * @param id Identificador único de la queja/sugerencia
                         * @param estado Nuevo estado a asignar
                         * @param respuesta Texto de respuesta a la queja/sugerencia
                         * @return Identificador único de la queja/sugerencia actualizada
                         * @throws Exception Si la queja/sugerencia no existe
                         */
                        @Override
                        public String actualizarEstado(String id, String estado, String respuesta) throws Exception {
                            Optional<QuejaSugerencia> optionalQueja = quejaSugerenciaRepo.findById(id);
                            if (optionalQueja.isPresent()) {
                                QuejaSugerencia queja = optionalQueja.get();
                                queja.setEstadoPQRS(EstadoPQRS.valueOf(estado));
                                queja.setRespuesta(respuesta);
                                queja.setFechaRespuesta(LocalDateTime.now());
                                return quejaSugerenciaRepo.save(queja).getId();
                            } else {
                                throw new Exception("Queja o sugerencia no encontrada");
                            }
                        }

                        /**
                         * Recupera todas las quejas y sugerencias registradas en el sistema
                         *
                         * @return Lista de objetos QuejaSugerenciaDTO con la información de todas las PQRS
                         */
                        @Override
                        public List<QuejaSugerenciaDTO> obtenerTodas() {
                            return quejaSugerenciaRepo.findAll()
                                    .stream()
                                    .map(quejaSugerencia -> new QuejaSugerenciaDTO(
                                            quejaSugerencia.getId(),
                                            quejaSugerencia.getTipoPQRS().toString(),
                                            quejaSugerencia.getUsuario().getId(),
                                            quejaSugerencia.getDescripcion(),
                                            quejaSugerencia.getEstadoPQRS().toString(),
                                            quejaSugerencia.getRespuesta(),
                                            quejaSugerencia.getFechaEnvio(),
                                            quejaSugerencia.getFechaRespuesta()
                                    ))
                                    .collect(Collectors.toList());
                        }

                        /**
                         * Genera un reporte con el número de quejas agrupadas por tipo (queja, reclamo, sugerencia)
                         *
                         * @return Lista de objetos QuejasPorTipoDTO con las estadísticas por tipo de PQRS
                         */
                        @Override
                        public List<QuejasPorTipoDTO> obtenerQuejasPorTipo() {
                            return quejaSugerenciaRepo.contarQuejasPorTipo();
                        }

                        /**
                         * Genera un reporte con la cantidad de quejas presentadas por cada cliente
                         *
                         * @return Lista de objetos QuejasPorClienteDTO con las estadísticas por cliente
                         */
                        @Override
                        public List<QuejasPorClienteDTO> obtenerQuejasPorCliente() {
                            return quejaSugerenciaRepo.contarQuejasPorUsuario();
                        }

                        /**
                         * Obtiene estadísticas de las quejas/sugerencias de un cliente específico agrupadas por tipo
                         *
                         * @param cliente Identificador único del cliente
                         * @return Lista de objetos QuejasPorTipoDTO con las estadísticas del cliente
                         */
                        @Override
                        public List<QuejasPorTipoDTO> obtenerQuejasPorClienteYTipo(String cliente) {
                            return quejaSugerenciaRepo.contarQuejasPorUsuarioYTipo(cliente);
                        }

                        /**
                         * Obtiene la lista de todos los tipos de PQRS disponibles en el sistema
                         *
                         * @return Lista de cadenas con los nombres de los tipos de PQRS
                         */
                        @Override
                        public List<String> obtenerTipoPqrs() {
                            return Arrays.stream(TipoPQRS.values())
                                    .map(Enum::name)
                                    .collect(Collectors.toList());
                        }

                        /**
                         * Obtiene la lista de todos los estados posibles para una PQRS
                         *
                         * @return Lista de cadenas con los nombres de los estados
                         */
                        @Override
                        public List<String> obtenerEstadoPqrs() {
                            return Arrays.stream(EstadoPQRS.values())
                                    .map(Enum::name)
                                    .collect(Collectors.toList());
                        }
                    }