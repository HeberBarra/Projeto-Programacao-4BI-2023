package Tarefa;

public class CriarTarefa {

    private String nome;

    public CriarTarefa(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
        this.nome = String.join(" ", novoNome.strip().split(" "));
    }
}
