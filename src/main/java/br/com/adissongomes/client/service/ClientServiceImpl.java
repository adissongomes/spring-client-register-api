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
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    private ClientRepository repository;

    @Override
    public ClientModel salvar(@Valid ClientModel client) {
        try {
            if (repository.findByCpf(client.getCpf()).isPresent())
                throw new ClientExistenteException("Cliente de CPF " + client.getCpf() + " ja cadastrado");
            LOGGER.info("Criando novo cliente {}", client.getNome());
            Client novoClient = repository.save(toClient(client));
            return toModel(novoClient);
        } catch (DataAccessException e) {
            String msg = "Erro ao criar novo cliente " + client.getNome();
            LOGGER.error(msg, e);
            throw new FalhaOperacaoException(msg, e);
        }
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
    public void atualizar(ClientModel client) {
        if (client == null || client.getId() == null)
            throw new IllegalArgumentException("Id do cliente necessario");

        try {
            LOGGER.info("Atualizando cliente {}" + client.getId());
            Optional<Client> optional = getClientAntigo(client.getId(), client.getCpf());
            if (optional.isPresent()) {
                repository.save(toClient(client));
            } else {
                throw new ClientInexistenteException("Cliente de id " + client.getId() + " nao cadastrado");
            }
        } catch (DataAccessException e) {
            String msg = "Erro ao atualizar cliente " + client.getId();
            LOGGER.error(msg, e);
            throw new FalhaOperacaoException(msg, e);
        }
    }

    @Override
    public void atualizacaoParcial(Long id, ClientUpdate parcial) {
        if (id == null)
            throw new IllegalArgumentException("Id do cliente necessario");

        try {
            LOGGER.info("Atualizando cliente {}" + id);
            Optional<Client> optional = getClientAntigo(id, parcial.getCpf());
            if (optional.isPresent()) {
                Client atualizado = Client.from(optional.get());
                atualizado.setId(id);
                if (StringUtils.hasText(parcial.getCpf()))
                    atualizado.setCpf(parcial.getCpf());
                if (StringUtils.hasText(parcial.getNome()))
                    atualizado.setNome(parcial.getNome());
                if (parcial.getDataNascimento() != null)
                    atualizado.setDataNascimento(parcial.getDataNascimento());
                repository.save(atualizado);
            } else {
                throw new ClientInexistenteException("Cliente de id " + id + " nao cadastrado");
            }
        } catch (DataAccessException e) {
            String msg = "Erro ao atualizar cliente " + id;
            LOGGER.error(msg, e);
            throw new FalhaOperacaoException(msg, e);
        }
    }

    private Optional<Client> getClientAntigo(long id, String novoCpf) {
        Optional<Client> optional = repository.findById(id);
        if (optional.isPresent()) {
            Client antigo = optional.get();
            if (novoCpf != null && !antigo.getCpf().equals(novoCpf)) {
                Optional<Client> byCpf = repository.findByCpf(novoCpf);
                if (byCpf.isPresent())
                    throw new ClientExistenteException("Ja existe cliente (id=" +
                            byCpf.get().getId() + ") com CPF " +
                            novoCpf + " cadastrado");
            }
        }
        return optional;
    }

    @Override
    public void remover(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id do cliente necessario");

        try {
            LOGGER.info("Atualizando cliente {}" + id);
            if (repository.findById(id).isPresent()) {
                repository.deleteById(id);
            } else {
                throw new ClientInexistenteException("Cliente de id " + id + " nao cadastrado");
            }
        } catch (DataAccessException e) {
            String msg = "Erro ao remover cliente " + id;
            LOGGER.error(msg, e);
            throw new FalhaOperacaoException(msg, e);
        }

    }

    @Override
    public ClientModel buscaPorId(Long id) {
        return toModel(repository.findById(id)
                .orElseThrow(() -> new ClientInexistenteException("Cliente " + id + " nao enontrado")));
    }

    @Override
    public Page<ClientModel> busca(String nome, String novoCpf, int page, int size) {
        try {
            Function<Client, ClientModel> mapClientModel = c -> toModel(c);
            if (!StringUtils.hasText(nome) && !StringUtils.hasText(novoCpf)) {
                return repository.findAll(PageRequest.of(page, size))
                        .map(mapClientModel);
            }

            ExampleMatcher matcher = ExampleMatcher.matchingAny()
                    .withMatcher("nome", startsWith().ignoreCase())
                    .withMatcher("cpf", contains())
                    .withIgnoreNullValues();
            Client c = new Client();
            c.setNome(nome);
            c.setCpf(novoCpf);
            return repository.findAll(Example.of(c, matcher), PageRequest.of(page, size)).map(mapClientModel);
        } catch (DataAccessException e) {
            String msg = "Erro ao obter clientes em busca por cpf '" + novoCpf + "' nome '" + nome + "'";
            LOGGER.error(msg, e);
            throw new FalhaOperacaoException(msg, e);
        }
    }
}
