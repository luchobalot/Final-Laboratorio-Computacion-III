package ar.edu.utn.frbb.tup.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.persistence.implementation.ClienteDaoImp;
import ar.edu.utn.frbb.tup.persistence.implementation.CuentaBancariaDaoImp;

@ExtendWith(MockitoExtension.class)
public class CuentaBancariaDaoTest {

    @InjectMocks
    private CuentaBancariaDaoImp cuentaBancariaDao;

    @Mock
    private ClienteDaoImp clienteDao;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerCuentas(){
        List<CuentaBancaria>cuentas = cuentaBancariaDao.getAllCuentasBancarias();

        assertNotNull(cuentas);
    }

    @Test
    public void testCrearDeleteYGet() throws ClienteNoExisteException, CuentaNoExisteException {
        Cliente cliente = new Cliente("Juan", "Perez", 12345678, LocalDate.of(1990, 1, 1), "juan@mail.com", "+5491112345678", "F");
        CuentaBancaria cuentaBancaria = new CuentaBancaria(12345678, 200000, "CC", "USD");

        when(clienteDao.getCliente(anyLong())).thenReturn(cliente);
        when(clienteDao.updateCliente(cliente.getDni(), cliente)).thenReturn(cliente);
        cliente.addCuentas(cuentaBancaria);
        CuentaBancaria cuenta = cuentaBancariaDao.createCuentaBancaria(cuentaBancaria);
        
        
        assertNotEquals(cuenta, 0);

        assertEquals(cuenta.getIdCuenta(), cuentaBancariaDao.getCuentaBancariaById(cuenta.getIdCuenta()).getIdCuenta());
        when(clienteDao.updateCliente(cuenta.getTitular(), cliente)).thenReturn(cliente);
        cuentaBancariaDao.deleteCuentaBancaria(cliente, cuentaBancaria.getIdCuenta());

        assertThrows(CuentaNoExisteException.class, () -> cuentaBancariaDao.getCuentaBancariaById(cuentaBancaria.getIdCuenta()));
    }

    @Test
    public void testDeleteErroneo() throws CuentaNoExisteException, ClienteNoExisteException {
        CuentaBancaria cuentaBancaria = new CuentaBancaria(12345678, 200000, "CC", "USD");

        assertThrows(CuentaNoExisteException.class, () -> cuentaBancariaDao.deleteCuentaBancaria(null, cuentaBancaria.getIdCuenta()));
    }

    @Test 
    public void testObtenerTransfers() throws ClienteNoExisteException, CuentaNoExisteException{
        Cliente cliente = new Cliente("Juan", "Perez", 12345678, LocalDate.of(1990, 1, 1), "juan@mail.com", "+5491112345678", "F");
        CuentaBancaria cuentaBancaria = new CuentaBancaria(12345678, 200000, "CC", "USD");

        when(clienteDao.getCliente(anyLong())).thenReturn(cliente);
        when(clienteDao.updateCliente(cliente.getDni(), cliente)).thenReturn(cliente);
        cliente.addCuentas(cuentaBancaria);
        CuentaBancaria cuenta = cuentaBancariaDao.createCuentaBancaria(cuentaBancaria);

        assertEquals(cuentaBancariaDao.getTransferenciasById(cuenta.getIdCuenta()), new ArrayList<>());
    
        cuentaBancariaDao.deleteCuentaBancaria(cliente, cuentaBancaria.getIdCuenta());

        assertThrows(CuentaNoExisteException.class, () -> cuentaBancariaDao.getCuentaBancariaById(cuentaBancaria.getIdCuenta()));
    }

    @Test 
    public void testDeposito() throws CuentaNoExisteException, ClienteNoExisteException{
        Cliente cliente = new Cliente("Juan", "Perez", 12345678, LocalDate.of(1990, 1, 1), "juan@mail.com", "+5491112345678", "F");
        CuentaBancaria cuentaBancaria = new CuentaBancaria(12345678, 200000, "CC", "USD");

        when(clienteDao.getCliente(anyLong())).thenReturn(cliente);
        when(clienteDao.updateCliente(cliente.getDni(), cliente)).thenReturn(cliente);
        cliente.addCuentas(cuentaBancaria);
        CuentaBancaria cuenta = cuentaBancariaDao.createCuentaBancaria(cuentaBancaria);

        assertEquals(cuenta.getSaldo(), 200000);
        cuentaBancariaDao.addDeposito(cliente, cuenta.getIdCuenta(), 1000);

        assertEquals(cuenta.getSaldo(), 201000);

        cuentaBancariaDao.deleteCuentaBancaria(cliente, cuentaBancaria.getIdCuenta());

        assertThrows(CuentaNoExisteException.class, () -> cuentaBancariaDao.getCuentaBancariaById(cuentaBancaria.getIdCuenta()));
    }

}