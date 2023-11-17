package com.main;

import com.inputusuario.CancelarOperacao;
import com.tarefa.GerenciarTarefas;

public class Main {
    // TODO: criar classe de controle principal

    private final GerenciarTarefas gerenciarTarefas = new GerenciarTarefas();
    private final Configurador configurador = new Configurador();

    public static void main(String[] args) {
        Main main = new Main();
        main.configurador.lerConfiguracoes();
        main.configurador.mudarConfiguracoes();
    }

    // TODO: método que mostrar todas as tarefas em forma de lista,
    //  mostrando apenas o nome, o status, o prazo e a data de entrega
    public void mostrarTarefas() {

    }

    // TODO: método para gerenciar uma tarefa, mostra as informações necessárias
    //  (nome, status, funcionários, recursos e resíduos com as informações deles, prazo e data de entrega)
    //  permitir modificação das informações usando eventos(essa parte se quiser posso fazer separado)
    public void gerenciarTarefa() {

    }

    // TODO: mostra um menu de opções, que contará com opções numa caixa de diálogo para ver
    //  tarefas, configuraçõese sair do programa
    //  0 - ver tarefas
    //  1 - configurações
    //  2 - sair do programa
    public int menuOpcoes() {
        // return só pra não dá erro, mude
        return 0;
    }
}