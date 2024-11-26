package com.tarefa;

import org.json.JSONObject;

public class Residuo extends Material {
    private String localDescarte;

    // super é usado para acessar um método herdado de uma classe,
    // de forma que ainda possamos fazer modificações no método, por exemplo, neste caso
    // o método super chama o construtor da classe Material,
    // porém o construtor da classe também salva uma propriedade a mais
    public Residuo(String nome, String localDescarte, long valor, int quantidade) {
        super(nome, valor, quantidade);
        this.localDescarte = localDescarte;
    }

    public String getLocalDescarte() {
        return localDescarte;
    }

    public void setLocalDescarte(String novoLocalDiscarte) {
        this.localDescarte = novoLocalDiscarte;
    }

    // utiliza o super para pegar os valores comuns,
    // mas ainda com a possibilidade de passar a propriedade exclusiva da classe
    // a anotação overide indica que este método sobreescrever o herdado de material
    @Override
    public JSONObject getValoresJSON() {
        JSONObject jsonObject = super.getValoresJSON();
        jsonObject.put("localDiscarte", localDescarte);
        return jsonObject;
    }
}
