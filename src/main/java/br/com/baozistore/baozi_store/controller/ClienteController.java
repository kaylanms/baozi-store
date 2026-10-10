package br.com.baozistore.baozi_store.controller;

import br.com.baozistore.baozi_store.model.Cliente;
import br.com.baozistore.baozi_store.repository.ClienteRepository;
import br.com.baozistore.baozi_store.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteController(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        cliente.setId(null);
        if (cliente.getClienteDesde() == null) {
            cliente.setClienteDesde(LocalDate.now());
        }
        Cliente salvo = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable("id") Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagar(@PathVariable("id") Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (pedidoRepository.existsByClienteId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nao e possivel apagar: o cliente possui pedidos registrados.");
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}