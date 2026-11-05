package br.upf.projectcalccorp.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "agendamento_reuniao")
public class AgendamentoReuniao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "colaborador_id")
    private Colaborador solicitante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sala_id")
    private SalaReuniao sala;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_hora_inicio", nullable = false)
    private Date dataHoraInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_hora_fim", nullable = false)
    private Date dataHoraFim;

    @Column(name = "duracao_horas", nullable = false)
    private Double duracaoHoras;

    @Column(name = "custo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal custoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAgendamento status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoReuniao tipo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "agendamento_recurso",
        joinColumns = @JoinColumn(name = "agendamento_id"),
        inverseJoinColumns = @JoinColumn(name = "recurso_id")
    )
    private List<RecursoTecnologico> recursos = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "agendamento_participante",
        joinColumns = @JoinColumn(name = "agendamento_id"),
        inverseJoinColumns = @JoinColumn(name = "colaborador_id")
    )
    private List<Colaborador> participantes = new ArrayList<>();

    public AgendamentoReuniao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Colaborador getSolicitante() { return solicitante; }
    public void setSolicitante(Colaborador solicitante) { this.solicitante = solicitante; }

    public SalaReuniao getSala() { return sala; }
    public void setSala(SalaReuniao sala) { this.sala = sala; }

    public Date getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(Date dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }

    public Date getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(Date dataHoraFim) { this.dataHoraFim = dataHoraFim; }

    public Double getDuracaoHoras() { return duracaoHoras; }
    public void setDuracaoHoras(Double duracaoHoras) { this.duracaoHoras = duracaoHoras; }

    public BigDecimal getCustoTotal() { return custoTotal; }
    public void setCustoTotal(BigDecimal custoTotal) { this.custoTotal = custoTotal; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }

    public TipoReuniao getTipo() { return tipo; }
    public void setTipo(TipoReuniao tipo) { this.tipo = tipo; }

    public List<RecursoTecnologico> getRecursos() { return recursos; }
    public void setRecursos(List<RecursoTecnologico> recursos) { this.recursos = recursos; }

    public List<Colaborador> getParticipantes() { return participantes; }
    public void setParticipantes(List<Colaborador> participantes) { this.participantes = participantes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendamentoReuniao that = (AgendamentoReuniao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}