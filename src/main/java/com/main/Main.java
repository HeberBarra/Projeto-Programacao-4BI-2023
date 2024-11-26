package com.main;

import com.inputusuario.CancelarOperacao;
import com.inputusuario.InputUsuario;
import com.tarefa.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private final GerenciarTarefas gerenciarTarefas = new GerenciarTarefas();
    private final Configurador configurador = new Configurador();
    private final InputUsuario inputUsuario = new InputUsuario();
    private static Main main;
    private Tarefa tarefaAtual;

    public static void main(String[] args) {
        main = new Main();
        main.configurador.lerConfiguracoes();
        main.menuOpcoes();
    }

    public void mostrarTarefas() {
        final int FRAME_WIDTH = 1200;
        final int FRAME_HEIGHT = 700;
        // Atomic permite um compartilhamento seguro de um valor de uma variável. Principalmente entre threads.
        // Necessário para poder passar o valor para o método do WindowAdapter
        AtomicBoolean flag = new AtomicBoolean(false);

        ArrayList<Tarefa> tarefas = gerenciarTarefas.lerArquivosTarefas();
        JFrame frameTarefas = new JFrame();

        frameTarefas.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frameTarefas.setLocationRelativeTo(null);
        frameTarefas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frameTarefas.setTitle("TAREFAS");


        Object[][] dados = new Object[tarefas.size()][5];

        for (int i = 0; i < tarefas.size(); i++) {
            tarefas.get(i).atualizarStatus();
            dados[i] = new Object[]{
                    tarefas.get(i).getNome(),
                    tarefas.get(i).getStatus(),
                    tarefas.get(i).calcularPrazo(),
                    tarefas.get(i).getDataDeEntrega(),
            };
        }

        String[] headers = {"NOME", "STATUS", "PRAZO", "DATA DE ENTREGA"};

        JTable table = new JTable(dados, headers) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JScrollPane jScrollPane = new JScrollPane(table);
        frameTarefas.add(jScrollPane);

        table.setCellSelectionEnabled(true);
        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cellSelectionModel.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) return;

            flag.set(true);
            int linhaSelecionada = table.getSelectedRow();
            String nomeTarefa = (String) table.getValueAt(linhaSelecionada, 0);

            tarefaAtual = gerenciarTarefas.pegarTarefaPeloNome(nomeTarefa);
            frameTarefas.dispose();
            gerenciarTarefa();
        });

        frameTarefas.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e){
                super.windowClosed(e);
                if (!flag.get()) {
                    main.menuOpcoes();
                }
            }
        });

        frameTarefas.setVisible(true);
    }

    public void gerenciarTarefa() {
        String[] opcoes = {"Voltar", "Modificar", "Mostrar Recursos", "Mostrar Resíduos", "Salvar", "Excluir"};

        loop:
        while (true) {

            int escolha = JOptionPane.showOptionDialog(
                    null,
                    criarMensagem(tarefaAtual),
                    "O QUE DESEJA FAZER?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            switch (escolha) {
                // Voltar
                case 0 -> {
                    main.mostrarTarefas();
                    return;
                }

                // Modificar
                case 1 -> main.modificarTarefa();

                // Mostrar recursos
                case 2 -> {
                    if (tarefaAtual.getRecursos().isEmpty()) {
                        int opcaoEscolhida = JOptionPane.showConfirmDialog(
                                null,
                                "Não há recursos salvos! Deseja adicionar um? ",
                                "ADICIONAR?",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (opcaoEscolhida == JOptionPane.YES_OPTION) {
                            var listaRecursos = tarefaAtual.getRecursos();

                            try {
                                listaRecursos.add(converterParaRecurso(escolherNovoMaterial("Recurso")));
                                tarefaAtual.setRecursos(listaRecursos);
                                tarefaAtual.salvarTarefa();
                            } catch (CancelarOperacao e ) {
                                JOptionPane.showMessageDialog(null, "Operação cancelada");
                            }
                        }

                        continue;
                    }

                    modificarMateriais(new ArrayList<>(tarefaAtual.getRecursos()));
                }

                // Mostrar Resíduos
                case 3 -> {
                    if (tarefaAtual.getResiduos().isEmpty()) {
                        int opcaoEscolhida = JOptionPane.showConfirmDialog(
                                null,
                                "Não há resíduos salvos!",
                                "ADICIONAR?",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (opcaoEscolhida == JOptionPane.YES_OPTION) {
                            var listaResiduos = tarefaAtual.getResiduos();

                            try {
                                listaResiduos.add(converterParaResiduo(escolherNovoMaterial("Residuo")));
                                tarefaAtual.setResiduos(listaResiduos);
                            } catch (CancelarOperacao e ) {
                                JOptionPane.showMessageDialog(null, "Operação cancelada");
                            }
                        }

                        continue;
                    }

                    modificarMateriais(new ArrayList<>(tarefaAtual.getResiduos()));
                }

                // Salvar
                case 4 -> tarefaAtual.salvarTarefa();

                // Excluir
                case 5 -> {
                    int excluir = JOptionPane.showConfirmDialog(
                            null,
                            "Deseja a apagar a tarefa?",
                            "APAGAR?",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (excluir == JOptionPane.YES_OPTION) {
                        gerenciarTarefas.excluirTarefa(tarefaAtual.getNome());
                    }

                    main.mostrarTarefas();
                    return;
                }

                default -> {
                    int continuar = JOptionPane.showConfirmDialog(
                            null,
                            "Opção inválida! Deseja continuar?",
                            "CONTINUAR?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (continuar != JOptionPane.YES_OPTION) {
                        break loop;
                    }
                }
            }
        }
    }

    private void modificarTarefa() {
        String[] opcoesTarefa = {"Nome", "Status", "Data de Entrega", "Funcionários"};
        Status[] statuses = {
                Status.EM_ANDAMENTO,
                Status.ATRASADO,
                Status.CONCLUIDO,
                Status.NAO_CONFORME,
                Status.REPROVADO
        };

        String escolha;

        try {
            escolha = inputUsuario.escolhaDeLista(opcoesTarefa, "opção");


            switch (escolha) {

                case "Nome" -> {
                    String novoNome = inputUsuario.tratarInputString("Qual o novo da tarefa");
                    String nomeAntigo = tarefaAtual.getNome();
                    tarefaAtual.setNome(novoNome);
                    tarefaAtual.salvarTarefa();
                    gerenciarTarefas.excluirTarefa(nomeAntigo);
                }

                case "Status" -> {
                    Status novoStatus = inputUsuario.escolhaDeLista(statuses, "status");
                    tarefaAtual.setStatus(novoStatus);
                    tarefaAtual.salvarTarefa();
                }

                case "Data de Entrega" -> {
                    LocalDate novaData = inputUsuario.tratarInputData();

                    if (tarefaAtual.getStatus() == Status.ATRASADO) {
                        tarefaAtual.setStatus(Status.EM_ANDAMENTO);
                    }

                    tarefaAtual.setDataDeEntrega(novaData);
                    tarefaAtual.atualizarStatus();
                    tarefaAtual.salvarTarefa();
                }

                case "Funcionários" -> {
                    ArrayList<String> listaFuncionarios = tarefaAtual.getFuncionarios();
                    String[] botoesOpcoes = {"Voltar", "Modificar", "Excluir", "Próximo"};
                    do {
                        String opcaoEscolhida = inputUsuario.escolhaDeLista(new String[]{"Criar", "Visualizar", "Voltar"}, "opcao");

                        if (opcaoEscolhida.equals("Voltar")) {
                            return;
                        }

                        if (opcaoEscolhida.equals("Criar")) {
                            String novoFuncionario = inputUsuario.tratarInputString("Qual o novo funcionário");

                            if (tarefaAtual.getFuncionarios().contains(novoFuncionario)) {
                                JOptionPane.showMessageDialog(null, "Funcionário já existe");
                                continue;
                            }

                            listaFuncionarios.add(novoFuncionario);
                            tarefaAtual.setFuncionarios(listaFuncionarios);
                            tarefaAtual.salvarTarefa();
                            continue;
                        }


                        for (int i = 0; i < listaFuncionarios.size(); i++) {
                            int botaoEscolhido = JOptionPane.showOptionDialog(
                                    null,
                                    String.format("Funcionário: %s \nO que deseja fazer?", listaFuncionarios.get(i)),
                                    "ESCOLHA",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    botoesOpcoes,
                                    botoesOpcoes[0]
                            );

                            switch (botaoEscolhido) {

                                // Anterior
                                case 0 -> {
                                    if (i == 0) {
                                        i = listaFuncionarios.size() - 2;
                                        continue;
                                    }

                                    i -= 2;
                                }

                                // Modificar
                                case 1 -> {
                                    String novoNomeFuncionario = inputUsuario.tratarInputString("Qual o novo nome? ");

                                    if (novoNomeFuncionario.equals(listaFuncionarios.get(i))) {
                                        JOptionPane.showMessageDialog( null,"O novo nome não pode ser igual ao anterior","Erro",JOptionPane.ERROR_MESSAGE);
                                        i--;
                                        continue;
                                    }

                                    listaFuncionarios.set(i, novoNomeFuncionario);
                                    tarefaAtual.setFuncionarios(listaFuncionarios);
                                    tarefaAtual.salvarTarefa();
                                    JOptionPane.showMessageDialog(null, "Nome modificado");
                                    i--;
                                }

                                // Excluir
                                case 2 -> {
                                    int excluir = JOptionPane.showConfirmDialog(
                                            null,
                                            String.format("Deseja excluir o funcionário: %s?", listaFuncionarios.get(i)),
                                            "EXCLUIR",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE
                                    );

                                    if (excluir == JOptionPane.YES_OPTION) {
                                        listaFuncionarios.remove(i);
                                        tarefaAtual.setFuncionarios(listaFuncionarios);
                                        tarefaAtual.salvarTarefa();
                                        return;
                                    }
                                }

                                // Próximo
                                case 3 -> {
                                    if (i == listaFuncionarios.size() - 1) {
                                        i = -1;
                                    }
                                }
                            }
                        }
                    } while (true);
                }
            }

        } catch (CancelarOperacao e) {
            JOptionPane.showMessageDialog(null, "Operação cancelada!");
        }

    }

    private void modificarMateriais(ArrayList<Material> materiais) {
        String tipoMaterial;

        if (materiais.get(0).getClass() == Recurso.class) {
            tipoMaterial = "Recurso";
        } else {
            tipoMaterial = "Residuo";
        }

        String[] opcoes = {"Voltar", "Mudar Quantidade", "Adicionar", "Deletar", "Próximo", "Sair"};

        for (int i = 0; i < materiais.size(); i++) {
            int escolha = JOptionPane.showOptionDialog(
                    null,
                    String.format(
                        "Nome: %s \n%s: %s \nValor: %s \nQuantidade: %d \nTotal: %s",
                        materiais.get(i).getNome(),
                        materiais.get(i).getNomeLocal(),
                        materiais.get(i).getLocal(),
                        materiais.get(i).getValorString(),
                        materiais.get(i).getQuantidade(),
                        materiais.get(i).calcularValorTotalString()
                    ),
                    "O QUE DESEJA FAZER?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            switch (escolha) {
                // Voltar
                case 0 -> {
                    if (i == 0) {
                        i = materiais.size() - 2;
                        continue;
                    }

                    i -= 2;
                }

                // Mudar quantidade
                case 1 -> {
                    try {
                        while (true) {
                            int novaQuantidade = inputUsuario.tratarInputInt("Qual a nova quantidade? ");

                            if (novaQuantidade < 1) {
                                JOptionPane.showMessageDialog(null, "Deve-se adicionar ao menos um!");
                                continue;
                            }

                            materiais.get(i).setQuantidade(novaQuantidade);
                            break;
                        }
                    } catch (CancelarOperacao e) {
                        JOptionPane.showMessageDialog(null, "Operação cancelada");
                        return;
                    }
                }


                // Adicionar
                case 2 -> {
                    try {
                        materiais.add(escolherNovoMaterial(tipoMaterial));
                    } catch (CancelarOperacao e) {
                        JOptionPane.showMessageDialog(null, "Operação cancelada!");
                        return;
                    }

                    if (tipoMaterial.equals("Recurso")) {
                        tarefaAtual.setRecursos(converterParaRecursos(materiais));
                        return;
                    }

                    tarefaAtual.setResiduos(converterParaResiduos(materiais));
                    return;
                }

                // Deletar
                case 3 -> {
                    materiais.remove(i);

                    if (tipoMaterial.equals("Recurso")) {
                        tarefaAtual.setRecursos(converterParaRecursos(materiais));
                        return;
                    }

                    tarefaAtual.setResiduos(converterParaResiduos(materiais));
                    return;
                }
                // Próximo
                case 4 -> {
                    if (i == materiais.size() - 1) {
                        i = -1;
                    }
                }
                // Sair
                case 5 -> {
                    return;
                }
            }
        }

    }

    private ArrayList<Recurso> converterParaRecursos(ArrayList<Material> materiais) {
        ArrayList<Recurso> recursos = new ArrayList<>();

        for (var material: materiais) {
            recursos.add((Recurso) material);
        }

        return recursos;
    }

    private Recurso converterParaRecurso(Material material) {
        return new Recurso(material.getNome(), material.getLocal(), material.getValor(), material.getQuantidade());
    }

    private ArrayList<Residuo> converterParaResiduos(ArrayList<Material> materiais) {
        ArrayList<Residuo> residuos = new ArrayList<>();

        for (var material: materiais) {
            residuos.add((Residuo) material);
        }

        return residuos;
    }

    private Residuo converterParaResiduo(Material material) {
        return new Residuo(material.getNome(), material.getLocal(), material.getValor(), material.getQuantidade());
    }

    private Material escolherNovoMaterial(String tipo) throws CancelarOperacao {
        ArrayList<Material> materiais;
        ArrayList<String> locais;
        ArrayList<String> nomesMateriais = new ArrayList<>();
        String nomeLocal;

        if (tipo.equals("Recurso")) {
            materiais = new ArrayList<>(configurador.getRecursos());
            locais = configurador.getFornecedores();
            nomeLocal = "Fornecedor";
        } else {
            materiais = new ArrayList<>(configurador.getResiduos());
            locais = configurador.getLocaisDescarte();
            nomeLocal = "Local de descarte";
        }

        for (var material: materiais) {
            nomesMateriais.add(material.getNome());
        }

        var materialEscolhido = inputUsuario.escolhaDeLista(nomesMateriais, tipo.toLowerCase());

        if (!materialEscolhido.equals("Outro")) {
            int quantidade;

            while (true) {
                quantidade = inputUsuario.tratarInputInt("Qual a quantidade?");

                if (quantidade < 1) {
                    JOptionPane.showMessageDialog(null, "É necessário adicionar ao menos um!");
                    continue;
                }
                break;
            }

            for (var material: materiais) {
                if (materialEscolhido.equals(material.getNome())) {
                    material.setQuantidade(quantidade);
                    return material;
                }
            }
        }

        String[] informacoesMaterial = criarNovoMaterial(tipo, nomeLocal, locais);

        if (tipo.equals("Recurso")) {
            return new Recurso(
                    informacoesMaterial[0],
                    informacoesMaterial[1],
                    Long.parseLong(informacoesMaterial[2]),
                    Integer.parseInt(informacoesMaterial[3])
            );
        }

        return new Residuo(
                informacoesMaterial[0],
                informacoesMaterial[1],
                Long.parseLong(informacoesMaterial[2]),
                Integer.parseInt(informacoesMaterial[3])
        );
    }

    private String[] criarNovoMaterial(String tipo, String nomeLocal, ArrayList<String> locais) throws CancelarOperacao {
        String nome, local, valor, quantidade;

        nome = inputUsuario.tratarInputString(String.format("Qual o nome do %s?", tipo.toLowerCase()));
        local = inputUsuario.escolhaDeLista(locais, nomeLocal);

        if (local == null) {
            local = inputUsuario.tratarInputString(String.format("Qual o %s?", nomeLocal));
        }

        valor = inputUsuario.tratarInputString("Qual o valor? ");

        while (true) {
            int quantidadeInt = inputUsuario.tratarInputInt("Qual a quantidade?");

            if (quantidadeInt < 1) {
                JOptionPane.showMessageDialog(null, "É necessário adicionar ao menos um!");
                continue;
            }

            quantidade = String.valueOf(quantidadeInt);
            
            break;
        }


        return new String[]{nome, local, valor, quantidade};
    }

    private String criarMensagem(Tarefa tarefa) {
        String nomesFuncionarios = "Funcionários: \n- " + String.join("\n- ", tarefa.getFuncionarios());
        String nomesRecursos = pegarNomes(new ArrayList<>(tarefa.getRecursos()));
        String nomesResiduos = pegarNomes(new ArrayList<>(tarefa.getResiduos()));
        tarefa.atualizarStatus();

        return String.format(
                "Nome: %s \nStatus: %s \nData de Entrega: %s \nPrazo: %d dias \n%s \nRecursos: %s \nResíduos: %s",
                tarefa.getNome(),
                tarefa.getStatus(),
                tarefa.getStringDataDeEntrega(),
                tarefa.calcularPrazo(),
                nomesFuncionarios,
                nomesRecursos,
                nomesResiduos
        );
    }

    private String pegarNomes(ArrayList<Material> materiais) {
        StringBuilder nomes = new StringBuilder();

        for (Material material: materiais) {
            nomes.append("\n- ").append(material.getNome());
        }

        return nomes.toString();
    }

    public void menuOpcoes() {
        String[] opcoes = {"Criar Tarefa", "Mostrar Tarefas", "Configurações", "Sair"};

        int escolha = JOptionPane.showOptionDialog(
                null,
                "O que deseja fazer? ",
                "ESCOLHA",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        switch (escolha) {

            case 0 -> {
                criarTarefa();
                main.menuOpcoes();
            }

            case 1 -> mostrarTarefas();

            case 2 -> {
                configurador.mudarConfiguracoes();
                main.menuOpcoes();
            }

            case 3 -> {
                JOptionPane.showMessageDialog(null, "Finalizando programa");
                System.exit(0);
            }
        }
    }


    private ArrayList<String> pegarFuncionarios() throws CancelarOperacao {
        ArrayList<String> funcionarios = new ArrayList<>();

        int continuar;
        do {
            funcionarios.add(inputUsuario.tratarInputString("Qual o nome do funcionário? "));
            continuar = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja adicionar mais um funcionário?",
                    "CONTINUAR?",
                    JOptionPane.YES_NO_OPTION
            );

        } while (continuar == JOptionPane.YES_OPTION);

        return funcionarios;
    }

    private ArrayList<Recurso> pegarRecursos() throws CancelarOperacao {
        ArrayList<Recurso> recursos = new ArrayList<>();
        ArrayList<String> nomesRecursos = new ArrayList<>();
        Recurso recursoAtual = null;

        for (var recurso: configurador.getRecursos()) {
            nomesRecursos.add(recurso.getNome());
        }

        int continuar;
        do {
            String nomeRecurso = inputUsuario.escolhaDeLista(nomesRecursos, "recurso");

            for (int i = 0; i < nomesRecursos.size(); i++) {
                if (nomesRecursos.contains(nomeRecurso)) {
                    var recursoEscolhido = configurador.getRecursos().get(i);
                    if (!recursos.contains(recursoEscolhido)) {
                        recursoAtual = recursoEscolhido;
                        break;
                    }

                    JOptionPane.showMessageDialog(null, "Recurso já foi adicionado!");
                    break;
                }
            }

            String fornecedor = null;
            long valor = -1;
            if (nomeRecurso.equals("outro")) {
                fornecedor = inputUsuario.escolhaDeLista(configurador.getFornecedores(), "fornecedor");

                if (fornecedor.equals("outro")) {
                    fornecedor = inputUsuario.tratarInputString("Qual o fornecedor? ");
                }

                valor = inputUsuario.tratarInputInt("Qual o valor? ");
            }

            int quantidade;
            while (true) {
                quantidade = inputUsuario.tratarInputInt("Qual a quantidade? ");

                if (quantidade < 1) {
                    JOptionPane.showMessageDialog(null, "Deve-se adicionar ao menos um");
                    continue;
                }

                break;
            }

            if (fornecedor != null && valor != -1) {
                recursoAtual = new Recurso(nomeRecurso, fornecedor, valor, quantidade);
            }

            recursos.add(recursoAtual);

            continuar = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja adicionar mais um recurso?",
                    "CONTINUAR?",
                    JOptionPane.YES_NO_OPTION
            );
        } while (continuar == JOptionPane.YES_OPTION);

        return recursos;
    }

    private ArrayList<Residuo> pegarResiduos() throws CancelarOperacao {
        ArrayList<Residuo> residuos = new ArrayList<>();
        ArrayList<String> nomesResiduos = new ArrayList<>();
        Residuo residuoAtual = null;

        for (var residuo: configurador.getResiduos()) {
            nomesResiduos.add(residuo.getNome());
        }

        int continuar;
        do {
            String nomeResiduo = inputUsuario.escolhaDeLista(nomesResiduos, "resíduo");

            for (int i = 0; i < nomesResiduos.size(); i++) {
                if (nomesResiduos.contains(nomeResiduo)) {
                    var residuoEscolhido = configurador.getResiduos().get(i);
                    if (!residuos.contains(residuoEscolhido)) {
                        residuoAtual = residuoEscolhido;
                        break;
                    }

                    JOptionPane.showMessageDialog(null, "Resíduo já foi adicionado!");
                    break;
                }
            }

            String localDescarte = null;
            long valor = -1;
            if (nomeResiduo.equals("outro")) {
                localDescarte = inputUsuario.escolhaDeLista(configurador.getLocaisDescarte(), "local de descarte");

                if (localDescarte.equals("outro")) {
                    localDescarte = inputUsuario.tratarInputString("Qual o local de descarte? ");
                }

                valor = inputUsuario.tratarInputInt("Qual o valor? ");
            }

            int quantidade;
            while (true) {
                quantidade = inputUsuario.tratarInputInt("Qual a quantidade? ");

                if (quantidade < 1) {
                    JOptionPane.showMessageDialog(null, "Deve-se adicionar ao menos um");
                    continue;
                }

                break;
            }

            if (localDescarte != null && valor != -1) {
                residuoAtual = new Residuo(nomeResiduo, localDescarte, valor, quantidade);
            }

            residuos.add(residuoAtual);

            continuar = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja adicionar mais um recurso?",
                    "CONTINUAR?",
                    JOptionPane.YES_NO_OPTION
            );
        } while (continuar == JOptionPane.YES_OPTION);

        return residuos;
    }

    private void criarTarefa() {
        try {
            String nomeTarefa = inputUsuario.tratarInputString("Qual o nome da tarefa? ");

            if (gerenciarTarefas.pegarTarefaPeloNome(nomeTarefa) != null) {
                JOptionPane.showMessageDialog(null, "Tarefa já existe.");
                return;
            }

            LocalDate data = inputUsuario.tratarInputData();
            ArrayList<String> funcionarios = pegarFuncionarios();
            ArrayList<Residuo> residuos = new ArrayList<>();
            ArrayList<Recurso> recursos = new ArrayList<>();

            int adicionar;

            adicionar = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja adicionar recursos?",
                    "ADICIONAR RECURSOS",
                    JOptionPane.YES_NO_OPTION
            );

            if (adicionar == JOptionPane.YES_OPTION) {
                recursos = pegarRecursos();
            }

            adicionar = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja adicionar resíduos?",
                    "ADICIONAR RECURSOS",
                    JOptionPane.YES_NO_OPTION
            );

            if (adicionar == JOptionPane.YES_OPTION) {
                residuos = pegarResiduos();
            }

            tarefaAtual = new Tarefa(nomeTarefa, funcionarios, recursos, residuos, data);
            tarefaAtual.salvarTarefa();

        } catch (CancelarOperacao e) {
            JOptionPane.showMessageDialog(null, "Operação cancelada!");
        }

    }
}