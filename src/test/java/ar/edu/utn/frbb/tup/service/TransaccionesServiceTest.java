package ar.edu.utn.frbb.tup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.utn.frbb.tup.controllers.dto.TransaccionesDto;
import ar.edu.utn.frbb.tup.models.Banco;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.TipoMoneda;
import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;
import ar.edu.utn.frbb.tup.persistence.implementation.CuentaBancariaDaoImp;
import ar.edu.utn.frbb.tup.persistence.implementation.TransaccionesDaoImp;
import ar.edu.utn.frbb.tup.service.implementation.TransaccionesServiceImp;

@ExtendWith(MockitoExtension.class)
public class TransaccionesServiceTest {
    @Mock
    private CuentaBancariaDaoImp cuentaBancariaDao;

    @Mock 
    private TransaccionesDaoImp transaccionesDao;

    @Mock
    private Banco banco;

    @InjectMocks
    private TransaccionesServiceImp transaccionesService;

    @Test
    public void testTransaccionErroresIniciales() {
        TransaccionesDto transaccionesDto = new TransaccionesDto();
        transaccionesDto.setMonto(0);

        MontoNoValidoException exception = assertThrows(MontoNoValidoException.class, 
            () -> transaccionesService.crearTransaccion(transaccionesDto));
        
        assertEquals("El monto no puede ser negativo o 0", exception.getMessage());
    }

    @Test
    public void testTransaccionEnDolaresError() throws CuentaNoExisteException {
        TransaccionesDto transaccionesDto = new TransaccionesDto();
        transaccionesDto.setMonto(10000);
        transaccionesDto.setTipo("CREDITO");
        transaccionesDto.setDescripcion("Test de transacción");
        transaccionesDto.setIdCuenta(1000333);

        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setMoneda(TipoMoneda.DOLARES);

        when(cuentaBancariaDao.getCuentaBancariaById(anyLong())).thenReturn(cuenta);

        MonedaNoCoincideException exception = assertThrows(MonedaNoCoincideException.class, 
            () -> transaccionesService.crearTransaccion(transaccionesDto));
        
        assertEquals("La cuenta no es en pesos", exception.getMessage());
    }

    @Test
    public void testTransaccionMontoNoValido() throws CuentaNoExisteException {
        TransaccionesDto transaccionesDto = new TransaccionesDto();
        transaccionesDto.setMonto(5666777);
        transaccionesDto.setTipo("CREDITO");
        transaccionesDto.setDescripcion("Test de transacción");
        transaccionesDto.setIdCuenta(1000333);

        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setMoneda(TipoMoneda.PESOS_ARG);
        cuenta.setSaldo(10000);

        when(cuentaBancariaDao.getCuentaBancariaById(anyLong())).thenReturn(cuenta);
        when(banco.getLimiteSobregiro()).thenReturn(-100000f);

        MontoNoValidoException exception = assertThrows(MontoNoValidoException.class, 
            () -> transaccionesService.crearTransaccion(transaccionesDto));
        
        assertEquals("El saldo sobrepasa el limite de sobregiro", exception.getMessage());
    }

    

    @Test
    public void testTransaccionExitosa() throws CuentaNoExisteException, ClienteNoExisteException, MontoNoValidoException, MonedaNoCoincideException {
        TransaccionesDto transaccionesDto = new TransaccionesDto();
        transaccionesDto.setMonto(10000);
        transaccionesDto.setTipo("CREDITO");
        transaccionesDto.setDescripcion("Test de transacción");
        transaccionesDto.setIdCuenta(1000333);

        Transacciones transaccion = new Transacciones(transaccionesDto);

        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setMoneda(TipoMoneda.PESOS_ARG);
        cuenta.setSaldo(100000);

        when(cuentaBancariaDao.getCuentaBancariaById(anyLong())).thenReturn(cuenta);
        when(banco.getLimiteSobregiro()).thenReturn(-100000f);
        when(transaccionesDao.createTransaccion(any(Transacciones.class))).thenReturn(transaccion);

        Transacciones resultado = transaccionesService.crearTransaccion(transaccionesDto);

        assertNotNull(resultado);
        assertEquals(transaccion, resultado);
        
        verify(transaccionesDao, times(1)).createTransaccion(any(Transacciones.class));
    }
}