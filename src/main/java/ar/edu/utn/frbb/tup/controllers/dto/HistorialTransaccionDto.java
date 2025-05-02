package ar.edu.utn.frbb.tup.controllers.dto;

import java.util.List;

public class HistorialTransaccionDto {
    private String numeroCuenta;
    private List<TransaccionResumenDto> transacciones;
    
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    public List<TransaccionResumenDto> getTransacciones() {
        return transacciones;
    }
    
    public void setTransacciones(List<TransaccionResumenDto> transacciones) {
        this.transacciones = transacciones;
    }
}