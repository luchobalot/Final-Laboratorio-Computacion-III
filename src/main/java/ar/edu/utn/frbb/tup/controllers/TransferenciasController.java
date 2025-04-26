package ar.edu.utn.frbb.tup.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.models.Recibo;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentasIgualesException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;
import ar.edu.utn.frbb.tup.models.exceptions.SaldoNoValidoException;
import ar.edu.utn.frbb.tup.service.TransferenciasService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transferencias")
public class TransferenciasController {
    
    @Autowired
    private TransferenciasService transferenciasService;
    
    @GetMapping
    public ResponseEntity<List<Transferencias>> obtenerAllTransferencias() {
        List<Transferencias> transferencias = transferenciasService.obtenerAllTransferencias();
        return ResponseEntity.ok(transferencias);
    }
    
    @PostMapping("/api/transfer")
    public ResponseEntity<?> realizarTransferencia(@Valid @RequestBody TransferenciasDto transferenciasDto) {
        try {
            Recibo recibo = transferenciasService.crearTransferencia(transferenciasDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(recibo);
        } catch (CuentasIgualesException | MontoNoValidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (CuentaNoExisteException | ClienteNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SaldoNoValidoException | MonedaNoCoincideException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> crearTransferencia(@Valid @RequestBody TransferenciasDto transferenciasDto) {
        try {
            Recibo recibo = transferenciasService.crearTransferencia(transferenciasDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(recibo);
        } catch (CuentasIgualesException | MontoNoValidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (CuentaNoExisteException | ClienteNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SaldoNoValidoException | MonedaNoCoincideException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}