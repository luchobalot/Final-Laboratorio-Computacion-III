package ar.edu.utn.frbb.tup.controllers.validator;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controllers.dto.ClienteDto;
import ar.edu.utn.frbb.tup.models.exceptions.DataNoValidException;
import ar.edu.utn.frbb.tup.models.exceptions.EdadNoValidaException;

@Component
public class ValidatorCliente {
    public void validate(ClienteDto clienteDto) throws DataNoValidException { 
        validateNombreApellido(clienteDto.getNombre(), clienteDto.getApellido());
        validateDni(clienteDto.getDni());
        validateFechaNacimiento(clienteDto.getFechaNacimiento());
        validateMail(clienteDto.getMail());
        validateTelefono(clienteDto.getTelefono());
        validateTipo(clienteDto.getTipo());
    }

    // Validación de nombre y apellido
    private void validateNombreApellido(String nombre, String apellido) throws DataNoValidException {
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+")) {
            throw new DataNoValidException(nombre + " no es un nombre valido");
        } else if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+")) {
            throw new DataNoValidException(apellido + " no es un apellido valido");
        }
    }

    // Validación de DNI
    private void validateDni(long dni) throws DataNoValidException {
        if (dni < 1) {
            throw new DataNoValidException("Error: El DNI no puede ser negativo o 0.");
        } else if (String.valueOf(dni).length() > 8) {
            throw new DataNoValidException("Error: El DNI debe ser de 8 digitos.");
        }
    }

    // Validación de fecha de nacimiento
    private void validateFechaNacimiento(LocalDate fechaNacimiento) throws EdadNoValidaException {
        LocalDate fechaMinima = LocalDate.now().minusYears(18); // Fecha mínima para ser mayor de edad
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new EdadNoValidaException("La fecha de nacimiento no puede ser posterior a la fecha actual");
        } else if (fechaNacimiento.isAfter(fechaMinima)) {
            throw new EdadNoValidaException("Error: Usted no puede ser menor de edad.");
        }
    }
  
    // Validación de mail
    private void validateMail(String mail) throws DataNoValidException{
        if (!mail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new DataNoValidException("Error: " + mail + " no es un mail valido. Revise e intente nuevamente.");
        }
    }

    // Validación de telefono
    private void validateTelefono(String telefono) throws DataNoValidException{
        if (!telefono.matches("^\\+?[0-9]{9,15}$")) {
            throw new DataNoValidException("Error: " + telefono + " no es un telefono valido.");
        }
    }

    // Validación de tipo de persona
    private void validateTipo(String tipo) throws DataNoValidException{
        if (!tipo.equalsIgnoreCase("F") && !tipo.equalsIgnoreCase("J")
            && !tipo.equalsIgnoreCase("FISICA") && !tipo.equalsIgnoreCase("JURIDICA")) {
            throw new DataNoValidException("Error: " + tipo + " no es un tipo valido.");
        }
    }
}
