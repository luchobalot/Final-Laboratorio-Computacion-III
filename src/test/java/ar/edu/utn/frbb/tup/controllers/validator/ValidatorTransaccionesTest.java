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

import ar.edu.utn.frbb.tup.controllers.dto.TransaccionesDto;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;

@ExtendWith(MockitoExtension.class)
public class ValidatorTransaccionesTest {

    @InjectMocks
    private ValidatorTransacciones validatorTransacciones;

    private TransaccionesDto transaccionesDto;

    @BeforeEach
    public void configurar() {
        transaccionesDto = new TransaccionesDto();
        transaccionesDto.setIdCuenta(1000333);
        transaccionesDto.setTipo("CREDITO");
        transaccionesDto.setDescripcion("Compra en comercio");
        transaccionesDto.setMonto(1000.0f);
    }

    @Test
    @DisplayName("Validar transacción con datos correctos")
    public void testValidarTransaccionCorrecta() {
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción con ID de cuenta inválido (negativo)")
    public void testValidarTransaccionIdCuentaNegativo() {
        transaccionesDto.setIdCuenta(-100);
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorTransacciones.validate(transaccionesDto));
        
        assertEquals("Error: El ID de la cuenta no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transacción con ID de cuenta inválido (cero)")
    public void testValidarTransaccionIdCuentaCero() {
        transaccionesDto.setIdCuenta(0);
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorTransacciones.validate(transaccionesDto));
        
        assertEquals("Error: El ID de la cuenta no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transacción tipo CREDITO")
    public void testValidarTransaccionTipoCredito() {
        transaccionesDto.setTipo("CREDITO");
        
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción tipo DEBITO")
    public void testValidarTransaccionTipoDebito() {
        transaccionesDto.setTipo("DEBITO");
        
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción tipo C (abreviado de CREDITO)")
    public void testValidarTransaccionTipoCreditoAbreviado() {
        transaccionesDto.setTipo("C");
        
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción tipo D (abreviado de DEBITO)")
    public void testValidarTransaccionTipoDebitoAbreviado() {
        transaccionesDto.setTipo("D");
        
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción tipo inválido")
    public void testValidarTransaccionTipoInvalido() {
        transaccionesDto.setTipo("TRANSFERENCIA");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorTransacciones.validate(transaccionesDto));
        
        assertEquals("Error: TRANSFERENCIA no es un tipo valido de transaccion (DEBITO o CREDITO).", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar transacción tipo en minúsculas")
    public void testValidarTransaccionTipoMinusculas() {
        transaccionesDto.setTipo("credito");
        
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción tipo en mayúsculas")
    public void testValidarTransaccionTipoMayusculas() {
        transaccionesDto.setTipo("DEBITO");
        
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción tipo con formato mixto")
    public void testValidarTransaccionTipoMixto() {
        transaccionesDto.setTipo("CrEdItO");
        
        assertDoesNotThrow(() -> validatorTransacciones.validate(transaccionesDto));
    }

    @Test
    @DisplayName("Validar transacción tipo vacío")
    public void testValidarTransaccionTipoVacio() {
        transaccionesDto.setTipo("");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorTransacciones.validate(transaccionesDto));
        
        assertEquals("Error:  no es un tipo valido de transaccion (DEBITO o CREDITO).", excepcion.getMessage());
    }
}