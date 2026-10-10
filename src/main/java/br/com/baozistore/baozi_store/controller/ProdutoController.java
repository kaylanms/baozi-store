package br.com.baozistore.baozi_store.controller;

import br.com.baozistore.baozi_store.model.Produto;
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

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public ProdutoController(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        produto.setId(null);
        Produto salvo = produtoRepository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable("id") Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagar(@PathVariable("id") Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (pedidoRepository.existsByProdutoId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nao e possivel apagar: o produto esta em pedidos registrados.");
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}