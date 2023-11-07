package Tarefa;

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

    public Tarefa(String caminhoArquivo, JSONObject tarefaJSON) {
        String[] partesCaminho = caminhoArquivo.split("/");
        String nomeArquivo = partesCaminho[partesCaminho.length - 1];
        this.nome = nomeArquivo.substring(0, nomeArquivo.length() - 5);

        System.out.println(tarefaJSON);
        List<String> chavesTarefa = Arrays.stream(new String[]{"status", "funcionarios", "recursos", "residuos", "dataDeEntrega"}).toList();
        List<String> chavesMaterial = Arrays.stream(new String[]{"nome", "valor", "quantidade"}).toList();
        ArrayList<String> chavesRecurso = new ArrayList<>(List.copyOf(chavesMaterial));
        chavesRecurso.add(1, "fornecedor");
        ArrayList<String> chavesResiduo = new ArrayList<>(List.copyOf(chavesMaterial));
        chavesResiduo.add(1, "localDiscarte");

        HashMap<String, Status> statusHashMap = new HashMap<>();
        statusHashMap.put("EM_ANDAMENTO", Status.EM_ANDAMENTO);
        statusHashMap.put("CONCLUIDO", Status.CONCLUIDO);
        statusHashMap.put("ATRASADO", Status.ATRASADO);
        statusHashMap.put("NAO_CONFORME", Status.NAO_CONFORME);
        statusHashMap.put("REPROVADO", Status.REPROVADO);

        for (var key: tarefaJSON.keySet()) {
            var valorJson = tarefaJSON.get(key);

            switch (key) {

                case "status" -> status = statusHashMap.get((String) valorJson);

                case "funcionarios" -> {
                    ArrayList<String> funcionarios = new ArrayList<>();

                    for (var funcionario: (JSONArray) valorJson) {
                        funcionarios.add((String) funcionario);
                    }

                    this.funcionarios = funcionarios;
                }

                case "recursos" -> {
                    ArrayList<Recurso> recursos = new ArrayList<>();
                    for (int i = 0; i < ((JSONArray) valorJson).length(); i++) {
                        JSONObject recurso = (JSONObject) ((JSONArray) valorJson).get(i);
                        String nome = (String) recurso.get(chavesRecurso.get(0));
                        String fornecedor = (String) recurso.get(chavesRecurso.get(1));
                        long valor = (int) recurso.get(chavesRecurso.get(2));
                        int quantidade = (int) recurso.get(chavesRecurso.get(3));

                        recursos.add(new Recurso(nome, fornecedor, valor, quantidade));
                    }

                    this.recursos = recursos;
                }

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

        try (FileWriter fileWriter = new FileWriter(String.format("src/main/resources/tarefas/%s.json", getNome()))) {
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

    // calcula o prazo em dias, da data atual até a data salva.
    // Se a data já passou, os dias ficarão negativos
    public int calcularPrazo() {
        return LocalDate.now().until(dataDeEntrega).getDays();
    }
}
