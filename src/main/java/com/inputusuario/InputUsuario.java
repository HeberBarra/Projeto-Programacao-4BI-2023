package com.inputusuario;

import com.tarefa.Status;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class InputUsuario {

    private final String[] valoresProibidos = {"", " ", OpcoesInput.CANCELAR.getValor(), OpcoesInput.REPETIR.getValor()};

    public String tratarInputString(String mensagem) throws CancelarOperacao {
        while (true) {
            String input = JOptionPane.showInputDialog(mensagem);

            if (input == null) {
                int escolha = JOptionPane.showConfirmDialog(null, "Deseja cancelar?", "Cancelar?", JOptionPane.YES_NO_OPTION);

                // Talvez mude, principal função desse enum é evitar erro de digitação
                if (escolha == JOptionPane.YES_OPTION) throw new CancelarOperacao(OpcoesInput.CANCELAR.getValor());

                continue;
            }

            input = input.strip();

            if (Arrays.stream(valoresProibidos).toList().contains(input)) {
                JOptionPane.showMessageDialog(null, "Valor inválido! Por favor tente novamente...", "Valor inválido", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            int estaCorreto = JOptionPane.showConfirmDialog(null, String.format("%s está correto?", input), "Está correto", JOptionPane.YES_NO_OPTION);

            if (estaCorreto != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Por favor tente novamente...");
                continue;
            }

            return input;
        }
    }

    public int tratarInputInt(String mensagem) throws CancelarOperacao {
        String input = tratarInputString(mensagem);
        int inputInt;
        while (true) {
            try {
                inputInt = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Digite um valor válido!");
            }
        }

        return inputInt;
    }

    public String escolhaDeLista(String[] listaOpcoes, String nome) throws CancelarOperacao {
        String resposta;

        while (true) {

            resposta = (String) JOptionPane.showInputDialog(
                    null,
                    String.format("Escolha um %s por favor: ", nome),
                    "ESCOLHA",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    listaOpcoes,
                    listaOpcoes[0]
            );

            if (resposta == null) {
                int cancelar = JOptionPane.showConfirmDialog(
                        null,
                        "Deseja cancelar a operação?",
                        "CANCELAR?",
                        JOptionPane.YES_NO_OPTION
                );

                if (cancelar != JOptionPane.YES_OPTION) continue;

                throw new CancelarOperacao(OpcoesInput.CANCELAR.getValor());
            }

            break;
        }
        return resposta;
    }

    public Status escolhaDeLista(Status[] listaOpcoes, String nome) throws CancelarOperacao {
        Status resposta;

        while (true) {

            resposta = (Status) JOptionPane.showInputDialog(
                    null,
                    String.format("Escolha um %s por favor: ", nome),
                    "ESCOLHA",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    listaOpcoes,
                    listaOpcoes[0]
            );

            if (resposta == null) {
                int cancelar = JOptionPane.showConfirmDialog(
                        null,
                        "Deseja cancelar a operação?",
                        "CANCELAR?",
                        JOptionPane.YES_NO_OPTION
                );

                if (cancelar != JOptionPane.YES_OPTION) continue;

                throw new CancelarOperacao(OpcoesInput.CANCELAR.getValor());
            }

            break;
        }

        return resposta;
    }

    public LocalDate tratarInputData() throws CancelarOperacao {
        int dia, mes, ano;

        while (true)
        {
            JOptionPane.showMessageDialog(null,"Digite a data de entrega\n DIA/MES/ANO ");
            dia = this.tratarInputInt("Dia");
            mes = this.tratarInputInt("Mês");
            ano = this.tratarInputInt("Ano");

            if( (dia >= 1 && dia <= 30) && (mes == 4 || mes == 6 ||mes == 9 || mes == 11 ) && (ano > 2000))
            {
                break;
            }

            else if ((dia >= 1 && dia <= 31) && (mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes== 12) && (ano > 2000))
            {
                break;
            }

            else if( (dia >= 1 && dia <= 29) && (mes == 2 ) && (ano % 4 == 0))
            {
                break;
            }

            else  if( (dia >= 1 && dia <= 28) && (mes == 2 ) && ano > 2000)
            {
                break;
            }

            else
            {
                JOptionPane.showMessageDialog(null,"DATA INVÁLIDA", "ERRO" , JOptionPane.ERROR_MESSAGE);
            }
        }

        return LocalDate.of(ano, mes, dia);
    }

    public String escolhaDeLista(ArrayList<String> listaOpcoes, String nome) throws CancelarOperacao {
        String[] opcoes = new String[listaOpcoes.size() + 1];
        opcoes[0] = "Outro";

        for (int i = 0; i < listaOpcoes.size(); i++) {
            opcoes[i + 1] = listaOpcoes.get(i);
        }

        return escolhaDeLista(opcoes, nome);
    }
}
