package ar.edu.utn.frbb.tup.persistence;

import java.util.List;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;

public interface ClienteDao {
    List<Cliente> getAllClientes();

    Cliente getCliente(long dni) throws ClienteNoExisteException;

    Cliente createCliente(Cliente cliente) throws ClienteAlreadyExistsException;

    Cliente updateCliente(long dni, Cliente cliente) throws ClienteNoExisteException;

    void deleteCliente(long dni) throws ClienteNoExisteException, CuentaNoExisteException;

    Cliente getClienteByCuentaId(long id) throws ClienteNoExisteException;
}
