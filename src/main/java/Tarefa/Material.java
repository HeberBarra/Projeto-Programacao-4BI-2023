package Tarefa;

class Material {
    private String nome;
    private long valor;
    private int quantidade;

    public Material(String nome, long valor, int quantidade) {
        this.nome = nome;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getValor() {
        return valor;
    }

    public String getValorString() {
        String centavos = String.valueOf(valor);
        return String.format("R$%s,%s", centavos.substring(0, centavos.length() - 2), centavos.substring(centavos.length() - 2));
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public long calcularValorTotal() {
        return quantidade * valor;
    }

    public String calcularValorTotalString() {
        String centavos = String.valueOf(calcularValorTotal());
        return String.format("R$%s,%s", centavos.substring(0, centavos.length() - 2), centavos.substring(centavos.length() - 2));
    }
}
