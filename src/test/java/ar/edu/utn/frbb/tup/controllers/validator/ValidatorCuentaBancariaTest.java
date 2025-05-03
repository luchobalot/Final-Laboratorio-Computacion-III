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

import ar.edu.utn.frbb.tup.controllers.dto.CuentaBancariaDto;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoValidaException;

@ExtendWith(MockitoExtension.class)
public class ValidatorCuentaBancariaTest {

    @InjectMocks
    private ValidatorCuentaBancaria validatorCuentaBancaria;

    private CuentaBancariaDto cuentaBancariaDto;

    @BeforeEach
    public void configurar() {
        cuentaBancariaDto = new CuentaBancariaDto();
        cuentaBancariaDto.setTitular(12345678);
        cuentaBancariaDto.setSaldo(1000);
        cuentaBancariaDto.setTipoCuenta("CC");
        cuentaBancariaDto.setMoneda("USD");
    }

    @Test
    @DisplayName("Validar cuenta bancaria con datos correctos")
    public void testValidarCuentaBancariaCorrecta() {
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    // Pruebas para validacion del titular (DNI)
    @Test
    @DisplayName("Validar cuenta con titular DNI negativo")
    public void testValidarTitularNegativo() {
        cuentaBancariaDto.setTitular(-12345);
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCuentaBancaria.validate(cuentaBancariaDto));
        
        assertEquals("Error: El DNI no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar cuenta con titular DNI cero")
    public void testValidarTitularCero() {
        cuentaBancariaDto.setTitular(0);
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCuentaBancaria.validate(cuentaBancariaDto));
        
        assertEquals("Error: El DNI no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar cuenta con titular DNI demasiado largo")
    public void testValidarTitularLargo() {
        cuentaBancariaDto.setTitular(123456789);
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCuentaBancaria.validate(cuentaBancariaDto));
        
        assertEquals("Error: El DNI debe ser de 8 dígitos o menos.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar cuenta con titular DNI válido máximo")
    public void testValidarTitularValido() {
        cuentaBancariaDto.setTitular(43795841);
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    // Pruebas para validacion de tipo de cuenta
    @Test
    @DisplayName("Validar cuenta tipo CC")
    public void testValidarTipoCuentaCC() {
        cuentaBancariaDto.setTipoCuenta("CC");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta tipo CA")
    public void testValidarTipoCuentaCA() {
        cuentaBancariaDto.setTipoCuenta("CA");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta tipo CUENTA CORRIENTE")
    public void testValidarTipoCuentaCuentaCorriente() {
        cuentaBancariaDto.setTipoCuenta("CUENTA CORRIENTE");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta tipo CAJA DE AHORRO")
    public void testValidarTipoCuentaCajaAhorro() {
        cuentaBancariaDto.setTipoCuenta("CAJA DE AHORRO");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta tipo inválido")
    public void testValidarTipoCuentaInvalido() {
        cuentaBancariaDto.setTipoCuenta("PLAZO FIJO");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCuentaBancaria.validate(cuentaBancariaDto));
        
        assertEquals("Error: PLAZO FIJO no es un tipo válido (CC para CUENTA CORRIENTE o CA para CAJA DE AHORRO)", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar cuenta tipo con minusculas")
    public void testValidarTipoCuentaMinusculas() {
        cuentaBancariaDto.setTipoCuenta("cc");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta tipo con mayúsculas")
    public void testValidarTipoCuentaMayusculas() {
        cuentaBancariaDto.setTipoCuenta("CA");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta tipo con formato mixto")
    public void testValidarTipoCuentaMixta() {
        cuentaBancariaDto.setTipoCuenta("Cc");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    // Pruebas para validación de moneda
    @Test
    @DisplayName("Validar cuenta con moneda USD")
    public void testValidarMonedaUSD() {
        cuentaBancariaDto.setMoneda("USD");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta con moneda ARS")
    public void testValidarMonedaARS() {
        cuentaBancariaDto.setMoneda("ARS");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta con moneda DOLARES")
    public void testValidarMonedaDolares() {
        cuentaBancariaDto.setMoneda("DOLARES");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta con moneda PESOS")
    public void testValidarMonedaPesos() {
        cuentaBancariaDto.setMoneda("PESOS");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta con moneda inválida")
    public void testValidarMonedaInvalida() {
        cuentaBancariaDto.setMoneda("EUR");
        
        MonedaNoValidaException excepcion = assertThrows(MonedaNoValidaException.class, 
            () -> validatorCuentaBancaria.validate(cuentaBancariaDto));
        
        assertEquals("Error: EUR no es una moneda válida (USD, ARS, DOLARES o PESOS)", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar cuenta con moneda en minúsculas")
    public void testValidarMonedaMinusculas() {
        cuentaBancariaDto.setMoneda("usd");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta con moneda en mayúsculas")
    public void testValidarMonedaMayusculas() {
        cuentaBancariaDto.setMoneda("ARS");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

    @Test
    @DisplayName("Validar cuenta con moneda formato mixto")
    public void testValidarMonedaMixto() {
        cuentaBancariaDto.setMoneda("UsD");
        
        assertDoesNotThrow(() -> validatorCuentaBancaria.validate(cuentaBancariaDto));
    }

}