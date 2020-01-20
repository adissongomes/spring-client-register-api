package br.com.adissongomes.client.service.model;

import java.time.LocalDate;
import java.util.Objects;

public class ClientUpdate {
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientUpdate that = (ClientUpdate) o;
        return cpf.equals(that.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
