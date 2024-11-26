package Tarefa;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tarefa {

    private String nome;
    private Status status = Status.EM_ANDAMENTO;
    private ArrayList<String> funcionarios;
    private ArrayList<Material> materiais;
    private ArrayList<Residuo> residuos;

    public Tarefa(String nome, ArrayList<String> funcionarios, ArrayList<Material> materiais, ArrayList<Residuo> residuos) {
        this.nome = nome;
        this.funcionarios = funcionarios;
        this.materiais = materiais;
        this.residuos = residuos;
    }

    public void salvarTarefa() {
        JSONObject jsonTarefa = new JSONObject();

        jsonTarefa.put("status", getStatus());
        jsonTarefa.put("funcionarios", getFuncionarios());
        jsonTarefa.put("materiais", getMateriais());
        jsonTarefa.put("residuos", getResiduos());

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

    public ArrayList<Material> getMateriais() {
        return materiais;
    }

    public void setMateriais(ArrayList<Material> materiais) {
        this.materiais = materiais;
    }

    public ArrayList<Residuo> getResiduos() {
        return residuos;
    }

    public void setResiduos(ArrayList<Residuo> residuos) {
        this.residuos = residuos;
    }
}
