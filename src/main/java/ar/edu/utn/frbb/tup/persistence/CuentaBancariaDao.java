package ar.edu.utn.frbb.tup.persistence;

import java.util.List;

import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.Transacciones;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;

public interface CuentaBancariaDao {
    List<CuentaBancaria> getAllCuentasBancarias();
    
    CuentaBancaria getCuentaBancariaById(long id) throws CuentaNoExisteException;
    
    List<Transacciones> getTransaccionesById(long id) throws CuentaNoExisteException;
    
    List<Transferencias> getTransferenciasById(long id) throws CuentaNoExisteException;

    CuentaBancaria createCuentaBancaria(CuentaBancaria cuentaBancaria) throws ClienteNoExisteException;

    CuentaBancaria addDeposito(Cliente cliente, long id, float monto) throws CuentaNoExisteException, ClienteNoExisteException;

    CuentaBancaria addRetiro(Cliente cliente, long id, float monto) throws CuentaNoExisteException, ClienteNoExisteException;

    void deleteCuentaBancaria(Cliente cliente, long id) throws CuentaNoExisteException, ClienteNoExisteException;

    void deleteCuentasPorTitular(long dni);

    void addTransferBetweenBanks(Transferencias transferencia);
    
    void addTransferInBank(Transferencias transferencia);

    void addTransaccion(Transacciones transaccion);
}