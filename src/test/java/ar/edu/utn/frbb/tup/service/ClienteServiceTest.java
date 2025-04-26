package ar.edu.utn.frbb.tup.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.utn.frbb.tup.controllers.dto.ClienteDto;
import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.EdadNoValidaException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.implementation.CuentaBancariaDaoImp;
import ar.edu.utn.frbb.tup.service.implementation.ClienteServiceImp;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @Mock 
    private CuentaBancariaDaoImp cuentaBancariaDao;

    @InjectMocks
    private ClienteServiceImp clienteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerAllClientes() {
        List<Cliente> expectedClientes = Arrays.asList(new Cliente(), new Cliente());
        when(clienteDao.getAllClientes()).thenReturn(expectedClientes);

        List<Cliente> actualClientes = clienteService.obtenerAllClientes();

        assertEquals(expectedClientes, actualClientes);
        verify(clienteDao, times(1)).getAllClientes();
    }

    @Test
    public void testObtenerClienteExistente() throws ClienteNoExisteException {
        Cliente expectedCliente = new Cliente();
        when(clienteDao.getCliente(12345678L)).thenReturn(expectedCliente);

        Cliente actualCliente = clienteService.obtenerCliente(12345678L);

        assertEquals(expectedCliente, actualCliente);
        verify(clienteDao, times(1)).getCliente(12345678L);
    }

    @Test
    public void testObtenerClienteNoExistente() throws ClienteNoExisteException {
        when(clienteDao.getCliente(12345678L)).thenThrow(new ClienteNoExisteException("Cliente no existe."));

        assertThrows(ClienteNoExisteException.class, () -> clienteService.obtenerCliente(12345678L));
        verify(clienteDao, times(1)).getCliente(12345678L);
    }

    @Test
    public void testCrearCliente() throws ClienteAlreadyExistsException, EdadNoValidaException {
        //Test para ver si crea cliente
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        clienteDto.setDni(12345678L);
        clienteDto.setTipo("M");
        clienteDto.setNombre("Luciano");
        clienteDto.setApellido("Balot");
        clienteDto.setMail("luchobalot@gmail.com");
        clienteDto.setTelefono("123456789");

        Cliente expectedCliente = new Cliente(clienteDto);
        when(clienteDao.createCliente(any(Cliente.class))).thenReturn(expectedCliente);

        Cliente actualCliente = clienteService.crearCliente(clienteDto);

        assertEquals(expectedCliente, actualCliente);
        verify(clienteDao, times(1)).createCliente(any(Cliente.class));
    }

    @Test
    public void testCrearClienteEdadNoValida() throws ClienteAlreadyExistsException {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setFechaNacimiento(LocalDate.now().minusYears(17));

        assertThrows(EdadNoValidaException.class, () -> clienteService.crearCliente(clienteDto));
        verify(clienteDao, never()).createCliente(any(Cliente.class));
    }

    @Test
    public void testActualizarCliente() throws ClienteNoExisteException, EdadNoValidaException {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        clienteDto.setDni(12345678L);
        clienteDto.setTipo("M");
        clienteDto.setNombre("Nicolas");
        clienteDto.setApellido("Balot");
        clienteDto.setMail("nicobalot@gmail.com");
        clienteDto.setTelefono("123456789");

        Cliente expectedCliente = new Cliente(clienteDto);
        when(clienteDao.updateCliente(eq(12345678L), any(Cliente.class))).thenReturn(expectedCliente);
        when(clienteDao.getCliente(12345678L)).thenReturn(expectedCliente);

        Cliente actualCliente = clienteService.actualizarCliente(12345678L, clienteDto);

        assertEquals(expectedCliente, actualCliente);
        verify(clienteDao, times(1)).updateCliente(eq(12345678L), any(Cliente.class));
    }

    @Test
    public void testBorrarCliente() throws ClienteNoExisteException, CuentaNoExisteException {
      ClienteDto clienteDto = new ClienteDto();
      clienteDto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
      clienteDto.setDni(12345678L);
      clienteDto.setTipo("M");
      clienteDto.setNombre("Nicolas");
      clienteDto.setApellido("Balot");
      clienteDto.setMail("nicobalot@gmail.com");
      clienteDto.setTelefono("123456789");


        Cliente expectedCliente = new Cliente(clienteDto);
        lenient().when(clienteDao.getCliente(12345678L)).thenReturn(expectedCliente);
        doNothing().when(clienteDao).deleteCliente(12345678L);
        doNothing().when(cuentaBancariaDao).deleteCuentasPorTitular(12345678L);
        clienteService.borrarCliente(12345678L);

        verify(clienteDao, times(1)).deleteCliente(12345678L);
    }
}