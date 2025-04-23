package ar.edu.utn.frbb.tup.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frbb.tup.controllers.dto.CuentaBancariaDto;

public class CuentaBancaria {
      private long titular;
      private long idCuenta;
      private float saldo;
      private TipoCuenta tipoCuenta;
      private TipoMoneda moneda; 
      private LocalDate fechaApertura;
      private List<Transferencias> transferencias;
      private List<Transacciones> transacciones;

      public CuentaBancaria(CuentaBancariaDto cuentaBancariaDto) {
            this.titular = cuentaBancariaDto.getTitular();
            this.idCuenta = 0;
            this.saldo = cuentaBancariaDto.getSaldo();
            if (cuentaBancariaDto.getTipoCuenta().equalsIgnoreCase("CC")
                || cuentaBancariaDto.getTipoCuenta().equalsIgnoreCase("CUENTA CORRIENTE")) {
                this.tipoCuenta = TipoCuenta.CUENTA_CORRIENTE;
            } else {
                this.tipoCuenta = TipoCuenta.CAJA_AHORRO;
            }
            if (cuentaBancariaDto.getMoneda().equalsIgnoreCase("USD") 
                || cuentaBancariaDto.getMoneda().equalsIgnoreCase("DOLARES")) {
                this.moneda = TipoMoneda.DOLARES;
            } else  {
                this.moneda = TipoMoneda.PESOS_ARG;
            }
            this.fechaApertura = LocalDate.now();
            this.transferencias = new ArrayList<>();
            this.transacciones = new ArrayList<>();
        }
    
        //Contructor de testeos
        public CuentaBancaria(long titular, float saldo, String strTipoCuenta, String strMoneda) {
            this.titular = titular;
            this.idCuenta = 0;
            this.saldo = saldo;
            if (strTipoCuenta.equals("CC")) {
                this.tipoCuenta = TipoCuenta.CUENTA_CORRIENTE;
            } else {
                this.tipoCuenta = TipoCuenta.CAJA_AHORRO;
            }
            if (strMoneda.equals("USD")) {
                this.moneda = TipoMoneda.DOLARES;
            } else  {
                this.moneda = TipoMoneda.PESOS_ARG;
            }
            this.fechaApertura = LocalDate.now();
            this.transferencias = new ArrayList<>();
            this.transacciones = new ArrayList<>();
        }
    
        public CuentaBancaria() {
            this.transferencias = new ArrayList<>();
            this.transacciones = new ArrayList<>();
        }
    
        public long getTitular() {
            return titular;
        }
        public void setTitular(long titular) {
            this.titular = titular;
        }
    
    
        public long getIdCuenta() {
            return idCuenta;
        }
        public void setIdCuenta(long idCuenta) {
            this.idCuenta = idCuenta;
        }
    
    
        public float getSaldo() {
            return saldo;
        }
        public void setSaldo(float saldo) {
            this.saldo = saldo;
        }
        public void addDeposito(float monto) {
            this.saldo += monto;
        }
        public void addRetiro(float monto) {
            this.saldo -= monto;
        }
    
    
        public TipoCuenta getTipoCuenta() {
            return tipoCuenta;
        }
        public void setTipoCuenta(TipoCuenta tipoCuenta) {
            this.tipoCuenta = tipoCuenta;
        }
    
    
        public TipoMoneda getMoneda() {
            return moneda;
        }
        public void setMoneda(TipoMoneda moneda) {
            this.moneda = moneda;
        }
    
    
        public LocalDate getFechaApertura() {
            return fechaApertura;
        }
        public void setFechaApertura(LocalDate fechaApertura) {
            this.fechaApertura = fechaApertura;
        }
    
    
        public List<Transferencias> getTransferencias() {
            return transferencias;
        }
        public void setTransferencias(List<Transferencias> transferencias) {
            this.transferencias = transferencias;
        }
        public void addTransferencia(Transferencias transferencia) {
            this.transferencias.add(transferencia);
        }
        
        
        public List<Transacciones> getTransacciones() {
            return transacciones;
        }
        public void setTransacciones(List<Transacciones> transacciones) {
            this.transacciones = transacciones;
        }
        public void addTransacciones(Transacciones transacciones) {
            this.transacciones.add(transacciones);
        }
      
}
