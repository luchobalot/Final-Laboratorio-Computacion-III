package ar.edu.utn.frbb.tup.models;

import java.time.LocalDate;
import java.time.Period;

public class Persona {
      private String nombre;
      private String apellido;
      private long dni;
      private LocalDate fechaNacimiento;
      private String mail;
      private String telefono;

      public Persona(){}

      public Persona(String nombre, String apellido, long dni, LocalDate fechaNacimiento, String mail, String telefono) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.dni = dni;
            this.fechaNacimiento = fechaNacimiento;
            this.mail = mail;
            this.telefono = telefono;
    }


      public String getNombre() {
            return nombre;
      }
      public void setNombre(String nombre) {
            this.nombre = nombre;
      }


      public String getApellido() {
            return apellido;
      }
      public void setApellido(String apellido) {
            this.apellido = apellido;
      }


      public long getDni() {
            return dni;
      }
      public void setDni(long dni) {
            this.dni = dni;
      }


      public LocalDate getFechaNacimiento() {
            return fechaNacimiento;
      }
      public void setFechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
      }


      public String getMail() {
            return mail;
      }
      public void setMail(String mail) {
            this.mail = mail;
      }


      public String getTelefono() {
            return telefono;
      }
      public void setTelefono(String telefono) {
            this.telefono = telefono;
      }


      public int getEdad() {
            LocalDate FechaHoy = LocalDate.now();
            Period edad = Period.between(fechaNacimiento, FechaHoy);
            return edad.getYears();
      }
  
}
