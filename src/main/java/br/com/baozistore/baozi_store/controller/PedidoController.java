package br.com.baozistore.baozi_store.controller;

import br.com.baozistore.baozi_store.model.Cliente;
import br.com.baozistore.baozi_store.model.Pedido;
import br.com.baozistore.baozi_store.model.Produto;
import br.com.baozistore.baozi_store.repository.ClienteRepository;
import br.com.baozistore.baozi_store.repository.PedidoRepository;
import br.com.baozistore.baozi_store.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null
                || pedido.getProduto() == null || pedido.getProduto().getId() == null) {
            return ResponseEntity.badRequest().body("Informe cliente.id e produto.id no pedido.");
        }
        if (pedido.getQuantidade() == null || pedido.getQuantidade() <= 0) {
            return ResponseEntity.badRequest().body("A quantidade deve ser maior que zero.");
        }

        Optional<Cliente> cliente = clienteRepository.findById(pedido.getCliente().getId());
        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente com id " + pedido.getCliente().getId() + " nao encontrado.");
        }

        Optional<Produto> produto = produtoRepository.findById(pedido.getProduto().getId());
        if (produto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Produto com id " + pedido.getProduto().getId() + " nao encontrado.");
        }
        if (Boolean.FALSE.equals(produto.get().getEstoque())) {
            return ResponseEntity.badRequest().body("Produto sem estoque no momento.");
        }

        pedido.setId(null);
        pedido.setCliente(cliente.get());
        pedido.setProduto(produto.get());

        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable("id") Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable("id") Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
