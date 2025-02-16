package ar.edu.utn.frbb.tup.controllers.validator;

import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controllers.dto.CuentaBancariaDto;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoValidaException;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;

@Component
public class ValidatorCuentaBancaria {
    public void validate(CuentaBancariaDto cuentaBancariaDto) throws DataNoValidException, MonedaNoValidaException, MontoNoValidoException {
        validateTitular(cuentaBancariaDto.getTitular());
        validateTipo(cuentaBancariaDto.getTipoCuenta());
        validateMoneda(cuentaBancariaDto.getMoneda());
    }

    // Validación del DNI del titular
    private void validateTitular(long dni) throws DataNoValidException {
        if (dni < 1) {
            throw new DataNoValidException("Error: El DNI no puede ser negativo o 0.");
        } else if (String.valueOf(dni).length() > 8) {
            throw new DataNoValidException("Error: El DNI debe ser de 8 dígitos o menos.");
        }
    }

    // Validación de tipo de cuenta
    private void validateTipo(String tipo) throws DataNoValidException {
        if (!tipo.equalsIgnoreCase("CC") && !tipo.equalsIgnoreCase("CA") 
            && !tipo.equalsIgnoreCase("CUENTA CORRIENTE") && !tipo.equalsIgnoreCase("CAJA DE AHORRO")) { 
            throw new DataNoValidException("Error: " + tipo + " no es un tipo válido (CC para CUENTA CORRIENTE o CA para CAJA DE AHORRO)");
        }
    }

    // Validación de moneda
    private void validateMoneda(String moneda) throws MonedaNoValidaException {
        if (!moneda.equalsIgnoreCase("USD") && !moneda.equalsIgnoreCase("ARS") 
                && !moneda.equalsIgnoreCase("DOLARES") && !moneda.equalsIgnoreCase("PESOS")) {
            throw new MonedaNoValidaException("Error: " + moneda + " no es una moneda válida (USD, ARS, DOLARES o PESOS)");
        }
    }
}
