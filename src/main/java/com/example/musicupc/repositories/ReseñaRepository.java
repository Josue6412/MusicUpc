package com.example.musicupc.repositories;

import com.example.musicupc.entities.Reseña;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReseñaRepository extends JpaRepository<Reseña, Long> {

    // 1) Listar reseñas de un usuario, ordenadas por fecha de creación descendente
    List<Reseña> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // 2) Calcular el promedio de rating por reserva
    @Query("select avg(r.rating) from Reseña r where r.reserva.id = :reservaId")
    Double calcularPromedioRatingPorReserva(@Param("reservaId") Long reservaId);
}