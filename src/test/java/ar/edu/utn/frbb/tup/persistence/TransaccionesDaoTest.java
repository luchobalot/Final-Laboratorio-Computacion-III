package ar.edu.utn.frbb.tup.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.TipoTransaccion;
import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.persistence.implementation.TransaccionesDaoImp;

@ExtendWith(MockitoExtension.class)
public class TransaccionesDaoTest {
    @Mock
    private ClienteDao clienteDao;

    @Mock 
    private CuentaBancariaDao cuentaBancariaDao;

    @InjectMocks
    private TransaccionesDaoImp transaccionesDao;

    @Test 
    public void testObtenerTransacciones() {
        List<Transacciones> transacciones = new ArrayList<>();
        transaccionesDao.deleteTransacciones();
        assertEquals(transaccionesDao.getAllTransacciones(), transacciones);
    }

    @Test
    public void testCrearTransacciones() throws ClienteNoExisteException {
        // Configurar cliente de prueba
        Cliente cliente = new Cliente();
        cliente.setDni(12345678);
        
        // Configurar cuenta de prueba
        CuentaBancaria cuentaBancaria = new CuentaBancaria();
        cuentaBancaria.setIdCuenta(12345678);
        cuentaBancaria.setSaldo(200000);
        
        // Importante: inicializar el conjunto de cuentas
        Set<CuentaBancaria> cuentas = new HashSet<>();
        cuentas.add(cuentaBancaria);
        cliente.setCuentas(cuentas);

        // Configurar transacción de prueba
        Transacciones transaccion = new Transacciones();
        transaccion.setIdCuenta(12345678);
        transaccion.setMonto(10000);
        transaccion.setTipo(TipoTransaccion.CREDITO);
        transaccion.setDescripcion("Prueba de transacción");

        // Configurar comportamiento de los mocks
        when(clienteDao.getClienteByCuentaId(eq(12345678L))).thenReturn(cliente);
        when(clienteDao.updateCliente(eq(12345678L), any(Cliente.class))).thenReturn(cliente);
        doNothing().when(cuentaBancariaDao).addTransaccion(any(Transacciones.class));    

        // Ejecutar método a probar
        Transacciones result = transaccionesDao.createTransaccion(transaccion);

        // Verificar que se obtuvo un resultado
        assertNotNull(result);
        
        // Verificar que se llamaron los métodos esperados
        verify(clienteDao, times(1)).getClienteByCuentaId(eq(12345678L));
        verify(clienteDao, times(1)).updateCliente(eq(12345678L), any(Cliente.class));
        verify(cuentaBancariaDao, times(1)).addTransaccion(any(Transacciones.class));
    }
}