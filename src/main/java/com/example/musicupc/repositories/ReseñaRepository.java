package com.example.musicupc.repositories;

import com.example.musicupc.entities.Reseña;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReseñaRepository extends JpaRepository<Reseña, Long> {

    List<Reseña> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    @Query("select avg(r.rating) from Reseña r where r.reserva.id = :reservaId")
    Double calcularPromedioRatingPorReserva(@Param("reservaId") Long reservaId);

    boolean existsByReserva_Id(Long reservaId);

    boolean existsByReserva_IdAndIdNot(Long reservaId, Long reseñaId);
}