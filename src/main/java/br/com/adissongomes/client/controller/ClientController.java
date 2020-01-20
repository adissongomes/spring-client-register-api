package br.com.adissongomes.client.controller;

import br.com.adissongomes.client.controller.response.PaginaResponse;
import br.com.adissongomes.client.service.ClientService;
import br.com.adissongomes.client.service.model.ClientModel;
import br.com.adissongomes.client.service.model.ClientUpdate;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.validation.Valid;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity inserir(@RequestBody @Valid ClientModel clientModel) {
        ClientModel novoCliente = service.salvar(clientModel);
        UriComponents uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoCliente.getId());
        return ResponseEntity.created(uri.toUri()).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> busca(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.buscaPorId(id));
    }

    @GetMapping
    public ResponseEntity<?> busca(@RequestParam(name="nome", required = false) String nome,
                                   @RequestParam(name="cpf", required = false) String cpf,
                                   @RequestParam(name="tamanho", defaultValue = "10") int quantidade,
                                   @RequestParam(name="pagina", defaultValue = "1") int pagina) {
        Page<ClientModel> paginaResultado = service.busca(nome, cpf, pagina - 1, quantidade);
        PaginaResponse response = new PaginaResponse();
        response.setClients(paginaResultado.getContent());
        response.setPagina(paginaResultado.getNumber() + 1);
        response.setQuantidadeItensPagina(paginaResultado.getNumberOfElements());
        response.setTotalPaginas(paginaResultado.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualiza(@PathVariable("id") Long id, @RequestBody @Valid ClientModel client) {
        client.setId(id);
        service.atualizar(client);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizaParcial(@PathVariable("id") Long id, @RequestBody ClientUpdate client) {
        service.atualizacaoParcial(id, client);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") Long id) {
        service.remover(id);
        return ResponseEntity.ok().build();
    }
}
