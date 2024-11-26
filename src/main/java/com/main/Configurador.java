package com.main;

import com.inputusuario.CancelarOperacao;
import com.inputusuario.InputUsuario;
import com.inputusuario.OpcoesInput;
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

    public void mudarConfiguracoes() throws CancelarOperacao {
        String[] opcoesConfiguracoes = {
                "Nome da Empresa",
                "Tipo da Empresa",
                "Fornecedores",
                "Locais de Descarte",
                "Recursos",
                "Resíduos"
        };

        String escolha = (String) JOptionPane.showInputDialog(
                null,
                "Escolha uma configuração por favor",
                "ESCOLHA UM",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesConfiguracoes,
                opcoesConfiguracoes[0]
        );

        switch (escolha) {
            case "Nome da Empresa" -> mudarNome();
            
            case "Tipo da Empresa" -> mudarTipo();
            
            case "Fornecedores" -> mudarFornecedores();
            
            case "Locais de Descarte" -> mudarLocaisDescarte();
            
            default -> {
                int sair = JOptionPane.showConfirmDialog(null, "Deseja sair?", "SAIR?", JOptionPane.YES_NO_OPTION);
                if (sair == JOptionPane.YES_OPTION) throw new CancelarOperacao();
            }
        }
    }

    private ArrayList<String> mudarListaString(ArrayList<String> listaInformacoes, String nomeLista, String nomeIndividual) throws CancelarOperacao {
        // StringBuilder informacoes = new StringBuilder();
        String[] opcoesInformacoes = new String[listaInformacoes.size()];
        String[] opcoesIndividuais = {
                "Apagar um " + nomeIndividual,
                "Mudar um " + nomeIndividual,
                "Criar um " + nomeIndividual
        };

        int opcaoEscolhida = JOptionPane.showOptionDialog(
                null,
                "Escolha uma opcao",
                "ESCOLHA UM",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesIndividuais,
                opcoesIndividuais[0]
        );

        switch (opcaoEscolhida) {
            case 0 -> {
                String escolha = inputUsuario.escolhaDeLista(listaInformacoes, nomeIndividual);
                listaInformacoes.remove(escolha);
            }

            case 1 -> {
                String escolha = inputUsuario.escolhaDeLista(listaInformacoes, nomeIndividual);
                String novo = inputUsuario.tratarInputString(String.format("Qual o novo valor para %s?", escolha));
                int index = listaInformacoes.indexOf(escolha);
                listaInformacoes.set(index, novo);
            }

            case 2 -> listaInformacoes.add(inputUsuario.tratarInputString(String.format("Qual o novo %s?", nomeIndividual)));

            default -> {
                int escolha = JOptionPane.showConfirmDialog(null, "Deseja sair?", "SAIR?", JOptionPane.YES_NO_OPTION);
                if (escolha == JOptionPane.YES_OPTION) throw new CancelarOperacao();
            }
        }

        return listaInformacoes;
    }

    private void mudarFornecedores() {
        try {
            setFornecedores(mudarListaString(fornecedores, "fornecedores", "fornecedor"));
        } catch (CancelarOperacao e) {
            JOptionPane.showMessageDialog(null, "Operação Cancelada.");
        }
    }

    private void mudarLocaisDescarte() {
        try {
            setLocaisDescarte(mudarListaString(locaisDescarte, "locais de descarte", "local de descarte"));
        } catch (CancelarOperacao e) {
            JOptionPane.showMessageDialog(null, "Operação Cancelada.");
        }
    }
    
    private String mudarString(String nomeValor, String valor) throws CancelarOperacao {
        int mudar = JOptionPane.showConfirmDialog(
                null,
                String.format("%s atual %s. Deseja mudá-lo", nomeValor, valor),
                "ESCOLHA UM",
                JOptionPane.YES_NO_OPTION
        );

        if (mudar != JOptionPane.YES_OPTION) throw new CancelarOperacao();

        return inputUsuario.tratarInputString(String.format("Qual o novo %s?", nomeValor));
    }

    private void mudarNome() {
        try {
            setNomeEmpresa(mudarString("nome da empresa", getNomeEmpresa()));
        } catch (CancelarOperacao e) {
            JOptionPane.showMessageDialog(null, "Mudança cancelada.");
        }
    }

    private void mudarTipo() {
        try {
            setTipoEmpresa(mudarString("tipo da empresa", getTipoEmpresa()));
        } catch (CancelarOperacao e) {
            JOptionPane.showMessageDialog(null, "Mudança cancelada.");
        }
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

