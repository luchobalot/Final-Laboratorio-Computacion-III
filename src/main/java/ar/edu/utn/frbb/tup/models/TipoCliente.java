package ar.edu.utn.frbb.tup.models;

public enum TipoCliente {
      PERSONA_FISICA("F"),
      PERSONA_JURIDICA("J");

      private final String value; // Valor del tipo de cliente

      public String getValue() {
            return value;
      }

      TipoCliente(String value) {
            this.value = value;
      }

      public static TipoCliente fromString(String text) throws IllegalArgumentException{
            for (TipoCliente tipo : TipoCliente.values()) {
                if (tipo.value.equalsIgnoreCase(text)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("No se pudo encontrar un tipo de Cliente con el valor: " + text);
        }
}
