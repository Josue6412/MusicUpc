package com.example.musicupc.repositories;
import com.example.musicupc.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT r FROM Reserva r WHERE r.cliente.id = :id")
    List<Reserva> buscarReservasPorUsuario(@Param("id") Long id);
}