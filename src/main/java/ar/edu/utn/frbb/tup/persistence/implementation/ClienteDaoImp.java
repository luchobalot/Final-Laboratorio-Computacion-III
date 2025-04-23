package ar.edu.utn.frbb.tup.persistence.implementation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
public class ClienteDaoImp implements ClienteDao{
    private static final String JSON_FILE_PATH = "src/main/java/ar/edu/utn/frbb/tup/persistence/resources/clientes.json";
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .create();


    public void saveClientes(List<Cliente> clientes) {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            gson.toJson(clientes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Cliente> findClientes() {
        try {
            System.out.println("Buscando clientes en: " + JSON_FILE_PATH);
            java.nio.file.Path path = Paths.get(JSON_FILE_PATH);
            
            if (!Files.exists(path)) {
                System.out.println("El archivo no existe: " + path.toAbsolutePath());
                return new ArrayList<>();
            }

            System.out.println("Archivo encontrado, intentando leer...");
            try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
                Type tipoListaClientes = new TypeToken<List<Cliente>>() {}.getType();
                List<Cliente> clientes = gson.fromJson(reader, tipoListaClientes);
                
                if (clientes == null) {
                    System.out.println("Se leyó el archivo pero la lista es null");
                    return new ArrayList<>();
                }
                
                System.out.println("Clientes encontrados: " + clientes.size());
                for (Cliente c : clientes) {
                    System.out.println("Cliente: " + c.getNombre() + " " + c.getApellido() + ", DNI: " + c.getDni());
                }
                
                return clientes;
            }
        } catch (IOException e) {
            System.out.println("Error de E/S al leer el archivo: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error inesperado al leer clientes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Cliente> getAllClientes() {
        return findClientes();
    }


    @Override
    public Cliente getCliente(long dni) throws ClienteNoExisteException {
        System.out.println("Buscando cliente con DNI: " + dni);
        List<Cliente> clientes = findClientes();
        return clientes.stream()
                .filter(cliente -> cliente.getDni() == dni)
                .findFirst()
                .orElseThrow(() -> new ClienteNoExisteException("No se encontro el cliente con dni: " + dni));
    }

    @Override
    public Cliente createCliente(Cliente cliente) throws ClienteAlreadyExistsException{
        List<Cliente> clientes = findClientes();
        if (clientes.stream().anyMatch(clienteExistente -> clienteExistente.getDni() == cliente.getDni())) {
            throw new ClienteAlreadyExistsException("El cliente con dni: " + cliente.getDni() + " ya existe");
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
        throw new ClienteNoExisteException("No se encontro el cliente con dni: " + cliente.getDni());
    }

    @Override
    public void deleteCliente(long dni) throws ClienteNoExisteException {
        List<Cliente> clientes = findClientes();
        if (!clientes.stream().anyMatch(cliente -> cliente.getDni() == dni)) {
            throw new ClienteNoExisteException("No se encontro el cliente con dni: " + dni);
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
        throw new ClienteNoExisteException("No se encontro el cliente con cuenta con id: " + id);
    }
}