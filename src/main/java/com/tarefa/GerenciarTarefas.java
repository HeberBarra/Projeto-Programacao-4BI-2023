package com.tarefa;

import com.leitorjson.LeitorJson;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GerenciarTarefas {
    private final File pastaTarefas = new File("tarefas/");
    private final LeitorJson leitorJson = new LeitorJson();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public GerenciarTarefas() {
        // O método mkdirs garante que as pastas sejam criadas para que erros não aconteçam
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
        // Garante que o nome da tarefa seja um caminho apropriado
        if (!nomeTarefa.contains(pastaTarefas.getPath())) { nomeTarefa = pastaTarefas.getPath() + "/" + nomeTarefa; }
        if (!nomeTarefa.contains(".json")) nomeTarefa += ".json";

        File arquivoTarefa = new File(nomeTarefa);
        logger.log(Level.INFO, String.valueOf(arquivoTarefa.delete()));
    }

    public Tarefa pegarTarefaPeloNome(String nomeTarefa) {
        File[] arquivosTarefa = pastaTarefas.listFiles();

        // TarefaNaoEncontrada é uma classe que herda da classe Exception.
        // Criado para deixar a exceção mais fácil de entender
        if (arquivosTarefa == null) return null;

        for (var arquivo: arquivosTarefa) {
            if (arquivo.getName().equals(nomeTarefa + ".json")) {
                return new Tarefa(arquivo.getName(), leitorJson.lerArquivo(arquivo.getPath()));
            }
        }

        return null;
    }
}
