package Tarefa;

import LeitorJson.LeitorJson;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GerenciarTarefas {
    // TODO: criar classe para o gerenciamento de tarefas, listagem, exclução, gerenciamento, etc.
    private final File pastaTarefas = new File("src/main/resources/tarefas/");
    private final LeitorJson leitorJson = new LeitorJson();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public GerenciarTarefas() {
        logger.log(Level.INFO, String.valueOf(pastaTarefas.mkdirs()));
    }

    public ArrayList<Tarefa> lerArquivosTarefas() {
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        File[] arquivosTarefas = pastaTarefas.listFiles();

        if (arquivosTarefas == null) return tarefas;

        for (var arquivo: arquivosTarefas) {
            tarefas.add(new Tarefa(arquivo.getName(), leitorJson.lerArquivo(arquivo.getPath())));
        }

        return tarefas;
    }

    public void excluirTarefa(String nomeTarefa) {
        // TODO: deleta o arquivo de uma tarefa com base no nome da tarefa.
        //  Pede confirmação ao usuário antes de deletar
    }

    public Tarefa pegarTarefaPeloNome(String nomeTarefa) throws TarefaNaoEncontrada {
        File[] arquivosTarefa = pastaTarefas.listFiles();

        // TarefaNaoEncontrada é uma classe que herda da classe Exception.
        // Criado para deixar a exceção mais fácil de entender
        if (arquivosTarefa == null) throw new TarefaNaoEncontrada("Arquivo não encontrado");

        for (var arquivo: arquivosTarefa) {
            if (arquivo.getName().equals(nomeTarefa + ".json")) {
                return new Tarefa(arquivo.getName(), leitorJson.lerArquivo(arquivo.getPath()));
            }
        }

        throw new TarefaNaoEncontrada("Arquivo não encontrado");
    }

    public int pegarTarefaPeloNome(String nomeTarefa, ArrayList<Tarefa> tarefas) {
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).getNome().equals(nomeTarefa)) {
                return i;
            }
        }

        // retorna -1 caso a tarefa não tenha sido encontrada
        return -1;
    }
}
