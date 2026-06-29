package com.example.musicupc.repositories;

import com.example.musicupc.entities.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    @Query("SELECT s FROM Suscripcion s WHERE s.usuario.id = :usuarioId ORDER BY s.fecha_creacion DESC")
    List<Suscripcion> findByUsuarioIdOrderByFechaCreacionDesc(@Param("usuarioId") Long usuarioId);

    long countByEstadoIgnoreCase(String estado);
}
