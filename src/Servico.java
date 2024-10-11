package com.mycompany.salaodebeleza;

public class Servico {
    private String descricao;
    private String atendente;
    private double valor;

    public Servico(String descricao, String atendente, double valor) {
        this.descricao = descricao;
        this.atendente = atendente;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAtendente() {
        return atendente;
    }

    public double getValor() {
        return valor;
    }
}
