package Tarefa;

public class Residuo {
    private String nome;
    private String localDiscarte;
    private long custo;
    private int quantidade;

    public Residuo(String nome, String localDiscarte, long custo, int quantidade) {
        this.nome = nome;
        this.localDiscarte = localDiscarte;
        this.custo = custo;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
        this.nome = novoNome;
    }

    public String getLocalDiscarte() {
        return localDiscarte;
    }

    public void setLocalDiscarte(String novoLocalDiscarte) {
        this.localDiscarte = novoLocalDiscarte;
    }

    public long getCusto() {
        return custo;
    }

    public String getStringCusto() {
        return String.valueOf(custo);
    }

    public void setCusto(long novoCusto) {
        this.custo = novoCusto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int novaQuantidade) {
        this.quantidade = novaQuantidade;
    }
}
