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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.TipoTransaccion;
import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaBancariaDao;
import ar.edu.utn.frbb.tup.persistence.TransaccionesDao;

@Repository
public class TransaccionesDaoImp implements TransaccionesDao {

    private static final Logger logger = LoggerFactory.getLogger(TransaccionesDaoImp.class);
    private static final String JSON_FILE_PATH = "src/main/java/ar/edu/utn/frbb/tup/persistence/resources/transacciones.json";
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .create();

    private final CuentaBancariaDao cuentaBancariaDao;
    private final ClienteDao clienteDao;

    @Autowired
    public TransaccionesDaoImp(CuentaBancariaDao cuentaBancariaDao, ClienteDao clienteDao) {
        this.cuentaBancariaDao = cuentaBancariaDao;
        this.clienteDao = clienteDao;
    }

    /**
     * Guarda la lista completa de transacciones en el archivo JSON.
     * 
     * @param transacciones lista de transacciones a guardar
     */
    public void saveTransacciones(List<Transacciones> transacciones) {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            gson.toJson(transacciones, writer);
        } catch (IOException e) {
            logger.error("Error al guardar transacciones en JSON", e);
        }
    }

    /**
     * Busca y devuelve todas las transacciones registradas.
     * 
     * @return lista de transacciones encontradas o vacía si no hay
     */
    public List<Transacciones> findTransacciones() {
        try {
            if (!Files.exists(Paths.get(JSON_FILE_PATH))) {
                return new ArrayList<>();
            }

            try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
                Type tipoListaTransacciones = new TypeToken<List<Transacciones>>() {}.getType();
                return gson.fromJson(reader, tipoListaTransacciones);
            }
        } catch (IOException e) {
            logger.error("Error al leer transacciones desde JSON", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Transacciones> getAllTransacciones() {
        return findTransacciones();
    }

    /**
     * Crea una nueva transacción en la base de datos.
     * 
     * @param transaccion datos de la transacción a crear
     * @return la transacción creada
     * @throws ClienteNoExisteException si el cliente no existe
     */
    @Override
    public Transacciones createTransaccion(Transacciones transaccion) throws ClienteNoExisteException {
        List<Transacciones> transacciones = findTransacciones();

        Cliente cliente = clienteDao.getClienteByCuentaId(transaccion.getIdCuenta());

        for (CuentaBancaria c : cliente.getCuentas()) {
            if (c.getIdCuenta() == transaccion.getIdCuenta()) {
                if (transaccion.getTipo().equals(TipoTransaccion.CREDITO)) {
                    c.setSaldo(c.getSaldo() - transaccion.getMonto());
                } else {
                    c.setSaldo(c.getSaldo() + transaccion.getMonto());
                }
                c.addTransacciones(transaccion);
                break;
            }
        }

        clienteDao.updateCliente(cliente.getDni(), cliente);
        cuentaBancariaDao.addTransaccion(transaccion);
        transacciones.add(transaccion);
        saveTransacciones(transacciones);

        return transaccion;
    }

    /**
     * Borra todas las transacciones del sistema.
     */
    @Override
    public void deleteTransacciones() {
        saveTransacciones(new ArrayList<>());
    }
}
