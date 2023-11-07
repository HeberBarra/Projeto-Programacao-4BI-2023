import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeitorJson {
     public JSONObject lerArquivo(String caminhoArquivo) {
          File file = new File(caminhoArquivo);

          String conteudo;
          try (FileReader fileReader = new FileReader(file)) {
               conteudo = new String(Files.readAllBytes(file.toPath()));
          } catch (IOException e) {
               Logger logger = Logger.getLogger(LeitorJson.class.getName());
               logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
               return new JSONObject();
          }

         return new JSONObject(conteudo);
     }
}

