package Tarefa;

// Enum é uma estrutura que serve para definir um conjunto de valores possíveis
// para alguma coisa, neste os status possíveis de uma tarefa.
// É definido algumas constantes e passado um valor para cada um delas.
// Assim podemos acessar os valores facilmente e evitar erros de digitação
// e de valores não permitidos
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
