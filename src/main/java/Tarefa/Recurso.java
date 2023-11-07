package Tarefa;

import org.json.JSONObject;

public class Recurso extends Material {

    private String fornecedor;

    // super é usado para acessar um método herdado de uma classe,
    // de forma que ainda possamos fazer modificações no método, por exemplo, neste caso
    // o método super chama o construtor da classe Material,
    // porém o construtor da classe também salva uma propriedade a mais
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

    // utiliza o super para pegar os valores comuns,
    // mas ainda com a possibilidade de passar a propriedade exclusiva da classe
    // a anotação overide indica que este método sobreescrever o herdado de material
    @Override
    public JSONObject getValoresJSON() {
        JSONObject jsonObject = super.getValoresJSON();
        jsonObject.put("fornecedor", fornecedor);
        return jsonObject;
    }
}
