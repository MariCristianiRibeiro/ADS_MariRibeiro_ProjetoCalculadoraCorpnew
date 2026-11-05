package br.upf.projectcalccorp.model;

public enum StatusAgendamento {
    AGENDADO("Agendado"),
    SOLICITADO("Solicitado"),
    CONFIRMADO("Confirmado"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}