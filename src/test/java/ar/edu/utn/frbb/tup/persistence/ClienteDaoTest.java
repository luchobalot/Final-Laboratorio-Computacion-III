package ar.edu.utn.frbb.tup.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.persistence.implementation.ClienteDaoImp;

public class ClienteDaoTest {

    @TempDir
    Path tempDir;
    
    private ClienteDaoImp clienteDao;
    private String jsonFilePath;
    private Cliente cliente;
    
    @BeforeEach
    void setUp() throws Exception {
      
        File file = tempDir.resolve("clientes.json").toFile();
        jsonFilePath = file.getAbsolutePath();
        Files.write(file.toPath(), "[]".getBytes());
        
        clienteDao = new ClienteDaoImp();

        java.lang.reflect.Field field = ClienteDaoImp.class.getDeclaredField("JSON_FILE_PATH");
        field.setAccessible(true);
        field.set(null, jsonFilePath);
        
        // Cliente de prueba
        cliente = new Cliente();
        cliente.setNombre("Luciano");
        cliente.setApellido("Balot");
        cliente.setDni(12345678);
        cliente.setFechaNacimiento(LocalDate.of(2002,3,5));
        cliente.setMail("lucianobalot@ejemplo.com");
        cliente.setTelefono("2915756380");
    }
    
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(jsonFilePath));
    }
    
    @Test
    @DisplayName("Crear y obtener cliente:")
    void testCreateAndGetCliente() throws ClienteAlreadyExistsException, ClienteNoExisteException {
        Cliente creado = clienteDao.createCliente(cliente);
        Cliente obtenido = clienteDao.getCliente(cliente.getDni());
        
        assertNotNull(creado);
        assertEquals(cliente.getDni(), creado.getDni());
        
        assertNotNull(obtenido);
        assertEquals(cliente.getDni(), obtenido.getDni());
    }
    
    @Test
    @DisplayName("Obtener todos los clientes:")
    void testGetAllClientes() throws ClienteAlreadyExistsException {
        clienteDao.createCliente(cliente);
        
        Cliente otroCliente = new Cliente();
        otroCliente.setNombre("Maria");
        otroCliente.setApellido("García");
        otroCliente.setDni(45789654);
        otroCliente.setFechaNacimiento(LocalDate.of(1985, 5, 20));
        otroCliente.setMail("anagarcia@ejemplo.com");
        otroCliente.setTelefono("2914568956");
        clienteDao.createCliente(otroCliente);
        
        List<Cliente> clientes = clienteDao.getAllClientes();
        
        assertEquals(2, clientes.size());
    }
}