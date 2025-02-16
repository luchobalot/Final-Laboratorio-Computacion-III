package ar.edu.utn.frbb.tup.controllers.validator;

import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.models.exceptions.CuentasIgualesException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoValidaException;

@Component
public class ValidatorTransferencias {
    public void validate(TransferenciasDto transferenciasDto) throws CuentasIgualesException, CuentaNoExisteException, MonedaNoValidaException {
        validateCuentas(transferenciasDto.getCuentaOrigen(), transferenciasDto.getCuentaDestino());
        validateMoneda(transferenciasDto.getMoneda());
    }

    // Validación de cuentas
    private void validateCuentas(long cuentaOrigen, long cuentaDestino) throws CuentasIgualesException, CuentaNoExisteException {
        if (cuentaOrigen <= 0) {
            throw new CuentaNoExisteException("Error: El ID de la cuenta origen no puede ser negativo o 0.");
        } else if (cuentaDestino <= 0) {
            throw new CuentaNoExisteException("Error: El ID de la cuenta destino no puede ser negativo o 0.");
        } else if (cuentaOrigen == cuentaDestino) {
            throw new CuentasIgualesException("Error: La cuenta de origen y destino no pueden ser la misma.");
        }
    }

    // Validación del tipo de moneda
    private void validateMoneda(String moneda) throws MonedaNoValidaException {
        if (!moneda.equalsIgnoreCase("USD") && !moneda.equalsIgnoreCase("ARS") 
                && !moneda.equalsIgnoreCase("DOLARES") && !moneda.equalsIgnoreCase("PESOS")) {
            throw new MonedaNoValidaException("Error: El tipo de moneda debe ser USD o ARS.");
        }
    }
}
