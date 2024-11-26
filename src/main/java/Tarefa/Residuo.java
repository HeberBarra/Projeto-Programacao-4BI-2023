package Tarefa;

import org.json.JSONObject;

public class Residuo extends Material {
    private String localDiscarte;

    public Residuo(String nome, String localDiscarte, long valor, int quantidade) {
        super(nome, valor, quantidade);
        this.localDiscarte = localDiscarte;
    }

    public String getLocalDiscarte() {
        return localDiscarte;
    }

    public void setLocalDiscarte(String novoLocalDiscarte) {
        this.localDiscarte = novoLocalDiscarte;
    }

    @Override
    public JSONObject getValoresJSON() {
        JSONObject jsonObject = super.getValoresJSON();
        jsonObject.put("localDiscarte", localDiscarte);
        return jsonObject;
    }
}
