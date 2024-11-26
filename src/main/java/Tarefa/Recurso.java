package Tarefa;

public class Recurso extends Material {

    private String fornecedor;

    public Recurso(String nome, String fornecedor, long valor, int quantidade) {
        super(nome, valor, quantidade);
        this.fornecedor = fornecedor;
    }


    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String novoFornecedor) {
        this.fornecedor = novoFornecedor;
    }
}
