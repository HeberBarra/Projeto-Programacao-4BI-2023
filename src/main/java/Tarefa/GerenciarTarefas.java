package Tarefa;

import java.time.LocalDate;
import java.util.ArrayList;

public class GerenciarTarefas {
    // TODO: criar classe para o gerenciamento de tarefas, listagem, exclução, gerenciamento, etc.

    public ArrayList<String> lerArquivosTarefas() {
        // TODO: método que lê os arquivos do diretório/pasta src/main/resources/tarefas/
        //  e retorna objetos Tarefa com base nas informações salvas.

        // return só pra não dá erro, mude
        return new ArrayList<>();
    }

    public void excluirTarefa(String nomeTarefa) {
        // TODO: deleta o arquivo de uma tarefa com base no nome da tarefa.
        //  Pede confirmação ao usuário antes de deletar
    }

    public Tarefa pegarTarefaPeloNome(String nomeTarefa) {
        // TODO: método que procura uma tarefa no diretório tarefas
        //  e retorna um objeto Tarefa se a tarefa existir
        //  se a Tarefa não existir criar um erro avisando

        // return só pra não dá erro, mude
        return new Tarefa("placeholder", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), LocalDate.now());
    }

    public int pegarTarefaPeloNome(String nomeTarefa, ArrayList<Tarefa> tarefas) {
        // TODO: método alternativo que ao invés de procurar no disco,
        //  procura num ArrayList de tarefas e retorna o index

        // return só pra não dá erro, mude
        return 0;
    }
}
