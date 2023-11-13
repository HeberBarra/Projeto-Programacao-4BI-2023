package InputUsuario;

public enum OpcoesInput {
    CANCELAR("CANCELAR"),
    REPETIR("REPETIR");

    private final String valor;

    OpcoesInput(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
