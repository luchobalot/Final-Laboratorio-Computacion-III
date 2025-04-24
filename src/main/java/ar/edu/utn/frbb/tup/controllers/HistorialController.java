package ar.edu.utn.frbb.tup.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frbb.tup.controllers.dto.HistorialTransaccionDto;
import ar.edu.utn.frbb.tup.controllers.dto.TransaccionResumenDto;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.service.CuentaBancariaService;

@RestController
@RequestMapping("/api")
public class HistorialController {
    
    @Autowired
    private CuentaBancariaService cuentaBancariaService;
    
    @GetMapping("/cuenta/{cuentaId}/transacciones")
    public ResponseEntity<?> obtenerHistorialTransacciones(@PathVariable("cuentaId") long cuentaId) {
        try {
            CuentaBancaria cuenta = cuentaBancariaService.obtenerCuentaPorId(cuentaId);
            List<Transacciones> transacciones = cuentaBancariaService.obtenerTransaccionesPorId(cuentaId);
            List<Transferencias> transferencias = cuentaBancariaService.obtenerTransferenciasPorId(cuentaId);
            
            HistorialTransaccionDto historialDto = new HistorialTransaccionDto();
            historialDto.setNumeroCuenta(String.valueOf(cuentaId));
            
            List<TransaccionResumenDto> listaTransacciones = new ArrayList<>();
            
            for (Transacciones t : transacciones) {
                TransaccionResumenDto resumen = new TransaccionResumenDto();
                resumen.setFecha(t.getFecha().toString());
                resumen.setTipo(t.getTipo().toString());
                resumen.setDescripcionBreve(t.getDescripcion());
                resumen.setMonto(t.getMonto());
                listaTransacciones.add(resumen);
            }
            
            for (Transferencias t : transferencias) {
                TransaccionResumenDto resumen = new TransaccionResumenDto();
                resumen.setFecha(t.getFechaHora().toString());
                
                if (t.getCuentaOrigen() == cuentaId) {
                    resumen.setTipo("DEBITO");
                    resumen.setDescripcionBreve("Transferencia saliente");
                    resumen.setMonto(t.getMonto() + t.getComision());
                } else {
                    resumen.setTipo("CREDITO");
                    resumen.setDescripcionBreve("Transferencia entrante");
                    resumen.setMonto(t.getMonto());
                }
                
                listaTransacciones.add(resumen);
            }
            
            historialDto.setTransacciones(listaTransacciones);
            
            return ResponseEntity.ok(historialDto);
        } catch (CuentaNoExisteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}