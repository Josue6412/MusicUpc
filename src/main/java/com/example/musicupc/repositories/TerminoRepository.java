package com.example.musicupc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.musicupc.entities.Terminos;

public interface TerminoRepository extends JpaRepository<Terminos, Long> {
}
