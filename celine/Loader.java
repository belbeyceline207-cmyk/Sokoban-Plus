
import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;

public class Loader {
      //  charge un objet de type Level depuis un fichier JSON
    public static Level load(String nomDuFichier) throws Exception {
        var json = Files.readString(Path.of(nomDuFichier)); // lire le fichier en chaine de caracteres
        return new Gson().fromJson(json, Level.class);
    }
}
