package ar.edu.utn.frbb.tup.models;

import org.springframework.stereotype.Component;

@Component
public class Banco {
    // Limite de saldo negativo
    private static final float LIMITE_SOBREGIRO = -100000;

    // Porcentaje comision transferencia
    private static final float COMISION_TRANSFER_ARS = 0.02f;
    private static final float COMISION_TRANSFER_USD = 0.005f;

    // A partir de cuando se cobra comision
    private static final float LIMITE_COMISION_ARS = 1000000;
    private static final float LIMITE_COMISION_USD = 5000;

    // Mensajes de recibo transferencias
    private static final String EXITO_ENTRE_BANCOS = ("La transferencia entre diferentes bancos fue exitosa.");
    private static final String EXITO_EN_BANCO = ("La transferencia en cuentas del mismo banco fue exitosa.");
    private static final String MENSAJE_ERROR = ("Error del sisteama al realizar la transferencia, por favor, intente de nuevo mas tarde.");
    private static final String ERROR_SALDO_INSUFICIENTE = ("Error. No tiene suficiente saldo.");

    public float getLimiteSobregiro() {
        return LIMITE_SOBREGIRO;
    }

    public float getComisionTransferArs() {
        return COMISION_TRANSFER_ARS;
    }

    public float getComisionTransferUsd() {
        return COMISION_TRANSFER_USD;
    }

    public float getLimiteComisionArs() {
        return LIMITE_COMISION_ARS;
    }

    public float getLimiteComisionUsd() {
        return LIMITE_COMISION_USD;
    }

    public String getExitoEntreBancos() {
        return EXITO_ENTRE_BANCOS;
    }

    public String getExitoEnBanco() {
        return EXITO_EN_BANCO;
    }

    public String getMensajeError() {
        return MENSAJE_ERROR;
    }

    public String getSaldoInsuficiente(){
        return ERROR_SALDO_INSUFICIENTE;
    }
}
