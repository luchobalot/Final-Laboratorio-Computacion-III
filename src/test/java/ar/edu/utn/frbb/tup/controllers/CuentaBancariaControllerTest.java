package ar.edu.utn.frbb.tup.controllers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

@ExtendWith(MockitoExtension.class)
public class CuentaBancariaControllerTest {
    
    @Mock
    private CuentaBancariaService cuentaBancariaService;

    @InjectMocks
    private CuentaBancariaController cuentaBancariaController;

    @Test
    public void testObtenerCuenta() throws CuentaNoExisteException {
        long id = 1222333;
        CuentaBancaria cuentaExpected = new CuentaBancaria();

        when(cuentaBancariaService.obtenerCuentaPorId(id)).thenReturn(cuentaExpected);

        ResponseEntity<?> response = cuentaBancariaController.obtenerCuenta(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cuentaExpected, response.getBody());
    }

    @Test 
    public void testObtenerTransacciones() throws CuentaNoExisteException {
        long id = 1222333;
        List<Transacciones> transacciones = new ArrayList<>();

        when(cuentaBancariaService.obtenerTransaccionesPorId(id)).thenReturn(transacciones);

        ResponseEntity<?> response = cuentaBancariaController.obtenerTransacciones(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transacciones, response.getBody());
    }

    @Test
    public void testObtenerTransferencias() throws CuentaNoExisteException {
        long id = 1222333;
        List<Transferencias> transferencias = new ArrayList<>();

        when(cuentaBancariaService.obtenerTransferenciasPorId(id)).thenReturn(transferencias);

        ResponseEntity<?> response = cuentaBancariaController.obtenerTransferencias(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transferencias, response.getBody());
    }

    @Test 
    public void testCrearCuentaClienteNoExiste() throws ClienteNoExisteException, CuentaAlreadyExistsException, SaldoNoValidoException {
        CuentaBancariaDto cuentaBancariaDto = new CuentaBancariaDto();

        when(cuentaBancariaService.crearCuenta(cuentaBancariaDto)).thenThrow(new ClienteNoExisteException("Cliente no existe"));
        
        ResponseEntity<?> response = cuentaBancariaController.crearCuenta(cuentaBancariaDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCrearCuentaConCuentaExistente() throws ClienteNoExisteException, CuentaAlreadyExistsException, SaldoNoValidoException {
        CuentaBancariaDto cuentaBancariaDto = new CuentaBancariaDto();

        when(cuentaBancariaService.crearCuenta(cuentaBancariaDto)).thenThrow(new CuentaAlreadyExistsException("Cuenta ya existe"));
        
        ResponseEntity<?> response = cuentaBancariaController.crearCuenta(cuentaBancariaDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCrearCuentaExitoso() throws ClienteNoExisteException, CuentaAlreadyExistsException, SaldoNoValidoException {
        CuentaBancariaDto cuentaBancariaDto = new CuentaBancariaDto();
        CuentaBancaria cuentaExpected = new CuentaBancaria();

        when(cuentaBancariaService.crearCuenta(cuentaBancariaDto)).thenReturn(cuentaExpected);
        
        ResponseEntity<?> response = cuentaBancariaController.crearCuenta(cuentaBancariaDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cuentaExpected, response.getBody());
    }

    @Test 
    public void testAgregarDeposito() throws CuentaNoExisteException, SaldoNoValidoException, ClienteNoExisteException, MonedaNoCoincideException {
        long id = 1222333;
        CuentaBancaria cuentaExpected = new CuentaBancaria();
        DepositoRetiroDto depositoRetiroDto = new DepositoRetiroDto();

        when(cuentaBancariaService.agregarDeposito(id, depositoRetiroDto)).thenReturn(cuentaExpected);

        ResponseEntity<?> response = cuentaBancariaController.agregarDeposito(id, depositoRetiroDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cuentaExpected, response.getBody());
    }

    @Test 
    public void testAgregarRetiro() throws CuentaNoExisteException, SaldoNoValidoException, ClienteNoExisteException, MonedaNoCoincideException {
        long id = 1222333;
        CuentaBancaria cuentaExpected = new CuentaBancaria();
        DepositoRetiroDto depositoRetiroDto = new DepositoRetiroDto();

        when(cuentaBancariaService.agregarRetiro(id, depositoRetiroDto)).thenReturn(cuentaExpected);

        ResponseEntity<?> response = cuentaBancariaController.agregarRetiro(id, depositoRetiroDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cuentaExpected, response.getBody());
    }

    @Test 
    public void testBorrarCuenta() throws CuentaNoExisteException, ClienteNoExisteException {
        long id = 1222333;

        doNothing().when(cuentaBancariaService).borrarCuenta(id);

        ResponseEntity<?> response = cuentaBancariaController.borrarCuenta(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}