package br.com.adissongomes.client.controller.response;

import br.com.adissongomes.client.service.model.ClientModel;

import java.util.List;

public class PaginaResponse {

    private List<ClientModel> clients;
    private int pagina;
    private int quantidadeItensPagina;
    private int totalPaginas;

    public List<ClientModel> getClients() {
        return clients;
    }

    public void setClients(List<ClientModel> clients) {
        this.clients = clients;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public int getQuantidadeItensPagina() {
        return quantidadeItensPagina;
    }

    public void setQuantidadeItensPagina(int quantidadeItensPagina) {
        this.quantidadeItensPagina = quantidadeItensPagina;
    }
}
