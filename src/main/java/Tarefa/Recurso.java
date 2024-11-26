package Tarefa;

import org.json.JSONObject;

public class Recurso extends Material {

    private String fornecedor;

    public Recurso(String nome, String fornecedor, long valor, int quantidade) {
        super(nome, valor, quantidade);
        this.fornecedor = fornecedor;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String novoFornecedor) {
        this.fornecedor = novoFornecedor;
    }

    @Override
    public JSONObject getValoresJSON() {
        JSONObject jsonObject = super.getValoresJSON();
        jsonObject.put("fornecedor", fornecedor);
        return jsonObject;
    }
}
