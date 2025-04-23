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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaBancariaDao;
import ar.edu.utn.frbb.tup.persistence.TransferenciasDao;

@Repository
public class TransferenciasDaoImp implements TransferenciasDao {
    private static final String JSON_FILE_PATH = "src/main/java/ar/edu/utn/frbb/tup/persistence/resources/transferencias.json";
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .create();

    
    private CuentaBancariaDao cuentaBancariaDao;
    private ClienteDao clienteDao;

    @Autowired
    public TransferenciasDaoImp(CuentaBancariaDao cuentaBancariaDao, ClienteDao clienteDao) {
        this.cuentaBancariaDao = cuentaBancariaDao;
        this.clienteDao = clienteDao;
    }


    public void saveTransfers(List<Transferencias> transferencias) {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            gson.toJson(transferencias, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Transferencias> findTransfers() {
        try {
            if (!Files.exists(Paths.get(JSON_FILE_PATH))) {
                return new ArrayList<>(); // Retorna una lista vacía si el archivo no existe
            }

            try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
                Type tipoListaTransfers = new TypeToken<List<Transferencias>>() {}.getType();
                return gson.fromJson(reader, tipoListaTransfers);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Retorna una lista vacía en caso de error  
        }
    }

    @Override 
    public List<Transferencias> getAllTransfers(){
        return findTransfers();
    }

    /**
     * Realiza una transferencia entre dos cuentas que pertenecen a
     * diferentes bancos. La transferencia se registra en la cuenta de
     * origen, en clientes y en el listado de transferencias.
     * 
     * @param transferencia La transferencia a realizar.
     * @throws ClienteNoExisteException Si el cliente no existe.
     */
    @Override
    public void transferBetweenBanks(Transferencias transferencia) throws ClienteNoExisteException {
        List<Transferencias> tranfers = findTransfers();
        
        Cliente cliente = clienteDao.getClienteByCuentaId(transferencia.getCuentaOrigen());
        
        //Agrega a clientes.json
        for (CuentaBancaria c : cliente.getCuentas()) {
            if (c.getIdCuenta() == transferencia.getCuentaOrigen()) {
                c.setSaldo(c.getSaldo() - transferencia.getMonto() - transferencia.getComision());
                c.addTransferencia(transferencia);
                break;
            }
        }

        clienteDao.updateCliente(cliente.getDni(), cliente);

        //Agrega a cuentasBancarias.json
        cuentaBancariaDao.addTransferBetweenBanks(transferencia);
        
        //Agrega a transferencias.json
        tranfers.add(transferencia);
        saveTransfers(tranfers);
    }

    /**
     * Realiza una transferencia entre dos cuentas que pertenecen al
     * mismo banco. La transferencia se registra en la cuenta de
     * origen, la cuenta de destino, en clientes y en el listado de transferencias.
     * 
     * @param transferencia La transferencia a realizar.
     * @throws ClienteNoExisteException Si el cliente no existe.
     */
    @Override
    public void transferInBank(Transferencias transferencia) throws ClienteNoExisteException {
        List<Transferencias> tranfers = findTransfers();
        
        Cliente clienteOrigen = clienteDao.getClienteByCuentaId(transferencia.getCuentaOrigen());
        Cliente clienteDestino = clienteDao.getClienteByCuentaId(transferencia.getCuentaDestino());

        if (clienteOrigen.getDni() == clienteDestino.getDni()) {
            Cliente cliente = clienteOrigen;

            //Agrega a clientes.json
            for (CuentaBancaria c : cliente.getCuentas()) {
                if (c.getIdCuenta() == transferencia.getCuentaOrigen()) {
                    c.setSaldo(c.getSaldo() - transferencia.getMonto() - transferencia.getComision());
                    c.addTransferencia(transferencia);
                }
                if (c.getIdCuenta() == transferencia.getCuentaDestino()) {
                    c.setSaldo(c.getSaldo() + transferencia.getMonto());
                    c.addTransferencia(transferencia);
                }
            }

            clienteDao.updateCliente(cliente.getDni(), cliente);

        } else {
            //Agrega a clientes.json
            for (CuentaBancaria c : clienteOrigen.getCuentas()) {
                if (c.getIdCuenta() == transferencia.getCuentaOrigen()) {
                    c.setSaldo(c.getSaldo() - transferencia.getMonto() - transferencia.getComision());
                    c.addTransferencia(transferencia);
                    break;
                }
            }
            for (CuentaBancaria c : clienteDestino.getCuentas()) {
                if (c.getIdCuenta() == transferencia.getCuentaDestino()) {
                    c.setSaldo(c.getSaldo() + transferencia.getMonto());
                    c.addTransferencia(transferencia);
                    break;
                }
            }

            clienteDao.updateCliente(clienteOrigen.getDni(), clienteOrigen);
            clienteDao.updateCliente(clienteDestino.getDni(), clienteDestino);
        }

    

        //Agrega a cuentasBancarias.json
        cuentaBancariaDao.addTransferInBank(transferencia);

        //Agrega a transferencias.json
        tranfers.add(transferencia);
        saveTransfers(tranfers);
    }

    //metodo solo para testear
    public void deleteTransfers(){
        saveTransfers(new ArrayList<>());
    }
}
