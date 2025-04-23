package ar.edu.utn.frbb.tup.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frbb.tup.controllers.dto.CuentaBancariaDto;
import ar.edu.utn.frbb.tup.controllers.dto.DepositoRetiroDto;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.SaldoNoValidoException;
import ar.edu.utn.frbb.tup.service.CuentaBancariaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cuenta")
public class CuentaBancariaController {
    
    @Autowired
    private CuentaBancariaService cuentaBancariaService;
    
    @GetMapping
    public ResponseEntity<List<CuentaBancaria>> obtenerAllCuentas() {
        List<CuentaBancaria> cuentas = cuentaBancariaService.obtenerAllCuentas();
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCuenta(@PathVariable("id") long id) {
        try {
            CuentaBancaria cuenta = cuentaBancariaService.obtenerCuentaPorId(id);
            return ResponseEntity.ok(cuenta);
        } catch (CuentaNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> crearCuenta(@Valid @RequestBody CuentaBancariaDto cuentaBancariaDto) {
        try {
            CuentaBancaria cuenta = cuentaBancariaService.crearCuenta(cuentaBancariaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(cuenta);
        } catch (ClienteNoExisteException | CuentaAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SaldoNoValidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/deposito")
    public ResponseEntity<?> agregarDeposito(@PathVariable("id") long id, 
                                          @Valid @RequestBody DepositoRetiroDto depositoRetiroDto) {
        try {
            CuentaBancaria cuenta = cuentaBancariaService.agregarDeposito(id, depositoRetiroDto);
            return ResponseEntity.ok(cuenta);
        } catch (CuentaNoExisteException | ClienteNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SaldoNoValidoException | MonedaNoCoincideException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/retiro")
    public ResponseEntity<?> agregarRetiro(@PathVariable("id") long id, 
                                         @Valid @RequestBody DepositoRetiroDto depositoRetiroDto) {
        try {
            CuentaBancaria cuenta = cuentaBancariaService.agregarRetiro(id, depositoRetiroDto);
            return ResponseEntity.ok(cuenta);
        } catch (CuentaNoExisteException | ClienteNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SaldoNoValidoException | MonedaNoCoincideException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/transacciones")
    public ResponseEntity<?> obtenerTransacciones(@PathVariable("id") long id) {
        try {
            List<Transacciones> transacciones = cuentaBancariaService.obtenerTransaccionesPorId(id);
            return ResponseEntity.ok(transacciones);
        } catch (CuentaNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/transferencias")
    public ResponseEntity<?> obtenerTransferencias(@PathVariable("id") long id) {
        try {
            List<Transferencias> transferencias = cuentaBancariaService.obtenerTransferenciasPorId(id);
            return ResponseEntity.ok(transferencias);
        } catch (CuentaNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarCuenta(@PathVariable("id") long id) {
        try {
            cuentaBancariaService.borrarCuenta(id);
            return ResponseEntity.ok("La cuenta se borró con éxito");
        } catch (CuentaNoExisteException | ClienteNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}