package ar.edu.utn.frbb.tup.models.exceptions;

public class ClienteAlreadyExistsException extends Throwable{
    public ClienteAlreadyExistsException(String message){
        super(message);
    }
}
