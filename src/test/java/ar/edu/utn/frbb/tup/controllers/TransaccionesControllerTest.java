package ar.edu.utn.frbb.tup.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ar.edu.utn.frbb.tup.controllers.dto.TransaccionesDto;
import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;
import ar.edu.utn.frbb.tup.service.TransaccionesService;

@ExtendWith(MockitoExtension.class)
public class TransaccionesControllerTest {
    
    @Mock 
    private TransaccionesService transaccionesService;

    @InjectMocks
    private TransaccionesController transaccionesController;

    @Test
    public void testCrearTransaccion() throws DataNoValidException, MontoNoValidoException, CuentaNoExisteException, MonedaNoCoincideException, ClienteNoExisteException {
        TransaccionesDto transaccionesDto = new TransaccionesDto();
        Transacciones transaccion = new Transacciones();

        when(transaccionesService.crearTransaccion(transaccionesDto)).thenReturn(transaccion);

        ResponseEntity<?> response = transaccionesController.crearTransaccion(transaccionesDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transaccion, response.getBody());
    }
}