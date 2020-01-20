package br.com.adissongomes.client.service;

import br.com.adissongomes.client.domain.Client;
import br.com.adissongomes.client.exceptions.ClientExistenteException;
import br.com.adissongomes.client.exceptions.ClientInexistenteException;
import br.com.adissongomes.client.exceptions.FalhaOperacaoException;
import br.com.adissongomes.client.repository.ClientRepository;
import br.com.adissongomes.client.service.model.ClientModel;
import br.com.adissongomes.client.service.model.ClientUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTests {

    @Mock
    ClientRepository repository;
    @InjectMocks
    ClientService service = new ClientServiceImpl();

    @Test
    public void novoClientTest() {
        ClientModel client = criaClientModel();
        Mockito.when(repository.save(ArgumentMatchers.any(Client.class)))
                .thenReturn(clientFromModel(client));

        ClientModel clientPersistido = service.salvar(client);

        Mockito.verify(repository).save(ArgumentMatchers.any(Client.class));
        assertEquals(18, clientPersistido.getIdade());
        assertNotNull(clientPersistido.getId());
    }

    @Test
    public void falhaNovoClientTest() {
        ClientModel model = criaClientModel();
        Mockito.when(repository.save(ArgumentMatchers.any(Client.class)))
                .thenThrow(new DataRetrievalFailureException(""));
        assertThrows(FalhaOperacaoException.class, () -> service.salvar(model));
    }

    @Test
    public void conflitoNovoClientTest() {
        ClientModel model = criaClientModel();
        Mockito.when(repository.findByCpf(anyString())).thenReturn(Optional.of(clientFromModel(model)));
        assertThrows(ClientExistenteException.class, () -> service.salvar(model));
    }

    @Test
    public void atualizarClientTest() {
        ClientModel model = criaClientModel();
        model.setId(1L);
        Mockito.when(repository.findById(model.getId())).thenReturn(Optional.of(clientFromModel(model)));

        service.atualizar(model);

        Mockito.verify(repository).findById(anyLong());
        Mockito.verify(repository).save(any(Client.class));
    }

    @Test
    public void atualizarClientInexistenteTest() {
        ClientModel model = criaClientModel();
        model.setId(1L);
        Mockito.when(repository.findById(model.getId())).thenReturn(Optional.empty());
        assertThrows(ClientInexistenteException.class, () -> service.atualizar(model));
    }

    @Test
    public void atualizarClientSemIdTest() {
        ClientModel model = criaClientModel();
        assertThrows(IllegalArgumentException.class, () -> service.atualizar(model));
    }

    @Test
    public void falhaAtualizarClientTest() {
        ClientModel model = criaClientModel();
        model.setId(1L);
        Mockito.when(repository.findById(anyLong())).thenThrow(new DataRetrievalFailureException(""));
        assertThrows(FalhaOperacaoException.class, () -> service.atualizar(model));
    }

    @Test
    public void atualizaParcialClientTest() {
        ClientUpdate clienteAtualizado = criaClientUpdate();
        long id = 1L;
        Client clientAntigo = clientFromModel(criaClientModel());
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(clientAntigo));

        service.atualizacaoParcial(id, clienteAtualizado);

        ArgumentCaptor<Client> argumentCaptor = ArgumentCaptor.forClass(Client.class);
        Mockito.verify(repository).findById(anyLong());
        Mockito.verify(repository).save(argumentCaptor.capture());
        assertEquals(clientAntigo.getNome(), argumentCaptor.getValue().getNome());
        assertNotEquals(clientAntigo.getCpf(), argumentCaptor.getValue().getCpf());
    }

    @Test
    public void removerClientTest() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(clientFromModel(criaClientModel())));
        long id = 1L;
        service.remover(id);
        Mockito.verify(repository).deleteById(id);
    }

    @Test
    public void removerClientIdNuloTest() {
        assertThrows(IllegalArgumentException.class, () -> service.remover(null));
    }

    @Test
    public void falhaRemoverClientTest() {
        Mockito.when(repository.findById(anyLong())).thenThrow(new DataRetrievalFailureException(""));
        assertThrows(FalhaOperacaoException.class, () -> service.remover(1L));
    }

    private ClientModel criaClientModel() {
        ClientModel client = new ClientModel();
        client.setCpf("12312312312");
        client.setDataNascimento(LocalDate.now().minus(18, ChronoUnit.YEARS));
        client.setNome("Client 123");
        return client;
    }

    private ClientUpdate criaClientUpdate() {
        ClientUpdate client = new ClientUpdate();
        client.setCpf("11122211122");
        return client;
    }

    private Client clientFromModel(ClientModel model) {
        Client client = new Client();
        client.setId(1L);
        client.setCpf(model.getCpf());
        client.setNome(model.getNome());
        client.setDataNascimento(model.getDataNascimento());
        return client;
    }


}
