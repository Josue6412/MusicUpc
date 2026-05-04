package com.example.musicupc.repositories;

import com.example.musicupc.entities.Suscripcion;
import com.example.musicupc.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

}
