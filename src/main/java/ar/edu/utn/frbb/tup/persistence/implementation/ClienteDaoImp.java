package ar.edu.utn.frbb.tup.persistence.implementation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;

@Repository
public class ClienteDaoImp implements ClienteDao {
    private static final String JSON_FILE_PATH = "src/main/java/ar/edu/utn/frbb/tup/persistence/resources/clientes.json";
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .create();
    private static final Logger logger = Logger.getLogger(ClienteDaoImp.class.getName());

    public void saveClientes(List<Cliente> clientes) {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            gson.toJson(clientes, writer);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar clientes", e);
        }
    }

    public List<Cliente> findClientes() {
        try {
            logger.info(MessageFormat.format("Buscando clientes en: {0}", JSON_FILE_PATH));
            java.nio.file.Path path = Paths.get(JSON_FILE_PATH);

            if (!Files.exists(path)) {
                logger.warning(MessageFormat.format("El archivo no existe: {0}", path.toAbsolutePath()));
                return new ArrayList<>();
            }

            logger.info("Archivo encontrado, intentando leer...");
            try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
                Type tipoListaClientes = new TypeToken<List<Cliente>>() {}.getType();
                List<Cliente> clientes = gson.fromJson(reader, tipoListaClientes);

                if (clientes == null) {
                    logger.warning("Se leyó el archivo pero la lista es null");
                    return new ArrayList<>();
                }

                logger.info(MessageFormat.format("Clientes encontrados: {0}", clientes.size()));
                if (logger.isLoggable(Level.FINE)) {
                    for (Cliente c : clientes) {
                        logger.fine(MessageFormat.format(
                            "Cliente: {0} {1}, DNI: {2}",
                            c.getNombre(), c.getApellido(), c.getDni()));
                    }
                }

                return clientes;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error de E/S al leer el archivo", e);
            return new ArrayList<>();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al leer clientes", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Cliente> getAllClientes() {
        return findClientes();
    }

    @Override
    public Cliente getCliente(long dni) throws ClienteNoExisteException {
        logger.info(MessageFormat.format("Buscando cliente con DNI: {0}", dni));
        List<Cliente> clientes = findClientes();
        return clientes.stream()
                .filter(cliente -> cliente.getDni() == dni)
                .findFirst()
                .orElseThrow(() -> new ClienteNoExisteException(
                        MessageFormat.format("No se encontró el cliente con dni: {0}", dni)));
    }

    @Override
    public Cliente createCliente(Cliente cliente) throws ClienteAlreadyExistsException {
        List<Cliente> clientes = findClientes();
        if (clientes.stream().anyMatch(clienteExistente -> clienteExistente.getDni() == cliente.getDni())) {
            throw new ClienteAlreadyExistsException(
                    MessageFormat.format("El cliente con dni: {0} ya existe", cliente.getDni()));
        }
        clientes.add(cliente);
        saveClientes(clientes);
        return cliente;
    }

    @Override
    public Cliente updateCliente(long dni, Cliente cliente) throws ClienteNoExisteException {
        List<Cliente> clientes = findClientes();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getDni() == dni) {
                clientes.set(i, cliente);
                saveClientes(clientes);
                return cliente;
            }
        }
        throw new ClienteNoExisteException(
                MessageFormat.format("No se encontró el cliente con dni: {0}", cliente.getDni()));
    }

    @Override
    public void deleteCliente(long dni) throws ClienteNoExisteException {
        List<Cliente> clientes = findClientes();
        if (!clientes.stream().anyMatch(cliente -> cliente.getDni() == dni)) {
            throw new ClienteNoExisteException(
                    MessageFormat.format("No se encontró el cliente con dni: {0}", dni));
        }
        clientes.removeIf(cliente -> cliente.getDni() == dni);
        saveClientes(clientes);
    }

    @Override
    public Cliente getClienteByCuentaId(long id) throws ClienteNoExisteException {
        List<Cliente> clientes = findClientes();
        for (Cliente cliente : clientes) {
            for (CuentaBancaria cuenta : cliente.getCuentas()) {
                if (cuenta.getIdCuenta() == id) {
                    return cliente;
                }
            }
        }
        throw new ClienteNoExisteException(
                MessageFormat.format("No se encontró el cliente con cuenta con id: {0}", id));
    }
}
