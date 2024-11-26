package Tarefa;

import org.json.JSONObject;

// Classe pai das classes Recurso e Residuo,
// criada para evitar repetição de código, pois ambas são muito parecidas
class Material {
    private String nome;

    // o valor é armazenado em centavos para maior precisão
    private long valor;
    private int quantidade;

    public Material(String nome, long valor, int quantidade) {
        this.nome = nome;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getValor() {
        return valor;
    }

    // retorna o valor formatado como reais, exemplo, 200 vira R$2,00.
    // o método substring é usado para pegar uma parte específica de uma String
    public String getValorString() {
        String centavos = String.valueOf(valor);
        return String.format("R$%s,%s", centavos.substring(0, centavos.length() - 2), centavos.substring(centavos.length() - 2));
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public long calcularValorTotal() {
        return quantidade * valor;
    }

    // pega as informações de um material e passa para uma estrutura JSON,
    // é usada para salvar em arquivo
    public JSONObject getValoresJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nome", nome);
        jsonObject.put("valor", valor);
        jsonObject.put("quantidade", quantidade);
        return jsonObject;
    }

    public String calcularValorTotalString() {
        String centavos = String.valueOf(calcularValorTotal());
        return String.format("R$%s,%s", centavos.substring(0, centavos.length() - 2), centavos.substring(centavos.length() - 2));
    }
}
