package com.example.musicupc.dtos;

import com.example.musicupc.entities.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionDTORequest {

    private Long id;
    private String plan_type;
    private double price;
    private LocalDate start_date;
    private LocalDate end_date;
    private String status;
    private LocalDate timestamp;

}
