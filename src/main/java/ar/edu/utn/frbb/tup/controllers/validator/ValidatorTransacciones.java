package ar.edu.utn.frbb.tup.controllers.validator;

import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controllers.dto.TransaccionesDto;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;

@Component
public class ValidatorTransacciones {
    public void validate(TransaccionesDto transaccionesDto) throws DataNoValidException {
        validateIDCuenta(transaccionesDto.getIdCuenta());
        validateTipo(transaccionesDto.getTipo());
    }

    // Validación de ID de cuenta
    private void validateIDCuenta(long idCuenta) throws DataNoValidException {
        if (idCuenta <= 0) {
            throw new DataNoValidException("Error: El ID de la cuenta no puede ser negativo o 0.");
        }
    }

    // Validación de tipo de transacción
    private void validateTipo(String tipo) throws DataNoValidException {
        if (!tipo.equalsIgnoreCase("DEBITO") && !tipo.equalsIgnoreCase("CREDITO")
            && !tipo.equalsIgnoreCase("D") && !tipo.equalsIgnoreCase("C")) {
            throw new DataNoValidException("Error: " + tipo + " no es un tipo valido de transaccion (DEBITO o CREDITO).");
        }
    }
}
