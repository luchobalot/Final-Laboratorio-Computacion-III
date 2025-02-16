package ar.edu.utn.frbb.tup.models.exceptions;

public class CuentaAlreadyExistsException extends Throwable{
    public CuentaAlreadyExistsException(String message){
        super(message);
    }
}
