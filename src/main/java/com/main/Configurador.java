package com.main;

import com.inputusuario.CancelarOperacao;
import com.inputusuario.InputUsuario;
import com.leitorjson.LeitorJson;
import com.tarefa.Recurso;
import com.tarefa.Residuo;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configurador {
    private final File  ARQUIVO_DE_CONFIGURACOES = new File("src/main/resources/config/config.json");
    private final Logger logger = Logger.getLogger(Configurador.class.getName());
    private final InputUsuario inputUsuario = new InputUsuario();
    private final LeitorJson leitorJson = new LeitorJson();
    private String nomeEmpresa;
    private String tipoEmpresa;
    private ArrayList<Recurso> recursos = new ArrayList<>();
    private ArrayList<Residuo> residuos = new ArrayList<>();
    private ArrayList<String> fornecedores = new ArrayList<>();
    private ArrayList<String> locaisDescarte = new ArrayList<>();

    // TODO: criar método para ler o arquivo "src/main/resources/config/config.json,
    //  usando a classe Leitor JSON"
    // TODO: criar método para interface gráfica modificar o arquivo de forma apropriada
    public Configurador() {
        // Garante que o arquivo de configurações existe
        try {
            logger.log(Level.INFO, String.valueOf(new File(ARQUIVO_DE_CONFIGURACOES.getParent()).mkdirs()));
            logger.log(Level.INFO, String.valueOf(ARQUIVO_DE_CONFIGURACOES.createNewFile()));
        } catch (IOException e) {
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
    }

    public void criarConfiguracoes() {
        try {
            setNomeEmpresa(inputUsuario.tratarInputString("Qual o nome da empresa?"));
            setTipoEmpresa(inputUsuario.tratarInputString("Qual o tipo da empresa?"));

            int adicionarRecursos = JOptionPane.showConfirmDialog(
                    null,
                    "Adicionar recursos?",
                    "ADICIONAR RECURSOS?",
                    JOptionPane.YES_NO_OPTION
            );

            if (adicionarRecursos == JOptionPane.YES_OPTION) {
                pegarFornecedores();
                pegarRecursos();
            }

            int adicionarResiduos = JOptionPane.showConfirmDialog(
                    null,
                    "Adicionar resíduos?",
                    "ADICIONAR RESÍDUOS?",
                    JOptionPane.YES_NO_OPTION
            );

            if (adicionarResiduos == JOptionPane.YES_OPTION) {
                pegarLocaisDescarte();
                pegarResiduos();
            }

        } catch (CancelarOperacao e) {
            JOptionPane.showMessageDialog(null, "Operação cancelada.");
            return;
        }

        JSONObject configuracoesJson = new JSONObject();
        configuracoesJson.put("nomeEmpresa", getNomeEmpresa());
        configuracoesJson.put("tipoEmpresa", getTipoEmpresa());
        configuracoesJson.put("fornecedores", getFornecedores());
        configuracoesJson.put("locaisDescarte", getLocaisDescarte());
        configuracoesJson.put("recursos", getRecursosJson());
        configuracoesJson.put("residuos", getResiduosJson());

        try(FileWriter fileWriter = new FileWriter(ARQUIVO_DE_CONFIGURACOES)) {
            configuracoesJson.write(fileWriter, 4, 0);
        } catch (IOException e) {
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
    }

    public void lerConfiguracoes() {
        var jsonData = leitorJson.lerArquivo(ARQUIVO_DE_CONFIGURACOES.getPath());

        if (jsonData.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Nenhuma configuração foi encontrada",
                    "CONFIGURAÇÃO NÃO ENCONTRADA",
                    JOptionPane.WARNING_MESSAGE
            );

            criarConfiguracoes();
            return;
        }

        setNomeEmpresa(jsonData.optString("nomeEmpresa"));
        setTipoEmpresa(jsonData.optString("tipoEmpresa"));
        setFornecedores(pegarLocais(jsonData.getJSONArray("fornecedores")));
        setLocaisDescarte(pegarLocais(jsonData.getJSONArray("locaisDescarte")));
        lerRecursos(jsonData.getJSONArray("recursos"));
        lerResiduos(jsonData.getJSONArray("residuos"));
    }

    private ArrayList<String[]> lerMateriais(JSONArray informacoes, String chaveLocal) {
        ArrayList<String[]> materiais = new ArrayList<>();

        for (int i = 0; i < informacoes.length(); i++) {
            String[] informacoesMaterial = new String[3];
            JSONObject informacoesMaterialJson = informacoes.getJSONObject(i);

            informacoesMaterial[0] = informacoesMaterialJson.optString("nome");
            informacoesMaterial[1] = informacoesMaterialJson.optString(chaveLocal);
            informacoesMaterial[2] = informacoesMaterialJson.optString("valor");

            materiais.add(informacoesMaterial);
        }

        return materiais;
    }

    private void lerRecursos(JSONArray recursosJson) {
        ArrayList<Recurso> recursos = new ArrayList<>();
        var materiais = lerMateriais(recursosJson, "fornecedor");

        for (var material: materiais) {
            String nome = material[0];
            String fornecedor = material[1];
            long valor = Long.parseLong(material[2]);

            recursos.add(new Recurso(nome, fornecedor, valor, 0));
        }

        setRecursos(recursos);
    }

    private void lerResiduos(JSONArray residuosJson) {
        ArrayList<Residuo> residuos = new ArrayList<>();
        var materias = lerMateriais(residuosJson, "localDescarte");

        for (var material: materias) {
            String nome = material[0];
            String localDescarte = material[1];
            long valor = Long.parseLong(material[2]);

            residuos.add(new Residuo(nome, localDescarte, valor, 0));
        }

        setResiduos(residuos);
    }

    private ArrayList<String[]> pegarMateriais(String nomeMaterial, String nomeLocal, ArrayList<String> opcoes, String nomeOpcao) throws CancelarOperacao {
        ArrayList<String[]> materiais = new ArrayList<>();

        while (true) {
            String nome = inputUsuario.tratarInputString(String.format("Qual o nome do %s?", nomeMaterial));

            String local = inputUsuario.escolhaDeLista(opcoes, nomeOpcao);

            if (local.equals("Outro")) {
                local = inputUsuario.tratarInputString(String.format("Qual o %s?", nomeLocal));
                fornecedores.add(local);
            }

            String custoString = inputUsuario.tratarInputString("Qual o custo?(em centavos de real) ");

            materiais.add(new String[]{nome, local, custoString});

            int desejaContinuar = JOptionPane.showConfirmDialog(
                    null,
                    String.format("Deseja adicionar mais um %s?", nomeMaterial),
                    "Adicionar mais um?",
                    JOptionPane.YES_NO_OPTION
            );

            if (desejaContinuar != JOptionPane.YES_OPTION) break;
        }

        return materiais;
    }

    private ArrayList<String> pegarLocais(JSONArray informacoes) {
        ArrayList<String> locais = new ArrayList<>();

        for (int i = 0; i < informacoes.length(); i++) {
            locais.add((String) informacoes.get(i));
        }

        return locais;
    }

    public void pegarRecursos() throws CancelarOperacao {
        ArrayList<Recurso> recursos = new ArrayList<>();
        var materiais = pegarMateriais("recurso", "fornecedor", getFornecedores(), "fornecedor");

        for (var material: materiais) {
            recursos.add(new Recurso(material[0], material[1], Long.parseLong(material[2]), 0));
        }

        setRecursos(recursos);
    }

    public void pegarResiduos() throws CancelarOperacao {
        ArrayList<Residuo> residuos = new ArrayList<>();
        var materiais = pegarMateriais("resíduo", "local de descarte", getLocaisDescarte(), "local de descarte");

        for (var material: materiais) {
            residuos.add(new Residuo(material[0], material[1], Long.parseLong(material[2]), 0));
        }

        setResiduos(residuos);
    }

    private ArrayList<String> pegarArrayListNomeObjeto(String nomeObjeto) throws CancelarOperacao {
        ArrayList<String> nomesObjetos = new ArrayList<>();
        while(true) {
            String nome = inputUsuario.tratarInputString(String.format("Qual o %s?", nomeObjeto));

            nomesObjetos.add(nome);

            int desejaContinuar = JOptionPane.showConfirmDialog(
                    null,
                    String.format("Deseja adicionar mais um %s?", nomeObjeto),
                    "Adicionar mais um?",
                    JOptionPane.YES_NO_OPTION
            );

            if (desejaContinuar != JOptionPane.YES_OPTION) break;
        }

        return nomesObjetos;
    }

    public void pegarFornecedores() throws CancelarOperacao {
        setFornecedores(pegarArrayListNomeObjeto("fornecedor"));
    }

    public void pegarLocaisDescarte() throws CancelarOperacao {
        setLocaisDescarte(pegarArrayListNomeObjeto("local de descarte"));
    }

    private void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    private void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    private void setFornecedores(ArrayList<String> fornecedores) {
        this.fornecedores = fornecedores;
    }

    private void setLocaisDescarte(ArrayList<String> locaisDescarte) {
        this.locaisDescarte = locaisDescarte;
    }

    private void setResiduos(ArrayList<Residuo> residuos) {
        this.residuos = residuos;
    }

    private void setRecursos(ArrayList<Recurso> recursos) {
        this.recursos = recursos;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public ArrayList<String> getFornecedores() {
        return fornecedores;
    }

    public ArrayList<String> getLocaisDescarte() {
        return locaisDescarte;
    }

    public ArrayList<Recurso> getRecursos() {
        return recursos;
    }

    public ArrayList<Residuo> getResiduos() {
        return residuos;
    }

    public JSONArray getRecursosJson() {
        JSONArray recursosJson = new JSONArray();

        for (var recurso: recursos) {
            recursosJson.put(recurso.getValoresJSON());
        }

        return recursosJson;
    }

    public JSONArray getResiduosJson() {
        JSONArray residuosJson = new JSONArray();

        for (var residuo: residuos) {
            residuosJson.put(residuo.getValoresJSON());
        }

        return residuosJson;
    }
}

