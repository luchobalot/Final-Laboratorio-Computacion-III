package ar.edu.utn.frbb.tup.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import ar.edu.utn.frbb.tup.controllers.dto.ClienteDto;

public class Cliente extends Persona {
      private TipoCliente tipo; // Fisica o Juridica
      private String banco;
      private LocalDate fechaAlta;
      private Set<CuentaBancaria> cuentas; // Conjunto de cuentas bancarias del cliente

      public Cliente(ClienteDto clienteDto){
            super(clienteDto.getNombre(), clienteDto.getApellido(), clienteDto.getDni(), clienteDto.getFechaNacimiento(), clienteDto.getMail(), clienteDto.getTelefono());
            if (clienteDto.getTipo().equalsIgnoreCase("F") || clienteDto.getTipo().equalsIgnoreCase("FISICA")){
                this.tipo = TipoCliente.PERSONA_FISICA;
            }else{
                this.tipo = TipoCliente.PERSONA_JURIDICA;
            }
            this.banco = "Banco ISBC";
            this.fechaAlta = LocalDate.now();
            this.cuentas = new HashSet<>();
        }
      
}
