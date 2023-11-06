package Tarefa;

public enum Status {

    EM_ANDAMENTO("em andamento"),
    CONCLUIDO("concluído"),
    ATRASADO("atrasado"),
    NAO_CONFORME("não conforme"),
    REPROVADO("reprovado");

    private final String nomeStatus;

    Status(String nome) {
        this.nomeStatus = nome;
    }

    public String getNomeStatus() {
        return nomeStatus;
    }
}
