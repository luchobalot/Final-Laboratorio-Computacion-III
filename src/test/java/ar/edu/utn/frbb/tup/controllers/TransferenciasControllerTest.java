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

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.controllers.validator.ValidatorTransferencias;
import ar.edu.utn.frbb.tup.models.Recibo;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentasIgualesException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoValidaException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;
import ar.edu.utn.frbb.tup.models.exceptions.SaldoNoValidoException;
import ar.edu.utn.frbb.tup.service.TransferenciasService;

@ExtendWith(MockitoExtension.class)
public class TransferenciasControllerTest {
    
    @Mock
    private ValidatorTransferencias transferenciasValidator;

    @Mock
    private TransferenciasService transferenciasService;

    @InjectMocks
    private TransferenciasController transferenciasController;

    @Test
    public void testCrearTransferencia() throws CuentasIgualesException, MontoNoValidoException, 
        CuentaNoExisteException, SaldoNoValidoException, ClienteNoExisteException, 
        MonedaNoCoincideException, MonedaNoValidaException {
      
        TransferenciasDto transferenciasDto = new TransferenciasDto(); 
        Recibo recibo = new Recibo();

        when(transferenciasService.crearTransferencia(transferenciasDto)).thenReturn(recibo);

        ResponseEntity <?> response = transferenciasController.crearTransferencia(transferenciasDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(recibo, response.getBody());
    }
}