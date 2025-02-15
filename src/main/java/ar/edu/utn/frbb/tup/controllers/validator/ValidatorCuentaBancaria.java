package ar.edu.utn.frbb.tup.controllers.validator;

import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controllers.dto.CuentaBancariaDto;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;

@Component
public class ValidatorCuentaBancaria {
    public void validate(CuentaBancariaDto cuentaBancariaDto) throws DataNoValidException {
        validateTitular(cuentaBancariaDto.getTitular());
        validateTipo(cuentaBancariaDto.getTipoCuenta());
        validateMoneda(cuentaBancariaDto.getMoneda());
    }

    // Validación deL DNI
    private void validateTitular(long dni) throws DataNoValidException {
        if (dni < 1) {
            throw new DataNoValidException("Error: El DNI no puede ser negativo o 0.");
        } else if (String.valueOf(dni).length() > 8) {
            throw new DataNoValidException("Error: El DNI debe ser de 8 digitos o menos.");
        }
    }

    // Validación de tipo de cuenta
    private void validateTipo(String tipo) throws DataNoValidException{
        if (!tipo.equalsIgnoreCase("CC") && !tipo.equalsIgnoreCase("CA") 
            && !tipo.equalsIgnoreCase("CUENTA CORRIENTE") && !tipo.equalsIgnoreCase("CAJA DE AHORRO")){ 
            throw new DataNoValidException("Error: " + tipo + " no es un tipo valido (CC para CUENTA CORRIENTE o CA pára CAJA DE AHORRO)");
        }
    }

    // Validación de moneda
    private void validateMoneda(String moneda) throws DataNoValidException{
        if (!moneda.equalsIgnoreCase("USD") && !moneda.equalsIgnoreCase("ARS") 
                && !moneda.equalsIgnoreCase("DOLARES") && !moneda.equalsIgnoreCase("PESOS")){
            throw new DataNoValidException("Error: " + moneda + " no es una moneda valida (USD, ARS, DOLARES o PESOS)");
        }
    }
}

