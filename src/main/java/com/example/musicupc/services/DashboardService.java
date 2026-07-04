package com.example.musicupc.services;

import com.example.musicupc.dtos.DashboardDTO;
import com.example.musicupc.dtos.DashboardReservaDTO;
import com.example.musicupc.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.example.musicupc.dtos.DashboardPagoDTO;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ArtistaRepository artistaRepository;
    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final PagoSuscripcionRepository pagoSuscripcionRepository;
    private final SuscripcionRepository suscripcionRepository;

    public DashboardService(UsuarioRepository usuarioRepository, ArtistaRepository artistaRepository, ReservaRepository reservaRepository, PagoRepository pagoRepository, PagoSuscripcionRepository pagoSuscripcionRepository,
            SuscripcionRepository suscripcionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.artistaRepository = artistaRepository;
        this.reservaRepository = reservaRepository;
        this.pagoRepository = pagoRepository;
        this.pagoSuscripcionRepository = pagoSuscripcionRepository;
        this.suscripcionRepository = suscripcionRepository;
    }

    public DashboardDTO obtenerResumen(String periodo) {
        LocalDateTime inicio = calcularInicio(periodo);
        LocalDateTime fin = LocalDateTime.now();

        BigDecimal ingresosReservas =
                pagoRepository.sumarIngresosReservasPorPeriodo(inicio, fin);

        BigDecimal ingresosSuscripciones =
                pagoSuscripcionRepository.sumarIngresosSuscripcionesPorPeriodo(inicio, fin);

        List<DashboardReservaDTO> ultimasReservas = reservaRepository
                .findTop5ByFechaCreacionBetweenOrderByFechaCreacionDesc(inicio, fin)
                .stream()
                .map(r -> new DashboardReservaDTO(
                        r.getId(),
                        r.getCliente().getNombre() + " " + r.getCliente().getApellido(),
                        r.getFechaEvento(),
                        r.getHoraEvento(),
                        r.getEstado(),
                        r.getPrecioTotal()
                ))
                .toList();

        List<DashboardPagoDTO> pagosReservas = pagoRepository
                .findTop5ByFechaCreacionBetweenOrderByFechaCreacionDesc(inicio, fin)
                .stream()
                .map(p -> new DashboardPagoDTO(
                        p.getId(),
                        "Reserva #" + p.getReserva().getId(),
                        "RESERVA",
                        p.getMetodo(),
                        p.getEstado(),
                        p.getMonto(),
                        p.getFechaPago() != null ? p.getFechaPago() : p.getFechaCreacion()
                ))
                .toList();

        List<DashboardPagoDTO> pagosSuscripciones = pagoSuscripcionRepository
                .findTop5ByFechaCreacionBetweenOrderByFechaCreacionDesc(inicio, fin)
                .stream()
                .map(p -> new DashboardPagoDTO(
                        p.getId(),
                        "Suscripción " + p.getTipoPlan(),
                        "SUSCRIPCION",
                        p.getMetodo(),
                        p.getEstado(),
                        p.getMonto(),
                        p.getFechaPago() != null ? p.getFechaPago() : p.getFechaCreacion()
                ))
                .toList();

        List<DashboardPagoDTO> ultimosPagos = Stream
                .concat(pagosReservas.stream(), pagosSuscripciones.stream())
                .sorted(
                        Comparator.comparing(
                                (DashboardPagoDTO p) -> p.getFechaPago() != null
                                        ? p.getFechaPago()
                                        : LocalDateTime.MIN
                        ).reversed()
                )
                .limit(5)
                .toList();

        return new DashboardDTO(
                usuarioRepository.count(),
                artistaRepository.count(),

                reservaRepository.countByFechaCreacionBetween(inicio, fin),
                reservaRepository.countByEstadoIgnoreCaseAndFechaCreacionBetween("PAGADO", inicio, fin),
                reservaRepository.countByEstadoIgnoreCaseAndFechaCreacionBetween("PENDING", inicio, fin),

                pagoRepository.countByFechaCreacionBetween(inicio, fin),
                pagoSuscripcionRepository.countByFechaCreacionBetween(inicio, fin),

                suscripcionRepository.countByEstadoIgnoreCase("PAGADA"),
                suscripcionRepository.countByEstadoIgnoreCase("PENDIENTE"),

                ingresosReservas,
                ingresosSuscripciones,
                ingresosReservas.add(ingresosSuscripciones),

                ultimasReservas,
                ultimosPagos
        );
    }

    private LocalDateTime calcularInicio(String periodo) {
        LocalDate hoy = LocalDate.now();

        return switch (periodo.toLowerCase()) {
            case "hoy" -> hoy.atStartOfDay();
            case "mes" -> hoy.withDayOfMonth(1).atStartOfDay();
            case "anio" -> hoy.withDayOfYear(1).atStartOfDay();
            default -> LocalDateTime.of(2000, 1, 1, 0, 0);
        };
    }
}