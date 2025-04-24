package ar.edu.utn.frbb.tup.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ar.edu.utn.frbb.tup.controllers.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controllers.validator.ValidatorCliente;
import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;
import ar.edu.utn.frbb.tup.models.exceptions.EdadNoValidaException;
import ar.edu.utn.frbb.tup.service.ClienteService;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {

    @Mock 
    private ClienteService clienteService;

    @Mock
    private ValidatorCliente clienteValidator;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    public void testObtenerCliente() throws ClienteNoExisteException {
        Cliente expected = new Cliente();
        long dni = 12345678;

        when(clienteService.obtenerCliente(12345678)).thenReturn(expected);
    
        ResponseEntity<?> response = clienteController.obtenerCliente(dni);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    public void testObtenerClienteFallido() throws ClienteNoExisteException {
        long dni = 12345678;

        when(clienteService.obtenerCliente(12345678)).thenThrow(ClienteNoExisteException.class);
    
        ResponseEntity<?> response = clienteController.obtenerCliente(dni);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCrearMalDto() throws DataNoValidException {
        ClienteDto clienteDto = new ClienteDto();
        
        doThrow(DataNoValidException.class).when(clienteValidator).validate(clienteDto);
        
        ResponseEntity<?> response = clienteController.crearCliente(clienteDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCrearExito() throws ClienteAlreadyExistsException, EdadNoValidaException, DataNoValidException {
        ClienteDto clienteDto = new ClienteDto();
        Cliente expected = new Cliente();

        doNothing().when(clienteValidator).validate(clienteDto);
        when(clienteService.crearCliente(clienteDto)).thenReturn(expected);

        ResponseEntity<?> response = clienteController.crearCliente(clienteDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    public void testActualizarNoValid() throws DataNoValidException, ClienteNoExisteException, EdadNoValidaException {
        ClienteDto clienteDto = new ClienteDto();
        long dni = 12345678;

        doNothing().when(clienteValidator).validate(clienteDto);
        when(clienteService.actualizarCliente(dni, clienteDto)).thenThrow(EdadNoValidaException.class);
        
        ResponseEntity<?> response = clienteController.actualizarCliente(dni, clienteDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testActualizarNoCliente() throws DataNoValidException, ClienteNoExisteException, EdadNoValidaException {
        ClienteDto clienteDto = new ClienteDto();
        long dni = 12345678;

        doNothing().when(clienteValidator).validate(clienteDto);
        when(clienteService.actualizarCliente(dni, clienteDto)).thenThrow(ClienteNoExisteException.class);
        
        ResponseEntity<?> response = clienteController.actualizarCliente(dni, clienteDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testActualizarCorrectamente() throws DataNoValidException, ClienteNoExisteException, EdadNoValidaException {
        ClienteDto clienteDto = new ClienteDto();
        Cliente expected = new Cliente();
        long dni = 12345678;

        doNothing().when(clienteValidator).validate(clienteDto);
        when(clienteService.actualizarCliente(dni, clienteDto)).thenReturn(expected);
        
        ResponseEntity<?> response = clienteController.actualizarCliente(dni, clienteDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test 
    public void testBorrarNoExiste() throws ClienteNoExisteException, CuentaNoExisteException {
        long dni = 12345678;

        doThrow(ClienteNoExisteException.class).when(clienteService).borrarCliente(dni);
        
        ResponseEntity<?> response = clienteController.borrarCliente(dni);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}