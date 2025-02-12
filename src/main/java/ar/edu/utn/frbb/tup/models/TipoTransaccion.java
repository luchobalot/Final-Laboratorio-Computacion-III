package ar.edu.utn.frbb.tup.models;

public enum TipoTransaccion {
    DEBITO("D"),
    CREDITO("C");

    private final String value;

    public String getValue(){
        return value;
    }

    TipoTransaccion(String value) {
        this.value = value;
    }

    public static TipoTransaccion fromString (String text) throws IllegalArgumentException{
        for (TipoTransaccion tipo : TipoTransaccion.values()) {
            if (tipo.value.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un tipo de Transaccion con el valor: " + text);
    }
}
