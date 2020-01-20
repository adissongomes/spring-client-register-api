package br.com.adissongomes.client.service;

import br.com.adissongomes.client.service.model.ClientModel;
import br.com.adissongomes.client.service.model.ClientUpdate;
import org.springframework.data.domain.Page;

public interface ClientService {

    ClientModel salvar(ClientModel client);
    void atualizar(ClientModel client);
    void atualizacaoParcial(Long id, ClientUpdate client);
    void remover(Long id);
    ClientModel buscaPorId(Long id);
    Page<ClientModel> busca(String nome, String cpf);

}
