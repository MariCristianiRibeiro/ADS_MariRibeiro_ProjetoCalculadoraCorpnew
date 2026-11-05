package br.upf.projectcalccorp.model;

public enum TipoReuniao {
    INTERNA("Interna"),
    CLIENTE("Cliente"),
    PRESENCIAL("Presencial"),
    REMOTO("Remoto"),
    HIBRIDO("Híbrido");

    private final String descricao;

    TipoReuniao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}