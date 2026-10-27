package com.proyecto.inmobiliario.controlador;

import static com.proyecto.inmobiliario.util.Constantes.ACEPTADO;
import static com.proyecto.inmobiliario.util.Constantes.CANCELADO;
import static com.proyecto.inmobiliario.util.Constantes.COMPRADOR;
import static com.proyecto.inmobiliario.util.Constantes.ESTADOS;
import static com.proyecto.inmobiliario.util.Constantes.PENDIENTE;
import static com.proyecto.inmobiliario.util.Constantes.VENDEDOR;
import static com.proyecto.inmobiliario.util.Funciones.md5;
import static com.proyecto.inmobiliario.util.Funciones.tiempoSegunPago;
import static com.proyecto.inmobiliario.util.Funciones.validarParte;

import com.proyecto.inmobiliario.datos.AgenteDatos;
import com.proyecto.inmobiliario.datos.BaseDatos;
import com.proyecto.inmobiliario.datos.CatalogoDatos;
import com.proyecto.inmobiliario.datos.ContraOfertaDatos;
import com.proyecto.inmobiliario.datos.ContratoDatos;
import com.proyecto.inmobiliario.datos.InmuebleDatos;
import com.proyecto.inmobiliario.datos.NotificacionDatos;
import com.proyecto.inmobiliario.datos.OfertaDatos;
import com.proyecto.inmobiliario.datos.PrestamoDatos;
import com.proyecto.inmobiliario.datos.UsuarioDatos;
import com.proyecto.inmobiliario.dto.ClienteReq;
import com.proyecto.inmobiliario.dto.ContraOfertaReq;
import com.proyecto.inmobiliario.dto.Id;
import com.proyecto.inmobiliario.dto.InmuebleReq;
import com.proyecto.inmobiliario.dto.LoginReq;
import com.proyecto.inmobiliario.dto.Mensaje;
import com.proyecto.inmobiliario.dto.OfertaReq;
import com.proyecto.inmobiliario.dto.PrestamoReq;
import com.proyecto.inmobiliario.dto.Sesion;
import com.proyecto.inmobiliario.entidades.Agente;
import com.proyecto.inmobiliario.entidades.Catalogo;
import com.proyecto.inmobiliario.entidades.ContraOferta;
import com.proyecto.inmobiliario.entidades.Contrato;
import com.proyecto.inmobiliario.entidades.Inmueble;
import com.proyecto.inmobiliario.entidades.Negociacion;
import com.proyecto.inmobiliario.entidades.Notificacion;
import com.proyecto.inmobiliario.entidades.Oferta;
import com.proyecto.inmobiliario.entidades.Prestamo;
import com.proyecto.inmobiliario.entidades.Venta;
import com.proyecto.inmobiliario.excepciones.Rechazo;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class InmobiliarioController {

    private final BaseDatos bd;

    public InmobiliarioController(BaseDatos bd) {
        this.bd = bd;
    }

    @PostMapping("/login")
    public Sesion login(@RequestBody LoginReq r) {
        String hash = md5(r.pass());
        return bd.conexion(c -> {
            Sesion s = switch (String.valueOf(r.tipo())) {
                case "agente" -> AgenteDatos.login(c, r.usuario(), hash, r.tipo());
                case COMPRADOR, VENDEDOR -> UsuarioDatos.login(c, r.usuario(), hash, r.tipo());
                default -> throw new Rechazo("Tipo inválido: use comprador, vendedor o agente");
            };
            if (s == null) {
                throw new Rechazo("Usuario o contraseña incorrectos");
            }
            return s;
        });
    }

    @PostMapping("/clientes")
    public Id registrarCliente(@RequestBody ClienteReq r) {
        return bd.conexion(c -> {
            if (UsuarioDatos.idPorUsuario(c, r.usuario()) != null) {
                throw new Rechazo("El usuario ya existe");
            }
            return new Id(UsuarioDatos.insertarCliente(c, r, md5(r.pass())));
        });
    }

    @GetMapping("/tipos-inmueble")
    public List<Catalogo> tiposInmueble() {
        return bd.conexion(CatalogoDatos::tiposInmueble);
    }

    @GetMapping("/tipos-pago")
    public List<Catalogo> tiposPago() {
        return bd.conexion(CatalogoDatos::tiposPago);
    }

    @GetMapping("/caracteristicas")
    public List<Catalogo> caracteristicas() {
        return bd.conexion(CatalogoDatos::caracteristicas);
    }

    @GetMapping("/agentes")
    public List<Agente> agentes(@RequestParam(required = false) String q) {
        String texto = q == null ? "" : q.trim().toUpperCase();
        return bd.conexion(c -> AgenteDatos.buscar(c, texto));
    }

    @GetMapping("/agentes/{id}/ventas")
    public List<Venta> ventasAgente(@PathVariable long id) {
        return bd.conexion(c -> AgenteDatos.ventas(c, id));
    }

    @GetMapping("/agentes/{id}/inmuebles")
    public List<Inmueble> inmueblesAgente(@PathVariable long id) {
        return bd.conexion(c -> InmuebleDatos.porAgente(c, id));
    }

    @GetMapping("/inmuebles")
    public List<Inmueble> buscarInmuebles(@RequestParam(required = false) String direccion,
            @RequestParam(required = false) BigDecimal metrajeMin,
            @RequestParam(required = false) BigDecimal metrajeMax,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        return bd.conexion(c -> InmuebleDatos.buscar(c, direccion, metrajeMin, metrajeMax, precioMin, precioMax));
    }

    @GetMapping("/inmuebles/{id}")
    public Inmueble inmueble(@PathVariable long id) {
        return bd.conexion(c -> {
            Inmueble i = InmuebleDatos.porId(c, id);
            if (i == null) {
                throw new Rechazo("El inmueble no existe");
            }
            return i.conDetalles(InmuebleDatos.detalles(c, id));
        });
    }

    @PostMapping("/inmuebles")
    public Id crearInmueble(@RequestBody InmuebleReq r) {
        return bd.conexion(c -> new Id(InmuebleDatos.insertar(c, r)));
    }

    @GetMapping("/vendedores/{id}/inmuebles")
    public List<Inmueble> inmueblesVendedor(@PathVariable long id) {
        return bd.conexion(c -> InmuebleDatos.porVendedor(c, id));
    }

    @GetMapping("/inmuebles/{id}/ofertas")
    public List<Oferta> ofertasInmueble(@PathVariable long id) {
        return bd.conexion(c -> OfertaDatos.porInmueble(c, id));
    }

    @GetMapping("/compradores/{id}/ofertas")
    public List<Oferta> ofertasComprador(@PathVariable long id) {
        return bd.conexion(c -> OfertaDatos.porComprador(c, id));
    }

    @GetMapping("/ofertas/{id}")
    public Oferta oferta(@PathVariable long id) {
        return bd.conexion(c -> {
            Oferta o = OfertaDatos.porId(c, id);
            if (o == null) {
                throw new Rechazo("La oferta no existe");
            }
            return o;
        });
    }

    @PostMapping("/ofertas")
    public Id crearOferta(@RequestBody OfertaReq r) {
        return bd.conexion(c -> {
            Integer disponibilidad = InmuebleDatos.disponibilidad(c, r.idInmueble());
            if (disponibilidad == null || disponibilidad != 1) {
                throw new Rechazo("El inmueble no está disponible");
            }
            String tiempo = tiempoSegunPago(nombreTipoPago(c, r.idTipoPago()), r.tiempo());
            long id = OfertaDatos.insertar(c, r, tiempo, PENDIENTE);
            Negociacion n = negociacion(c, id);
            NotificacionDatos.insertar(c, n.idVendedor(), n.idAgente(), "Nueva oferta #" + id + " de "
                    + n.comprador() + " por " + r.monto() + " sobre el inmueble " + n.direccion());
            return new Id(id);
        });
    }

    @GetMapping("/ofertas/{id}/contraofertas")
    public List<ContraOferta> contraofertas(@PathVariable long id) {
        return bd.conexion(c -> ContraOfertaDatos.porOferta(c, id));
    }

    @PostMapping("/ofertas/{id}/contraofertas")
    public Id crearContraOferta(@PathVariable long id, @RequestBody ContraOfertaReq r) {
        validarParte(r.origen());
        return bd.conexion(c -> {
            Negociacion n = negociacion(c, id);
            if (!PENDIENTE.equals(n.estado())) {
                throw new Rechazo("La oferta no está pendiente");
            }
            String tipoPago = nombreTipoPago(c, r.idTipoPago());
            String tiempo = tiempoSegunPago(tipoPago, r.tiempo());
            ContraOfertaDatos.cambiarEstadoPorOferta(c, id, CANCELADO, PENDIENTE);
            int prioridad = "Efectivo".equals(tipoPago) ? 1 : 0;
            long idContra = ContraOfertaDatos.insertar(c, id, r, tiempo, prioridad, PENDIENTE);
            NotificacionDatos.insertar(c, n.contraparte(r.origen()), n.idAgente(), "Nueva contraoferta #"
                    + idContra + " del " + r.origen() + " por " + r.monto() + " sobre el inmueble " + n.direccion());
            return new Id(idContra);
        });
    }

    @PostMapping("/ofertas/{id}/aceptar")
    public Id aceptarOferta(@PathVariable long id, @RequestParam String por) {
        validarParte(por);
        return bd.conexion(c -> {
            Negociacion n = negociacion(c, id);
            ContraOferta pendiente = validarRespuesta(c, n, por);
            BigDecimal monto = n.monto();
            long idTipoPago = n.idTipoPago();
            if (pendiente != null) {
                ContraOfertaDatos.cambiarEstado(c, pendiente.id(), ACEPTADO);
                monto = pendiente.monto();
                idTipoPago = pendiente.idTipoPago();
            }
            OfertaDatos.cambiarEstado(c, id, ACEPTADO);
            List<long[]> canceladas = OfertaDatos.otrasDelInmueble(c, n.idInmueble(), id, PENDIENTE);
            for (long[] o : canceladas) {
                NotificacionDatos.insertar(c, o[1], n.idAgente(), "Tu oferta #" + o[0] + " sobre el inmueble "
                        + n.direccion() + " fue cancelada porque se aceptó otra oferta");
            }
            ContraOfertaDatos.cambiarEstadoOtrasOfertasDelInmueble(c, n.idInmueble(), id, CANCELADO, PENDIENTE);
            OfertaDatos.cambiarEstadoOtrasDelInmueble(c, n.idInmueble(), id, CANCELADO, PENDIENTE);
            long idContrato = ContratoDatos.insertar(c, monto, ESTADOS.indexOf(PENDIENTE), idTipoPago, id);
            InmuebleDatos.marcarNoDisponible(c, n.idInmueble());
            NotificacionDatos.insertar(c, n.contraparte(por), n.idAgente(), (pendiente != null
                    ? "La contraoferta #" + pendiente.id()
                    : "La oferta #" + id) + " fue aceptada por el " + por + "; se creó el contrato #" + idContrato
                    + " para el inmueble " + n.direccion());
            return new Id(idContrato);
        });
    }

    @PostMapping("/ofertas/{id}/rechazar")
    public Mensaje rechazarOferta(@PathVariable long id, @RequestParam String por) {
        validarParte(por);
        return bd.conexion(c -> {
            Negociacion n = negociacion(c, id);
            validarRespuesta(c, n, por);
            OfertaDatos.cambiarEstado(c, id, CANCELADO);
            ContraOfertaDatos.cambiarEstadoPorOferta(c, id, CANCELADO, PENDIENTE);
            NotificacionDatos.insertar(c, n.contraparte(por), n.idAgente(), "La oferta #" + id
                    + " sobre el inmueble " + n.direccion() + " fue rechazada por el " + por);
            return new Mensaje("Oferta rechazada");
        });
    }

    @GetMapping("/contratos")
    public List<Contrato> contratos(@RequestParam(required = false) Long idComprador,
            @RequestParam(required = false) Long idVendedor, @RequestParam(required = false) Long idAgente) {
        return bd.conexion(c -> ContratoDatos.buscar(c, idComprador, idVendedor, idAgente));
    }

    @GetMapping("/contratos/{id}")
    public Contrato contrato(@PathVariable long id) {
        return bd.conexion(c -> contrato(c, id));
    }

    @PostMapping("/contratos/{id}/aceptar")
    public Mensaje aceptarContrato(@PathVariable long id) {
        return bd.conexion(c -> {
            Contrato ct = contratoPendiente(c, id);
            if ("Credito".equals(ct.tipoPago())
                    && PrestamoDatos.idPorContratoYEstado(c, id, ESTADOS.indexOf(ACEPTADO)) == null) {
                throw new Rechazo("El pago es a crédito: el contrato requiere un préstamo aceptado");
            }
            ContratoDatos.cambiarEstado(c, id, ESTADOS.indexOf(ACEPTADO));
            UsuarioDatos.sumarVenta(c, ct.idVendedor());
            NotificacionDatos.insertar(c, ct.idComprador(), ct.idAgente(), "El contrato #" + id + " del inmueble "
                    + ct.inmueble() + " fue aceptado por el vendedor");
            return new Mensaje("Contrato aceptado");
        });
    }

    @PostMapping("/contratos/{id}/cancelar")
    public Mensaje cancelarContrato(@PathVariable long id, @RequestParam String por) {
        validarParte(por);
        return bd.conexion(c -> {
            Contrato ct = contratoPendiente(c, id);
            int cancelado = ESTADOS.indexOf(CANCELADO);
            ContratoDatos.cambiarEstado(c, id, cancelado);
            PrestamoDatos.cambiarEstadoPorContrato(c, id, cancelado);
            OfertaDatos.cambiarEstado(c, ct.idOferta(), CANCELADO);
            ContraOfertaDatos.cambiarEstadoPorOferta(c, ct.idOferta(), CANCELADO, ACEPTADO);
            InmuebleDatos.marcarDisponible(c, ct.idInmueble());
            long contraparte = VENDEDOR.equals(por) ? ct.idComprador() : ct.idVendedor();
            NotificacionDatos.insertar(c, contraparte, ct.idAgente(), "El contrato #" + id + " del inmueble "
                    + ct.inmueble() + " fue cancelado por el " + por);
            return new Mensaje("Contrato cancelado");
        });
    }

    @GetMapping("/contratos/{id}/prestamos")
    public List<Prestamo> prestamos(@PathVariable long id) {
        return bd.conexion(c -> PrestamoDatos.porContrato(c, id));
    }

    @PostMapping("/contratos/{id}/prestamos")
    public Id crearPrestamo(@PathVariable long id, @RequestBody PrestamoReq r) {
        return bd.conexion(c -> {
            contratoPendiente(c, id);
            return new Id(PrestamoDatos.insertar(c, id, r, ESTADOS.indexOf(PENDIENTE)));
        });
    }

    @PostMapping("/prestamos/{id}/aceptar")
    public Mensaje aceptarPrestamo(@PathVariable long id) {
        return cambiarEstadoPrestamo(id, ACEPTADO);
    }

    @PostMapping("/prestamos/{id}/cancelar")
    public Mensaje cancelarPrestamo(@PathVariable long id) {
        return cambiarEstadoPrestamo(id, CANCELADO);
    }

    private Mensaje cambiarEstadoPrestamo(long id, String estado) {
        return bd.conexion(c -> {
            Prestamo p = PrestamoDatos.porId(c, id);
            if (p == null) {
                throw new Rechazo("El préstamo no existe");
            }
            if (!PENDIENTE.equals(p.estado())) {
                throw new Rechazo("El préstamo no está pendiente");
            }
            PrestamoDatos.cambiarEstado(c, id, ESTADOS.indexOf(estado));
            return new Mensaje("Préstamo " + estado);
        });
    }

    @GetMapping("/notificaciones")
    public List<Notificacion> notificaciones(@RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idAgente) {
        return bd.conexion(c -> NotificacionDatos.buscar(c, idUsuario, idAgente));
    }

    @PutMapping("/notificaciones/{id}/leida")
    public Mensaje marcarLeida(@PathVariable long id) {
        return bd.conexion(c -> {
            NotificacionDatos.marcarLeida(c, id);
            return new Mensaje("Notificación leída");
        });
    }

    @ExceptionHandler(Rechazo.class)
    public ResponseEntity<Mensaje> rechazo(Rechazo e) {
        return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mensaje> error(Exception e) {
        return ResponseEntity.internalServerError().body(new Mensaje(e.getMessage()));
    }

    private static Negociacion negociacion(Connection c, long idOferta) throws SQLException {
        Negociacion n = OfertaDatos.negociacion(c, idOferta);
        if (n == null) {
            throw new Rechazo("La oferta no existe");
        }
        return n;
    }

    private static String nombreTipoPago(Connection c, Long idTipoPago) throws SQLException {
        String nombre = CatalogoDatos.nombreTipoPago(c, idTipoPago);
        if (nombre == null) {
            throw new Rechazo("La forma de pago no existe");
        }
        return nombre;
    }

    private static ContraOferta validarRespuesta(Connection c, Negociacion n, String por) throws SQLException {
        if (!PENDIENTE.equals(n.estado())) {
            throw new Rechazo("La oferta no está pendiente");
        }
        ContraOferta pendiente = ContraOfertaDatos.porOfertaYEstado(c, n.idOferta(), PENDIENTE);
        if (pendiente != null && pendiente.origen().equals(por)) {
            throw new Rechazo("La contraoferta #" + pendiente.id() + " debe responderla la contraparte");
        }
        if (pendiente == null && !VENDEDOR.equals(por)) {
            throw new Rechazo("La oferta debe responderla el vendedor");
        }
        return pendiente;
    }

    private static Contrato contrato(Connection c, long id) throws SQLException {
        Contrato ct = ContratoDatos.porId(c, id);
        if (ct == null) {
            throw new Rechazo("El contrato no existe");
        }
        return ct;
    }

    private static Contrato contratoPendiente(Connection c, long id) throws SQLException {
        Contrato ct = contrato(c, id);
        if (!PENDIENTE.equals(ct.estado())) {
            throw new Rechazo("El contrato no está pendiente");
        }
        return ct;
    }
}
