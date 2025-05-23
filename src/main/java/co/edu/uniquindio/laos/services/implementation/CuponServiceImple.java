package co.edu.uniquindio.laos.services.implementation;

        import co.edu.uniquindio.laos.dto.cupon.CrearCuponDTO;
        import co.edu.uniquindio.laos.dto.cupon.CuponDTO;
        import co.edu.uniquindio.laos.dto.cupon.EditarCuponDTO;
        import co.edu.uniquindio.laos.exceptions.RecursoEncontradoException;
        import co.edu.uniquindio.laos.exceptions.RecursoNoEncontradoException;
        import co.edu.uniquindio.laos.model.Cupon;
        import co.edu.uniquindio.laos.model.EstadoCupon;
        import co.edu.uniquindio.laos.repositories.CuponRepo;
        import co.edu.uniquindio.laos.services.interfaces.CuponService;
        import co.edu.uniquindio.laos.utils.TextUtils;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;
        import java.util.List;
        import java.util.Optional;
        import java.util.Random;
        import java.util.stream.Collectors;

        /**
         * Implementación del servicio de cupones que maneja la lógica de negocio
         * para la gestión de cupones de descuento en el sistema.
         * Esta clase se encarga de crear, editar y eliminar cupones, así como
         * consultar información de cupones según diferentes criterios.
         */
        @Service
        @Transactional
        @RequiredArgsConstructor
        public class CuponServiceImple implements CuponService {

            /**
             * Repositorio para el acceso y persistencia de cupones en la base de datos
             */
            private final CuponRepo cuponRepo;

            /**
             * Crea un nuevo cupón de descuento en el sistema
             * @param crearCuponDTO Datos necesarios para crear el cupón
             * @return Mensaje de confirmación de la creación
             * @throws Exception Si ya existe un cupón con el mismo código
             */
            @Override
            public String crearCupon(CrearCuponDTO crearCuponDTO) throws Exception {

                Optional<Cupon> cuponExistente = cuponRepo.findByCodigoAndEstadoNot(crearCuponDTO.codigo(), EstadoCupon.ELIMINADO);

                if (cuponExistente.isPresent()) {
                    throw new RecursoEncontradoException("Ya existe un cupón con el código ingresado");
                }
                Cupon cupon = Cupon.builder()
                        .codigo(TextUtils.normalizarTexto(crearCuponDTO.codigo()))
                        .nombre(TextUtils.normalizarTexto(crearCuponDTO.nombre()))
                        .porcentajeDescuento(crearCuponDTO.porcentajeDescuento())
                        .estadoCupon(EstadoCupon.ACTIVO)
                        .fechaVencimiento(crearCuponDTO.fechaVencimiento())
                        .build();

                cuponRepo.save(cupon);

                return "Cupon creado exitosamente";
            }

            /**
             * Actualiza la información de un cupón existente
             * @param editarCuponDTO Datos actualizados del cupón
             * @return Identificador único del cupón actualizado
             * @throws RecursoNoEncontradoException Si el cupón no existe
             */
            @Override
            public String editarCupon(EditarCuponDTO editarCuponDTO) throws RecursoNoEncontradoException {

                Cupon cupon = obtenerCuponPorId(editarCuponDTO.id());

                cupon.setCodigo(editarCuponDTO.codigo());
                cupon.setNombre(editarCuponDTO.nombre());
                cupon.setPorcentajeDescuento(editarCuponDTO.porcentajeDescuento());
                cupon.setEstadoCupon(editarCuponDTO.estadoCupon());
                cupon.setFechaVencimiento(editarCuponDTO.fechaVencimiento());

                cuponRepo.save(cupon);

                return cupon.getId();
            }

            /**
             * Elimina lógicamente un cupón cambiando su estado a ELIMINADO
             * @param idCupon Identificador único del cupón a eliminar
             * @return Mensaje de confirmación de la eliminación
             * @throws RecursoNoEncontradoException Si el cupón no existe
             */
            @Override
            public String eliminarCupon(String idCupon) throws RecursoNoEncontradoException {

                Cupon cupon = obtenerCuponPorId(idCupon);

                cupon.setEstadoCupon(EstadoCupon.ELIMINADO);

                cuponRepo.save(cupon);

                return "Cupón eliminado con éxito.";
            }

            /**
             * Busca un cupón por su código promocional
             * @param codigo Código único del cupón
             * @return Objeto Cupon encontrado
             * @throws RecursoNoEncontradoException Si no existe un cupón con ese código
             */
            @Override
            public Cupon obtenerCuponPorCodigo(String codigo) throws RecursoNoEncontradoException {

                Optional<Cupon> cuponExistente = cuponRepo.findByCodigoAndEstadoNot(codigo, EstadoCupon.ELIMINADO);

                if (cuponExistente.isEmpty()) {
                    throw new RecursoNoEncontradoException("Cupón no encontrado");
                }

                return cuponExistente.get();
            }

            /**
             * Recupera todos los cupones asociados a un usuario específico
             * @param idUsuario Identificador único del usuario
             * @return Lista de cupones del usuario
             */
            @Override
            public List<Cupon> obtenerListaCuponPorIdUsuario(String idUsuario) {
                if (idUsuario.length() != 24) {
                    if (idUsuario.length() < 24) {
                        // Si es más corto, completar con ceros al final
                        idUsuario = String.format("%-24s", idUsuario).replace(' ', '0');
                    } else {
                        // Si es más largo, recortar a 24 caracteres
                        idUsuario = idUsuario.substring(0, 24);
                    }
                }
                return cuponRepo.findByUsuarioIdAndEstadoNot(idUsuario, EstadoCupon.ELIMINADO);

            }

            /**
             * Busca un cupón específico por su código y el usuario al que pertenece
             * @param codigo Código del cupón
             * @param idUsuario Identificador único del usuario
             * @return Objeto Cupon encontrado
             * @throws RecursoNoEncontradoException Si no existe un cupón con ese código para ese usuario
             */
            @Override
            public Cupon obtenerCuponPorCodigoYIdUsuario(String codigo, String idUsuario) throws RecursoNoEncontradoException {
                if (idUsuario.length() != 24) {
                    if (idUsuario.length() < 24) {
                        // Si es más corto, completar con ceros al final
                        idUsuario = String.format("%-24s", idUsuario).replace(' ', '0');
                    } else {
                        // Si es más largo, recortar a 24 caracteres
                        idUsuario = idUsuario.substring(0, 24);
                    }
                }
                Optional<Cupon> cuponExistente = cuponRepo.findByCodigoAndIdUsuarioAndEstadoNot(codigo, idUsuario, EstadoCupon.ELIMINADO);

                if (cuponExistente.isEmpty()) {
                    throw new RecursoNoEncontradoException("Cupón no encontrado");
                }

                return cuponExistente.get();
            }

            /**
             * Recupera un cupón por su identificador único
             * @param id Identificador único del cupón
             * @return Objeto Cupon encontrado
             * @throws RecursoNoEncontradoException Si no existe un cupón con ese ID
             */
            @Override
            public Cupon obtenerCuponPorId(String id) throws RecursoNoEncontradoException {
                if (id.length() != 24) {
                    if (id.length() < 24) {
                        // Si es más corto, completar con ceros al final
                        id = String.format("%-24s", id).replace(' ', '0');
                    } else {
                        // Si es más largo, recortar a 24 caracteres
                        id = id.substring(0, 24);
                    }
                }
                Optional<Cupon> cuponExistente = cuponRepo.findByIdAndEstadoNot(id, EstadoCupon.ELIMINADO);

                if (cuponExistente.isEmpty()) {
                    throw new RecursoNoEncontradoException("Cupón no encontrado");
                }

                return cuponExistente.get();
            }

            /**
             * Recupera un cupón por su identificador único y lo convierte a formato DTO
             * @param id Identificador único del cupón
             * @return Objeto CuponDTO con los datos del cupón encontrado
             * @throws RecursoNoEncontradoException Si no existe un cupón con ese ID o está eliminado
             */
            @Override
            public CuponDTO obtenerCuponPorIdParaAdmin(String id) throws RecursoNoEncontradoException {
                if (id.length() != 24) {
                    if (id.length() < 24) {
                        // Si es más corto, completar con ceros al final
                        id = String.format("%-24s", id).replace(' ', '0');
                    } else {
                        // Si es más largo, recortar a 24 caracteres
                        id = id.substring(0, 24);
                    }
                }
                Optional<Cupon> cuponExistente = cuponRepo.findByIdAndEstadoNot(id, EstadoCupon.ELIMINADO);

                if (cuponExistente.isEmpty()) {
                    throw new RecursoNoEncontradoException("Cupón no encontrado");
                }

                Cupon cupon = cuponExistente.get();

                // Mapear el objeto Cupon a CuponDTO
                return new CuponDTO(
                        cupon.getId(),
                        cupon.getCodigo(),
                        cupon.getNombre(),
                        cupon.getPorcentajeDescuento(),
                        cupon.getEstadoCupon(),
                        cupon.getFechaVencimiento()
                );
            }

            /**
             * Recupera todos los cupones del sistema y los convierte a formato DTO
             * @return Lista de cupones en formato DTO para transferencia de datos
             */
            /**
             * Recupera todos los cupones del sistema que no estén en estado "ELIMINADO" y los convierte a formato DTO
             * @return Lista de cupones en formato DTO para transferencia de datos
             */
            @Override
            public List<CuponDTO> listarCupones() {
                return cuponRepo.findCuponesNoEliminados().stream()
                        .map(cupon -> new CuponDTO(
                                cupon.getId(),
                                cupon.getCodigo(),
                                cupon.getNombre(),
                                cupon.getPorcentajeDescuento(),
                                cupon.getEstadoCupon(),
                                cupon.getFechaVencimiento()
                        ))
                        .collect(Collectors.toList());
            }

            /**
             * Genera un código aleatorio para un nuevo cupón
             * @return Cadena de 6 caracteres alfanuméricos (letras mayúsculas y números)
             */
            @Override
            public String generarCodigoCupon() {

                String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

                StringBuilder codigo = new StringBuilder();

                Random random = new Random();

                for (int i = 0; i < 6; i++) {
                    codigo.append(cadena.charAt(random.nextInt(cadena.length())));
                }

                return codigo.toString();
            }
        }