package ar.edu.utn.frbb.tup.controllers.validator;

import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;

@Component
public class ValidatorTransferencias {
    public void validate(TransferenciasDto transferenciasDto) throws DataNoValidException {
        validateCuentas(transferenciasDto.getCuentaOrigen(), transferenciasDto.getCuentaDestino());
        validateMoneda(transferenciasDto.getMoneda());
    }

    // Validación de cuentas
    private void validateCuentas(long titular1, long titular2) throws DataNoValidException{
        if (titular1 < 1){
            throw new DataNoValidException("Error: El ID de la cuenta origen no puede ser negativo o 0.");
        } else if (titular2 < 1){
            throw new DataNoValidException("Error: El ID de la cuenta destino no puede ser negativo o 0.");
        }
    }

    // Validación del tipo de moneda
    private void validateMoneda(String moneda) throws DataNoValidException{
        if (!moneda.equalsIgnoreCase("USD") && !moneda.equalsIgnoreCase("ARS") 
                && !moneda.equalsIgnoreCase("DOLARES") && !moneda.equalsIgnoreCase("PESOS")){
            throw new DataNoValidException("Error: El tipo de moneda debe ser USD o ARS.");
        }
    }
}
