package com.example.musicupc.repositories;

import com.example.musicupc.entities.PagoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoSuscripcionRepository extends JpaRepository<PagoSuscripcion, Long> {
    List<PagoSuscripcion> findBySuscripcionUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoSuscripcion p")
    BigDecimal sumarIngresosSuscripciones();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoSuscripcion p WHERE p.fechaCreacion BETWEEN :inicio AND :fin")
    BigDecimal sumarIngresosSuscripcionesPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                    @Param("fin") LocalDateTime fin);

    long countByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}