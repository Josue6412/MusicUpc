package com.example.musicupc.repositories;

import com.example.musicupc.entities.Terminos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TerminoRepository extends JpaRepository<Terminos, Long> {

    Optional<Terminos> findByReservaId_Id(Long reservaId);

    boolean existsByReservaId_Id(Long reservaId);
}