package ar.edu.utn.frbb.tup.service;

import java.util.List;

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.models.Recibo;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentasIgualesException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;
import ar.edu.utn.frbb.tup.models.exceptions.SaldoNoValidoException;

public interface TransferenciasService {
    List<Transferencias> obtenerAllTransferencias();
    
    Recibo crearTransferencia(TransferenciasDto transferenciasDto) throws CuentasIgualesException, MontoNoValidoException, CuentaNoExisteException, SaldoNoValidoException, ClienteNoExisteException, MonedaNoCoincideException;

    void borrarAllTransferencias();
}
