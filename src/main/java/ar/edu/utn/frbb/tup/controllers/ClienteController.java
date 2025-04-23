package ar.edu.utn.frbb.tup.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frbb.tup.controllers.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controllers.validator.ValidatorCliente;
import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;
import ar.edu.utn.frbb.tup.models.exceptions.EdadNoValidaException;
import ar.edu.utn.frbb.tup.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ValidatorCliente clienteValidator;

    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerAllClientes() {
        List<Cliente> clientes = clienteService.obtenerAllClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<?> obtenerCliente(@PathVariable("dni") long dni) {
        try {
            Cliente cliente = clienteService.obtenerCliente(dni);
            return ResponseEntity.ok(cliente);
        } catch (ClienteNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearCliente(@Valid @RequestBody ClienteDto clienteDto) {
        try {
            clienteValidator.validate(clienteDto); // Validar el ClienteDto
            Cliente cliente = clienteService.crearCliente(clienteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (DataNoValidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ClienteAlreadyExistsException | EdadNoValidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{dni}")
    public ResponseEntity<?> actualizarCliente(@Valid @PathVariable("dni") long dni, @RequestBody ClienteDto clienteDto) {
        try {
            clienteValidator.validate(clienteDto); // Validar el ClienteDto
            Cliente cliente = clienteService.actualizarCliente(dni, clienteDto);
            return ResponseEntity.ok(cliente);
        } catch (DataNoValidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EdadNoValidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ClienteNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<?> borrarCliente(@PathVariable("dni") long dni) {
        try {
            clienteService.borrarCliente(dni);
            return ResponseEntity.ok("El cliente se borró con exito");
        } catch (ClienteNoExisteException | CuentaNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}