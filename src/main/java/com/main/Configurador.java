package com.main;

import com.inputusuario.InputUsuario;
import com.tarefa.Recurso;
import com.tarefa.Residuo;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configurador {
    private final File  ARQUIVO_DE_CONFIGURACOES = new File("src/main/resources/config/config.json");
    private final Logger logger = Logger.getLogger(Configurador.class.getName());
    private final InputUsuario inputUsuario = new InputUsuario();
    private String nomeEmpresa;
    private String tipoEmpresa;
    private ArrayList<Recurso> recursos = new ArrayList<>();
    private ArrayList<Residuo> residuos = new ArrayList<>();
    private ArrayList<String> fornecedores = new ArrayList<>();
    private ArrayList<String> locaisDescarte = new ArrayList<>();

    // TODO: criar método para ler o arquivo "src/main/resources/config/config.json,
    //  usando a classe Leitor JSON"
    // TODO: criar método para interface gráfica modificar o arquivo de forma apropriada
    public Configurador() {
        // Garante que o arquivo de configurações existe
        try {
            logger.log(Level.INFO, String.valueOf(new File(ARQUIVO_DE_CONFIGURACOES.getParent()).mkdirs()));
            logger.log(Level.INFO, String.valueOf(ARQUIVO_DE_CONFIGURACOES.createNewFile()));
        } catch (IOException e) {
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
    }

    public void mudarConfiguracoes() {
        // TODO: método que vai perguntando as configurações ao usuário, ou mostra todas as configurações e permite modificar clicando em cima.
    }

    private ArrayList<String> pegarArrayListString(String nomeObjeto) {
        ArrayList<String> nomesObjetos = new ArrayList<>();
        while(true) {
            String nome = inputUsuario.tratarInputString(String.format("Qual o %s?", nomeObjeto));

            nomesObjetos.add(nome);

            int desejaContinuar = JOptionPane.showConfirmDialog(
                    null,
                    String.format("Deseja adicionar mais um %s?", nomeObjeto),
                    "Adicionar mais um?",
                    JOptionPane.YES_NO_OPTION
            );

            if (desejaContinuar != JOptionPane.YES_OPTION) break;
        }

        return nomesObjetos;
    }

    public ArrayList<String> pegarFornecedores() {
        return pegarArrayListString("fornecedor");
    }

    public ArrayList<String> pegarLocaisDescarte() {
        return pegarArrayListString("local de descarte");
    }
}

