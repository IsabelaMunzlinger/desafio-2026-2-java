package com.example.demo.repository;

import com.example.demo.model.Status;
import com.example.demo.model.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findByPerfilPermitidoIsNullOrPerfilPermitido(Perfil perfil);
}