package ar.edu.utn.frbb.tup.models.exceptions;

public class CuentaNoExisteException extends Throwable{
    public CuentaNoExisteException(String message){
        super(message);
    }
}
