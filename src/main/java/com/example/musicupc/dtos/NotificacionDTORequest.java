package com.example.musicupc.dtos;

import com.example.musicupc.entities.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

public class NotificacionDTORequest {

    private Long id;
    private Long user_id;
    private String title;
    private String message;
    private String type;
    private boolean is_read;
    private LocalDate created_at;


}
