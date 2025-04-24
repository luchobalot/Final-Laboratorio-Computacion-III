package ar.edu.utn.frbb.tup.controllers.dto;

public class TransaccionResumenDto {
    private String fecha;
    private String tipo;
    private String descripcionBreve;
    private float monto;
    
    // Getters y setters
    public String getFecha() {
        return fecha;
    }
    
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getDescripcionBreve() {
        return descripcionBreve;
    }
    
    public void setDescripcionBreve(String descripcionBreve) {
        this.descripcionBreve = descripcionBreve;
    }
    
    public float getMonto() {
        return monto;
    }
    
    public void setMonto(float monto) {
        this.monto = monto;
    }
}