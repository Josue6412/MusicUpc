package com.example.musicupc.repositories;

import com.example.musicupc.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p")
    BigDecimal sumarIngresosReservas();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.fechaCreacion BETWEEN :inicio AND :fin")
    BigDecimal sumarIngresosReservasPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);

    long countByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}