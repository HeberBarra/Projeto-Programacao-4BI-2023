package com.tarefa;

public class TarefaNaoEncontrada extends Exception {

    private final String mensagem;
    public TarefaNaoEncontrada(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
