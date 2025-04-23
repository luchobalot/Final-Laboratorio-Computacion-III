package ar.edu.utn.frbb.tup.service.implementation;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;

import ar.edu.utn.frbb.tup.controllers.dto.ClienteDto;
import ar.edu.utn.frbb.tup.models.Cliente;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaBancariaDao;
import ar.edu.utn.frbb.tup.service.ClienteService;
import ar.edu.utn.frbb.tup.models.exceptions.EdadNoValidaException;

@Service
public class ClienteServiceImp implements ClienteService {

    private ClienteDao clienteDao;
    private CuentaBancariaDao cuentaBancariaDao;

    @Autowired
    public ClienteServiceImp(ClienteDao clienteDao, CuentaBancariaDao cuentaBancariaDao) {
        this.clienteDao = clienteDao;
        this.cuentaBancariaDao = cuentaBancariaDao;
    }



    @Override
    public List<Cliente> obtenerAllClientes() {
        return clienteDao.getAllClientes();
    }

    @Override
    public Cliente obtenerCliente(long dni) throws ClienteNoExisteException { 
        return clienteDao.getCliente(dni);
    }

    /**
     * Crea un nuevo cliente y lo agrega a la base de datos.
     * 
     * @param clienteDto datos del cliente a crear
     * @return el cliente creado
     * @throws ClienteAlreadyExistsException si el cliente ya existe
     * @throws EdadNoValidaException         si la edad del cliente es menor a 18 años
     */
    @Override
    public Cliente crearCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException, EdadNoValidaException {
        LocalDate fechaHoy = LocalDate.now();
        LocalDate fechaLimite = fechaHoy.minus(Period.ofYears(18));
        
        if (clienteDto.getFechaNacimiento().isAfter(fechaLimite)){
            throw new EdadNoValidaException("No se pueden registrar personas de menos de 18 años");
        }
        Cliente cliente = new Cliente(clienteDto);
        try {
            return clienteDao.createCliente(cliente);
        } catch (ClienteAlreadyExistsException e) {
            throw e;
        }
    }

    /**
     * Actualiza un cliente existente en la base de datos.
     * 
     * @param dni        dni del cliente a actualizar
     * @param clienteDto datos del cliente a actualizar
     * @return el cliente actualizado
     * @throws ClienteNoExisteException si el cliente no existe
     * @throws EdadNoValidaException    si la edad del cliente es menor a 18 años
     */
    @Override
    public Cliente actualizarCliente(long dni,ClienteDto clienteDto) throws ClienteNoExisteException, EdadNoValidaException {
        LocalDate fechaHoy = LocalDate.now();
        LocalDate fechaLimite = fechaHoy.minus(Period.ofYears(18));
        
        if (clienteDto.getFechaNacimiento().isAfter(fechaLimite)){
            throw new EdadNoValidaException("No se pueden registrar personas de menos de 18 años");
        }

        //Crear un nuevo cliente y le mete las cuentas del cliente a modificar
        Cliente cliente = new Cliente(clienteDto);
        Cliente clienteAModificar = clienteDao.getCliente(dni);
        Set<CuentaBancaria> cuentas = clienteAModificar.getCuentas();
        cliente.setCuentas(cuentas);
        cliente.setDni(clienteAModificar.getDni());

        return clienteDao.updateCliente(dni, cliente);
    }

    /**
     * Borra un cliente existente en la base de datos.
     * 
     * @param dni dni del cliente a borrar
     * @throws ClienteNoExisteException si el cliente no existe
     * @throws CuentaNoExisteException  si el cliente tiene al menos una cuenta
     */
    @Override
    public void borrarCliente(long dni) throws ClienteNoExisteException, CuentaNoExisteException {
        cuentaBancariaDao.deleteCuentasPorTitular(dni);
        try {
            clienteDao.deleteCliente(dni);
        } catch (ClienteNoExisteException e) {
            throw e;
        }
    }


}
