package ar.edu.utn.frbb.tup.controllers.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentasIgualesException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoValidaException;

@ExtendWith(MockitoExtension.class)
public class ValidatorTransferenciasTest {

    @InjectMocks
    private ValidatorTransferencias validatorTransferencias;

    private TransferenciasDto transferenciasDto;

    @BeforeEach
    public void configurar() {
        transferenciasDto = new TransferenciasDto();
        transferenciasDto.setCuentaOrigen(11222333);
        transferenciasDto.setCuentaDestino(44555666);
        transferenciasDto.setMonto(1000.0f);
        transferenciasDto.setMoneda("USD");
    }

    @Test
    @DisplayName("Validar transferencia con datos correctos")
    public void testValidarTransferenciaCorrecta() {
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con cuentas iguales")
    public void testValidarTransferenciaCuentasIguales() {
        transferenciasDto.setCuentaDestino(transferenciasDto.getCuentaOrigen());
        
        CuentasIgualesException excepcion = assertThrows(CuentasIgualesException.class, 
            () -> validatorTransferencias.validate(transferenciasDto));
        
        assertEquals("Error: La cuenta de origen y destino no pueden ser la misma.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transferencia con cuenta origen inválida (negativa)")
    public void testValidarTransferenciaCuentaOrigenNegativa() {
        transferenciasDto.setCuentaOrigen(-100);
        
        CuentaNoExisteException excepcion = assertThrows(CuentaNoExisteException.class, 
            () -> validatorTransferencias.validate(transferenciasDto));
        
        assertEquals("Error: El ID de la cuenta origen no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transferencia con cuenta origen inválida (cero)")
    public void testValidarTransferenciaCuentaOrigenCero() {
        transferenciasDto.setCuentaOrigen(0);
        
        CuentaNoExisteException excepcion = assertThrows(CuentaNoExisteException.class, 
            () -> validatorTransferencias.validate(transferenciasDto));
        
        assertEquals("Error: El ID de la cuenta origen no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transferencia con cuenta destino inválida (negativa)")
    public void testValidarTransferenciaCuentaDestinoNegativa() {
        transferenciasDto.setCuentaDestino(-100);
        
        CuentaNoExisteException excepcion = assertThrows(CuentaNoExisteException.class, 
            () -> validatorTransferencias.validate(transferenciasDto));
        
        assertEquals("Error: El ID de la cuenta destino no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transferencia con cuenta destino inválida (cero)")
    public void testValidarTransferenciaCuentaDestinoCero() {
        transferenciasDto.setCuentaDestino(0);
        
        CuentaNoExisteException excepcion = assertThrows(CuentaNoExisteException.class, 
            () -> validatorTransferencias.validate(transferenciasDto));
        
        assertEquals("Error: El ID de la cuenta destino no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transferencia con moneda dólares (USD)")
    public void testValidarTransferenciaMonedaDolares() {
        transferenciasDto.setMoneda("USD");
        
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con moneda dólares ('DOLARES')")
    public void testValidarTransferenciaMonedaDolaresCompleto() {
        transferenciasDto.setMoneda("DOLARES");
        
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con moneda pesos (ARS)")
    public void testValidarTransferenciaMonedaPesos() {
        transferenciasDto.setMoneda("ARS");
        
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con moneda pesos ('PESOS')")
    public void testValidarTransferenciaMonedaPesosCompleto() {
        transferenciasDto.setMoneda("PESOS");
        
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con moneda inválida")
    public void testValidarTransferenciaMonedaInvalida() {
        transferenciasDto.setMoneda("EUR");
        
        MonedaNoValidaException excepcion = assertThrows(MonedaNoValidaException.class, 
            () -> validatorTransferencias.validate(transferenciasDto));
        
        assertEquals("Error: El tipo de moneda debe ser USD o ARS.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transferencia con moneda en minúsculas")
    public void testValidarTransferenciaMonedaMinusculas() {
        transferenciasDto.setMoneda("usd");
        
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con moneda en mayúsculas")
    public void testValidarTransferenciaMonedaMayusculas() {
        transferenciasDto.setMoneda("ARS");
        
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con moneda mixta")
    public void testValidarTransferenciaMonedaMixta() {
        transferenciasDto.setMoneda("UsD");
        
        assertDoesNotThrow(() -> validatorTransferencias.validate(transferenciasDto));
    }

    @Test
    @DisplayName("Validar transferencia con moneda vacía")
    public void testValidarTransferenciaMonedaVacia() {
        transferenciasDto.setMoneda("");
        
        MonedaNoValidaException excepcion = assertThrows(MonedaNoValidaException.class, 
            () -> validatorTransferencias.validate(transferenciasDto));
        
        assertEquals("Error: El tipo de moneda debe ser USD o ARS.", excepcion.getMessage());
    }
}