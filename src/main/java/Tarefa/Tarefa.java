package Tarefa;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tarefa {

    private String nome;
    private Status status = Status.EM_ANDAMENTO;
    private ArrayList<String> funcionarios;
    private ArrayList<Recurso> recursos;
    private ArrayList<Residuo> residuos;
    private LocalDate dataDeEntrega;

    public Tarefa(String nome, ArrayList<String> funcionarios, ArrayList<Recurso> materiais, ArrayList<Residuo> residuos, LocalDate dataDeEntrega) {
        this.nome = nome;
        this.funcionarios = funcionarios;
        this.recursos = materiais;
        this.residuos = residuos;
        this.dataDeEntrega = dataDeEntrega;
    }

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
}
