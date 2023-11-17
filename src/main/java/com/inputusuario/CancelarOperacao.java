package com.inputusuario;

public class CancelarOperacao extends Exception {
    private final String mensagem;
    public CancelarOperacao(String mensagem) {
        this.mensagem = mensagem;
    }

    public CancelarOperacao() {
        this.mensagem = OpcoesInput.CANCELAR.getValor();
    }

    public String getMensagem() {
        return mensagem;
    }
}
