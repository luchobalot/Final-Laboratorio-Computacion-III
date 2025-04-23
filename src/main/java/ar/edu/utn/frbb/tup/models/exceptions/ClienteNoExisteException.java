package ar.edu.utn.frbb.tup.models.exceptions;

public class ClienteNoExisteException extends Throwable {
    public ClienteNoExisteException(String message){
        super(message);
    }
}
