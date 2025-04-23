package ar.edu.utn.frbb.tup.persistence;

import java.util.List;

import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;


public interface TransferenciasDao {
    List<Transferencias> getAllTransfers();

    void transferBetweenBanks(Transferencias transferencia) throws ClienteNoExisteException;

    void transferInBank(Transferencias transferencia) throws ClienteNoExisteException;

    void deleteTransfers();
}
