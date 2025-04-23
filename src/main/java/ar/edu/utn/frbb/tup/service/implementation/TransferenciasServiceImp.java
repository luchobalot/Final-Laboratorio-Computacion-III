package ar.edu.utn.frbb.tup.service.implementation;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.models.Banco;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.Recibo;
import ar.edu.utn.frbb.tup.models.TipoEstadoTransfers;
import ar.edu.utn.frbb.tup.models.TipoMoneda;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentasIgualesException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;
import ar.edu.utn.frbb.tup.models.exceptions.SaldoNoValidoException;
import ar.edu.utn.frbb.tup.persistence.CuentaBancariaDao;
import ar.edu.utn.frbb.tup.persistence.TransferenciasDao;
import ar.edu.utn.frbb.tup.service.TransferenciasService;

@Service
public class TransferenciasServiceImp implements TransferenciasService {

    private TransferenciasDao transferenciasDao;
    private CuentaBancariaDao cuentaBancariaDao;
    private Banco banco;

    @Autowired
    public TransferenciasServiceImp(TransferenciasDao transferenciasDao, CuentaBancariaDao cuentaBancariaDao, Banco banco) {
        this.cuentaBancariaDao = cuentaBancariaDao;
        this.transferenciasDao = transferenciasDao;
        this.banco = banco;
    }


    @Override
    public List<Transferencias> obtenerAllTransferencias() {
        return transferenciasDao.getAllTransfers();
    }

    /**
     * Crea una nueva transferencia entre dos cuentas.
     * 
     * @param transferenciasDto Contiene los datos de la transferencia a realizar.
     * @return Un objeto Recibo que contiene el estado de la transferencia.
     * @throws CuentasIgualesException         Si la cuenta de origen es igual a la cuenta de destino.
     * @throws MontoNoValidoException         Si el monto no es válido (negativo o 0).
     * @throws CuentaNoExisteException        Si la cuenta de origen o destino no existe.
     * @throws SaldoNoValidoException         Si el saldo de la cuenta de origen es insuficiente.
     * @throws ClienteNoExisteException       Si el cliente no existe.
     * @throws MonedaNoCoincideException      Si la moneda no coincide entre las dos cuentas.
     */
    @Override
    public Recibo crearTransferencia(TransferenciasDto transferenciasDto) throws CuentasIgualesException, MontoNoValidoException, CuentaNoExisteException, SaldoNoValidoException, ClienteNoExisteException, MonedaNoCoincideException {
        //Excepciones iniciales 
        if (transferenciasDto.getCuentaOrigen() == transferenciasDto.getCuentaDestino()) {
            throw new CuentasIgualesException("La cuenta de origen no puede ser igual a la cuenta de destino");
        } else if (transferenciasDto.getMonto() <= 0) {
            throw new MontoNoValidoException("El monto no puede ser negativo o 0");
        }
        
        Recibo recibo = new Recibo();
        boolean tranferEntreBancos;
        CuentaBancaria cuentaOrigen = cuentaBancariaDao.getCuentaBancariaById(transferenciasDto.getCuentaOrigen());
        
        //Caso de saldo insuficiente 
        if (cuentaOrigen.getSaldo() - transferenciasDto.getMonto() < banco.getLimiteSobregiro()) {
            recibo.setEstado(TipoEstadoTransfers.FALLIDA);
            recibo.setMensaje(banco.getSaldoInsuficiente());
            return recibo;
        }

        CuentaBancaria cuentaDestino = new CuentaBancaria();
        //Comprobar como será la transferencia
        try {
            cuentaDestino = cuentaBancariaDao.getCuentaBancariaById(transferenciasDto.getCuentaDestino());
            tranferEntreBancos = false;
        } catch(CuentaNoExisteException e) {
            tranferEntreBancos = true;
        }

        //Caso de transferencia erronea aleatoriamente
        if (!exitoTransferencia(tranferEntreBancos)){
            recibo.setEstado(TipoEstadoTransfers.ERROR);
            recibo.setMensaje(banco.getMensajeError());
            return recibo;
        }

        //Caso de transferencia exitosa
        Transferencias transferencia = sacarComision(new Transferencias(transferenciasDto));
        if (!tranferEntreBancos){
            if (cuentaOrigen.getMoneda() != cuentaDestino.getMoneda() 
                    || transferencia.getMoneda() != cuentaOrigen.getMoneda()
                    || transferencia.getMoneda() != cuentaDestino.getMoneda()) {
                throw new MonedaNoCoincideException("La moneda no coincide");
            }
        } else if (cuentaOrigen.getMoneda() != transferencia.getMoneda()) {
            throw new MonedaNoCoincideException("La moneda no coincide");
        }

        if (tranferEntreBancos) {
            transferenciasDao.transferBetweenBanks(transferencia);
            recibo.setEstado(TipoEstadoTransfers.EXITOSA);
            recibo.setMensaje(banco.getExitoEntreBancos());
            return recibo;
        } else {
            transferenciasDao.transferInBank(transferencia);
            recibo.setEstado(TipoEstadoTransfers.EXITOSA);
            recibo.setMensaje(banco.getExitoEnBanco());
            return recibo;
        }
    }

    public Transferencias sacarComision(Transferencias transferencia) {
        if (transferencia.getMoneda() == TipoMoneda.PESOS_ARG) {
            //COMISION PESOS
            if (transferencia.getMonto() > banco.getLimiteComisionArs()){
                float comision = transferencia.getMonto() * banco.getComisionTransferArs();
                transferencia.setMonto(transferencia.getMonto() - comision);
                transferencia.setComision(comision);
                return transferencia;
            } else {
                return transferencia;
            }
        } else {
            //COMISION DOLARES
            if (transferencia.getMonto() > banco.getLimiteComisionUsd()){
                float comision = transferencia.getMonto() * banco.getComisionTransferUsd();
                transferencia.setMonto(transferencia.getMonto() - comision);
                transferencia.setComision(comision);
                return transferencia;
            } else {
                return transferencia;
            }
        }
    }

    public boolean exitoTransferencia(boolean entreBancos){
        Random random = new Random();
        //TENES UN 5% DE CHANCE DE FALLAR si la transf es entre bancos
        //TENES UN 1% DE CHANCE DE FALLAR si la transf es entre cuentas del mismo banco
        if (entreBancos){
            if (random.nextInt(100) < 5){
                return false;
            } else {
                return true;
            }
        } else {
            if (random.nextInt(100) < 1){
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void borrarAllTransferencias(){
        transferenciasDao.deleteTransfers();
    }
}
