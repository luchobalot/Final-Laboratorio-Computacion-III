package ar.edu.utn.frbb.tup.controllers.validator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.utn.frbb.tup.controllers.dto.ClienteDto;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;
import ar.edu.utn.frbb.tup.models.exceptions.EdadNoValidaException;

@ExtendWith(MockitoExtension.class)
public class ValidatorClienteTest {

    @InjectMocks
    private ValidatorCliente validatorCliente;

    private ClienteDto clienteDto;

    @BeforeEach
    public void configurar() {
        clienteDto = new ClienteDto();
        clienteDto.setNombre("Juan");
        clienteDto.setApellido("Pérez");
        clienteDto.setDni(12345678);
        clienteDto.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        clienteDto.setMail("juan@correo.com");
        clienteDto.setTelefono("+5491112345678");
        clienteDto.setTipo("F");
    }

    @Test
    @DisplayName("Validar cliente con datos correctos")
    public void testValidarClienteCorrecto() {
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar nombre con caracteres no permitidos")
    public void testValidarNombreInvalido() {
        clienteDto.setNombre("Juan123");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Juan123 no es un nombre valido", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar nombre con caracteres especiales permitidos")
    public void testValidarNombreConCaracteresEspeciales() {
        clienteDto.setNombre("José");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar apellido con caracteres no permitidos")
    public void testValidarApellidoInvalido() {
        clienteDto.setApellido("Pérez123");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Pérez123 no es un apellido valido", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar apellido con caracteres especiales permitidos")
    public void testValidarApellidoConCaracteresEspeciales() {
        clienteDto.setApellido("Gómez");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar DNI negativo")
    public void testValidarDniNegativo() {
        clienteDto.setDni(-12345);
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: El DNI no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar DNI cero")
    public void testValidarDniCero() {
        clienteDto.setDni(0);
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: El DNI no puede ser negativo o 0.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar DNI demasiado largo")
    public void testValidarDniLargo() {
        clienteDto.setDni(123456789); // 9 dígitos
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: El DNI debe ser de 8 digitos.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar fecha de nacimiento posterior a la actual")
    public void testValidarFechaNacimientoFutura() {
        clienteDto.setFechaNacimiento(LocalDate.now().plusYears(1));
        
        EdadNoValidaException excepcion = assertThrows(EdadNoValidaException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("La fecha de nacimiento no puede ser posterior a la fecha actual", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar persona menor de edad")
    public void testValidarMenorEdad() {
        clienteDto.setFechaNacimiento(LocalDate.now().minusYears(17));
        
        EdadNoValidaException excepcion = assertThrows(EdadNoValidaException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: Usted no puede ser menor de edad.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar persona justo en la edad mínima (18 años)")
    public void testValidarEdadMinima() {
        clienteDto.setFechaNacimiento(LocalDate.now().minusYears(18).minusDays(1));
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar email sin formato correcto - sin @")
    public void testValidarEmailSinArroba() {
        clienteDto.setMail("juancorreo.com");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: juancorreo.com no es un mail valido. Revise e intente nuevamente.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar email sin formato correcto - sin dominio")
    public void testValidarEmailSinDominio() {
        clienteDto.setMail("juan@");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: juan@ no es un mail valido. Revise e intente nuevamente.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar email con formato complejo pero válido")
    public void testValidarEmailComplejo() {
        clienteDto.setMail("juan.perez-123@correo.empresa.com");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar teléfono sin formato adecuado - con letras")
    public void testValidarTelefonoConLetras() {
        clienteDto.setTelefono("+54911abcdefg");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: +54911abcdefg no es un telefono valido.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar teléfono demasiado corto")
    public void testValidarTelefonoCorto() {
        clienteDto.setTelefono("12345");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: 12345 no es un telefono valido.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Validar teléfono con prefijo internacional")
    public void testValidarTelefonoConPrefijo() {
        clienteDto.setTelefono("+541112345678");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar teléfono sin prefijo")
    public void testValidarTelefonoSinPrefijo() {
        clienteDto.setTelefono("1112345678");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar tipo de cliente físico - con F")
    public void testValidarTipoClienteFisico() {
        clienteDto.setTipo("F");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar tipo de cliente físico - con FISICA")
    public void testValidarTipoClienteFisicoCompleto() {
        clienteDto.setTipo("FISICA");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar tipo de cliente jurídico - con J")
    public void testValidarTipoClienteJuridico() {
        clienteDto.setTipo("J");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar tipo de cliente jurídico - con JURIDICA")
    public void testValidarTipoClienteJuridicoCompleto() {
        clienteDto.setTipo("JURIDICA");
        
        assertDoesNotThrow(() -> validatorCliente.validate(clienteDto));
    }

    @Test
    @DisplayName("Validar tipo de cliente inválido")
    public void testValidarTipoClienteInvalido() {
        clienteDto.setTipo("X");
        
        DataNoValidException excepcion = assertThrows(DataNoValidException.class, 
            () -> validatorCliente.validate(clienteDto));
        
        assertEquals("Error: X no es un tipo valido.", excepcion.getMessage());
    }
}