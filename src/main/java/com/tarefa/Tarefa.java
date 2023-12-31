package com.tarefa;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tarefa {

    private String nome;
    private Status status = Status.EM_ANDAMENTO;
    private ArrayList<String> funcionarios;
    private ArrayList<Recurso> recursos;
    private ArrayList<Residuo> residuos;
    private LocalDate dataDeEntrega;

    public Tarefa(String nome, ArrayList<String> funcionarios, ArrayList<Recurso> recursos, ArrayList<Residuo> residuos, LocalDate dataDeEntrega) {
        this.nome = nome;
        this.funcionarios = funcionarios;
        this.recursos = recursos;
        this.residuos = residuos;
        this.dataDeEntrega = dataDeEntrega;
    }

    // Construtor alternativo que pega as informações de um arquivo JSON
    public Tarefa(String caminhoArquivo, JSONObject tarefaJSON) {
        // Separa o caminho em partes, pega a última parte, 
        // e retira a extensão .json para obter o nome da tarefa
        String[] partesCaminho = caminhoArquivo.split("/");
        String nomeArquivo = partesCaminho[partesCaminho.length - 1];
        this.nome = nomeArquivo.substring(0, nomeArquivo.length() - 5);

        // Criar listas que possuem valores pré-definidos para melhor controle das chaves
        List<String> chavesMaterial = Arrays.stream(new String[]{"nome", "valor", "quantidade"}).toList();
        ArrayList<String> chavesRecurso = new ArrayList<>(List.copyOf(chavesMaterial));
        chavesRecurso.add(1, "fornecedor");
        ArrayList<String> chavesResiduo = new ArrayList<>(List.copyOf(chavesMaterial));
        chavesResiduo.add(1, "localDiscarte");

        // HashMap que possui Strings e seus status equivalentes
        HashMap<String, Status> statusHashMap = new HashMap<>();
        statusHashMap.put("EM_ANDAMENTO", Status.EM_ANDAMENTO);
        statusHashMap.put("CONCLUIDO", Status.CONCLUIDO);
        statusHashMap.put("ATRASADO", Status.ATRASADO);
        statusHashMap.put("NAO_CONFORME", Status.NAO_CONFORME);
        statusHashMap.put("REPROVADO", Status.REPROVADO);

        // percorre todas as chaves que foram pegas do arquivo passado
        for (var key: tarefaJSON.keySet()) {
            var valorJson = tarefaJSON.get(key);

            switch (key) {
                // como as chaves não possuem uma ordem definida exata,
                // é necessário fazer uma seleção para executar um código baseado na informação

                // type cast é necessário, pois o compilador não sabe o tipo exato do valor
                // convertamos valorJson para String e salvamos o status equivalente 
                case "status" -> status = statusHashMap.get((String) valorJson);

                // lê os funcionários salvos no arquivo e depois salva no ArrayList da Tarefa
                case "funcionarios" -> {
                    ArrayList<String> funcionarios = new ArrayList<>();

                    for (var funcionario: (JSONArray) valorJson) {
                        funcionarios.add((String) funcionario);
                    }

                    this.funcionarios = funcionarios;
                }

                // Os cases recursos e residuos tem um funcionamentos similar.
                case "recursos" -> {
                    // ArrayList para salvar as informações
                    ArrayList<Recurso> recursos = new ArrayList<>();

                    // como os recursos ficam salvos numa lista;
                    // é necessário percorrê-la para pegar cada um deles
                    for (int i = 0; i < ((JSONArray) valorJson).length(); i++) {
                        // pega o objeto do index atual e converte para JSONObject
                        JSONObject recurso = (JSONObject) ((JSONArray) valorJson).get(i);
                        
                        // pega cada uma das informações do objeto
                        String nome = (String) recurso.get(chavesRecurso.get(0));
                        String fornecedor = (String) recurso.get(chavesRecurso.get(1));
                        long valor = (int) recurso.get(chavesRecurso.get(2));
                        int quantidade = (int) recurso.get(chavesRecurso.get(3));

                        // adiciona a ArrayList um objeto Recurso que as informações obtidas
                        recursos.add(new Recurso(nome, fornecedor, valor, quantidade));
                    }

                    this.recursos = recursos;
                }

                // basicamente o mesmo funcionamento da case anterior só que com a classe Residuo
                case "residuos" -> {
                    ArrayList<Residuo> residuos = new ArrayList<>();
                    for (int i = 0; i < ((JSONArray) valorJson).length(); i++) {
                        JSONObject residuo = (JSONObject) ((JSONArray) valorJson).get(i);
                        String nome = (String) residuo.get(chavesResiduo.get(0));
                        String localDescarte = (String) residuo.get(chavesResiduo.get(1));
                        long valor = (int) residuo.get(chavesResiduo.get(2));
                        int quantidade = (int) residuo.get(chavesResiduo.get(3));
                        residuos.add(new Residuo(nome, localDescarte, valor, quantidade));
                    }

                    this.residuos = residuos;
                }

                // a data de entrega é salva no formato ISO 8601, AAAA-MM-DD(A = ano, M = mês, D = dia),
                // separamos a data em ano, mês e dia e depois passamos para o método of do LocalDate
                // para criar um objeto de data
                case "dataDeEntrega" -> {
                    String[] partesData = ((String) valorJson).split("-");
                    this.dataDeEntrega = LocalDate.of(Integer.parseInt(partesData[0]), Integer.parseInt(partesData[1]), Integer.parseInt(partesData[2]));
                }
            }
        }
    }

    // salvas todas as informações essencias da tarefa, numa estrutura JSON
    // e depois escreve num arquivo com o nome da tarefa e com a extensão json
    public void salvarTarefa() {
        JSONObject jsonTarefa = new JSONObject();

        jsonTarefa.put("status", getStatus());
        jsonTarefa.put("funcionarios", getFuncionarios());
        jsonTarefa.put("recursos", getRecursosJSON());
        jsonTarefa.put("residuos", getResiduosJSON());
        jsonTarefa.put("dataDeEntrega", getDataDeEntrega());

        try (FileWriter fileWriter = new FileWriter(String.format("tarefas/%s.json", getNome()))) {
            jsonTarefa.write(fileWriter, 4, 0);
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Tarefa.class.getName());
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
        this.nome = String.join(" ", novoNome.strip().split(" "));
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status novoStatus) {
        this.status = novoStatus;
    }

    public ArrayList<String> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(ArrayList<String> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public ArrayList<Recurso> getRecursos() {
        return recursos;
    }

    // percorre o ArrayList que armazena os recursos
    // e utiliza o método getValoresJSON de cada para pegar suas informações
    // e armazena numa estrutura JSON
    public JSONArray getRecursosJSON() {
        JSONArray jsonArray = new JSONArray();
        for (var recurso: recursos) {
            jsonArray.put(recurso.getValoresJSON());
        }

        return jsonArray;
    }

    public void setRecursos(ArrayList<Recurso> recursos) {
        this.recursos = recursos;
    }

    public ArrayList<Residuo> getResiduos() {
        return residuos;
    }

    // percorre o ArrayList que armazena os resíduos
    // e utiliza o método getValoresJSON de cada para pegar suas informações
    // e armazena numa estrutura JSON
    public JSONArray getResiduosJSON() {
        JSONArray jsonArray = new JSONArray();
        for (var residuo: residuos) {
            jsonArray.put(residuo.getValoresJSON());
        }

        return jsonArray;
    }

    public void setResiduos(ArrayList<Residuo> residuos) {
        this.residuos = residuos;
    }

    public LocalDate getDataDeEntrega() {
        return dataDeEntrega;
    }

    public String getStringDataDeEntrega() {
        return dataDeEntrega.toString();
    }

    public void setDataDeEntrega(LocalDate dataDeEntrega) {
        this.dataDeEntrega = dataDeEntrega;
    }

    public void atualizarStatus() {
        int prazo = LocalDate.now().until(dataDeEntrega).getDays();

        if (prazo < 0 && status == Status.EM_ANDAMENTO) {
            setStatus(Status.ATRASADO);
        }

        salvarTarefa();
    }

    // calcula o prazo em dias, da data atual até a data salva.
    // Se a data já passou, os dias ficarão negativos
    public int calcularPrazo() {
        atualizarStatus();
        salvarTarefa();
        return LocalDate.now().until(dataDeEntrega).getDays();
    }
}
