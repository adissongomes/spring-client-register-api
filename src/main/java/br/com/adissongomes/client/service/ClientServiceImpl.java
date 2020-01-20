package br.com.adissongomes.client.service;

import br.com.adissongomes.client.domain.Client;
import br.com.adissongomes.client.exceptions.ClientExistenteException;
import br.com.adissongomes.client.exceptions.ClientInexistenteException;
import br.com.adissongomes.client.exceptions.FalhaOperacaoException;
import br.com.adissongomes.client.repository.ClientRepository;
import br.com.adissongomes.client.service.model.ClientModel;
import br.com.adissongomes.client.service.model.ClientUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    private ClientRepository repository;

    @Override
    public ClientModel salvar(@Valid ClientModel client) {
        if (repository.findByCpf(client.getCpf()).isPresent())
            throw new ClientExistenteException("Cliente de CPF " + client.getCpf() + " ja cadastrado");
        Client novoClient = null;
        try {
            LOGGER.info("Criando novo cliente {}", client.getNome());
            novoClient = repository.save(toClient(client));
        } catch (Exception e) {
            String msg = "Erro ao criar novo cliente " + client.getId();
            LOGGER.error(msg, e);
            throw new FalhaOperacaoException(msg, e);
        }
        return toModel(novoClient);
    }

    private Client toClient(ClientModel model) {
        if (model == null) return null;
        Client client = new Client();
        client.setNome(model.getNome());
        client.setDataNascimento(model.getDataNascimento());
        client.setCpf(model.getCpf());
        return client;
    }

    private ClientModel toModel(Client client) {
        if (client == null) return null;
        ClientModel model = new ClientModel();
        model.setId(client.getId());
        model.setNome(client.getNome());
        model.setDataNascimento(client.getDataNascimento());
        model.setCpf(client.getCpf());
        model.setIdade(client.getDataNascimento().until(LocalDate.now()).getYears());
        return model;
    }

    @Override
    public boolean atualizar(ClientModel client) {
        if (client.getId() == null)
            throw new IllegalArgumentException("Id do cliente necessario");

        try {
            LOGGER.info("Atualizando cliente {}" + client.getId());
            if (repository.findById(client.getId()).isPresent()) {
                repository.save(toClient(client));
                return true;
            }
        } catch (Exception e) {
            String msg = "Erro ao atualizar cliente " + client.getId();
            LOGGER.error(msg, e);
            throw new FalhaOperacaoException(msg, e);
        }

        throw new ClientInexistenteException("Cliente de id " + client.getId() + " nao cadastrado");
    }

    @Override
    public boolean atualizacaoParcial(Long id, ClientUpdate client) {
        return false;
    }

    @Override
    public boolean remover(Long id) {
        return false;
    }

    @Override
    public ClientModel buscaPorId(Long id) {
        return null;
    }

    @Override
    public Page<ClientModel> busca(String nome, String cpf) {
        return null;
    }
}
