package InputUsuario;

import javax.swing.JOptionPane;
import java.util.Arrays;

public class InputUsuario {

    private final String[] valoresProibidos = {"", " ", OpcoesInput.CANCELAR.getValor(), OpcoesInput.REPETIR.getValor()};
    public String tratarInputString(String mensagem) {
        while (true) {
            String input = JOptionPane.showInputDialog(mensagem);

            if (input == null) {
                int escolha = JOptionPane.showConfirmDialog(null, "Deseja cancelar?", "Cancelar?", JOptionPane.YES_NO_OPTION);

                // Talvez mude, principal função desse enum é evitar erro de digitação
                if (escolha == JOptionPane.YES_OPTION) return OpcoesInput.CANCELAR.getValor();

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
}
