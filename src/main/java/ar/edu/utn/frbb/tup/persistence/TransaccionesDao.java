package ar.edu.utn.frbb.tup.persistence;

import java.util.List;

import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;

public interface TransaccionesDao {

    List<Transacciones> getAllTransacciones();

    Transacciones createTransaccion(Transacciones transaccion) throws ClienteNoExisteException;

    void deleteTransacciones();
}
