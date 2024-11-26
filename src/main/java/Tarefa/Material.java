package Tarefa;

public class Material {

    private String nome;
    private String fornecedor;
    private long valor;
    private int quantidade;

    public Material(String nome, String fornecedor,long valor, int quantidade) {
        this.nome = nome;
        this.fornecedor = fornecedor;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
        this.nome = novoNome;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String novoFornecedor) {
        this.fornecedor = novoFornecedor;
    }

    public long getValor() {
        return valor;
    }

    public String getStringValor() {
        return String.valueOf(valor);
    }

    public void setValor(long novoValor) {
        this.valor = novoValor;
    }

    public void setValor(String novoValor) {
        this.valor = Long.parseLong(novoValor) * 100;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int novoQuantidade) {
        this.quantidade = novoQuantidade;
    }

    public long calcularPrecoTotal() {
        return quantidade * valor;
    }
}
