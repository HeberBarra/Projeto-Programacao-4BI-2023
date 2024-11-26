package com.main;

import com.inputusuario.CancelarOperacao;
import com.inputusuario.InputUsuario;
import com.tarefa.*;
import javax.swing.*;
import java.util.ArrayList;

public class Main {
    // TODO: criar classe de controle principal

    private final GerenciarTarefas gerenciarTarefas = new GerenciarTarefas();
    private final Configurador configurador = new Configurador();
    private final InputUsuario inputUsuario = new InputUsuario();
    private static Main main;
    private Tarefa tarefaAtual;

    public static void main(String[] args) {
        main = new Main();
        main.configurador.lerConfiguracoes();
        main.mostrarTarefas();
    }

    public void mostrarTarefas() {
        final int FRAME_WIDTH = 1200;
        final int FRAME_HEIGHT = 700;

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

            int linhaSelecionada = table.getSelectedRow();
            String nomeTarefa = (String) table.getValueAt(linhaSelecionada, 0);

            try {
                tarefaAtual = gerenciarTarefas.pegarTarefaPeloNome(nomeTarefa);
                frameTarefas.dispose();
                gerenciarTarefa();
            } catch (TarefaNaoEncontrada ex) {
                throw new RuntimeException(ex);
            }
        });

        frameTarefas.setVisible(true);
    }

    // TODO: método para gerenciar uma tarefa, mostra as informações necessárias
    //  (nome, status, funcionários, recursos e resíduos com as informações deles, prazo e data de entrega)
    //  permitir modificação das informações usando eventos(essa parte se quiser posso fazer separado)
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
                                "ADICIOMAR?",
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
                            int novaQuantidade = Integer.parseInt(inputUsuario.tratarInputString("Qual a nova quantidade? "));

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
                quantidade = Integer.parseInt(inputUsuario.tratarInputString("Qual a quantidade?"));

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
            quantidade = inputUsuario.tratarInputString("Qual a quantidade?");

            if (Integer.parseInt(quantidade) < 1) {
                JOptionPane.showMessageDialog(null, "É necessário adicionar ao menos um!");
                continue;
            }
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
                "Nome: %s \nStatus: %s \nData de Entrega: %s \nPrazo: %d dias \nFuncionários: %s \nRecursos: %s \nResíduos: %s",
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

    // TODO: mostra um menu de opções, que contará com opções numa caixa de diálogo para ver
    //  tarefas, configuraçõese sair do programa
    //  0 - ver tarefas
    //  1 - configurações
    //  2 - sair do programa
    public int menuOpcoes() {
        // return só pra não dá erro, mude
        return 0;
    }
}