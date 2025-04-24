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

    @TempDir // Anotación específica de JUnit 5
    Path tempDir;
    
    private ClienteDaoImp clienteDao;
    private String jsonFilePath;
    private Cliente cliente;
    
    @BeforeEach
    void setUp() throws Exception {
        // Crear archivo JSON temporal
        File file = tempDir.resolve("clientes.json").toFile();
        jsonFilePath = file.getAbsolutePath();
        Files.write(file.toPath(), "[]".getBytes());
        
        // Inicializar DAO con el archivo temporal
        clienteDao = new ClienteDaoImp();
        // Setear ruta temporal usando reflexión
        java.lang.reflect.Field field = ClienteDaoImp.class.getDeclaredField("JSON_FILE_PATH");
        field.setAccessible(true);
        field.set(null, jsonFilePath);
        
        // Crear cliente de prueba
        cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setDni(12345678);
        cliente.setFechaNacimiento(LocalDate.of(1990,1,15));
        cliente.setMail("juan@ejemplo.com");
        cliente.setTelefono("1122334455");
    }
    
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(jsonFilePath));
    }
    
    @Test
    @DisplayName("Crear y obtener cliente")
    void testCreateAndGetCliente() throws ClienteAlreadyExistsException, ClienteNoExisteException {
        // Act
        Cliente creado = clienteDao.createCliente(cliente);
        Cliente obtenido = clienteDao.getCliente(cliente.getDni());
        
        // Assert - Usando aserciones de JUnit 5
        assertNotNull(creado);
        assertEquals(cliente.getDni(), creado.getDni());
        
        assertNotNull(obtenido);
        assertEquals(cliente.getDni(), obtenido.getDni());
    }
    
    @Test
    @DisplayName("Obtener todos los clientes")
    void testGetAllClientes() throws ClienteAlreadyExistsException {
        // Arrange
        clienteDao.createCliente(cliente);
        
        Cliente otroCliente = new Cliente();
        otroCliente.setNombre("Ana");
        otroCliente.setApellido("García");
        otroCliente.setDni(87654321);
        otroCliente.setFechaNacimiento(LocalDate.of(1985, 5, 20));
        otroCliente.setMail("ana@ejemplo.com");
        otroCliente.setTelefono("9988776655");
        clienteDao.createCliente(otroCliente);
        
        // Act
        List<Cliente> clientes = clienteDao.getAllClientes();
        
        // Assert
        assertEquals(2, clientes.size());
    }
}