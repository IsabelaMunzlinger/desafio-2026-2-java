package com.example.demo.model;

import com.example.demo.model.enums.Perfil;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

// Cadastro das etapas dos fluxos e dos reposnáveis

@Entity
@Audited
public class EtapaFluxo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private Documento tipoDocumento;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(nullable = false)
    private Integer ordem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfilResponsavel;

    public EtapaFluxo() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Documento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(Documento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public Perfil getPerfilResponsavel() {
        return perfilResponsavel;
    }

    public void setPerfilResponsavel(Perfil perfilResponsavel) {
        this.perfilResponsavel = perfilResponsavel;
    }
}