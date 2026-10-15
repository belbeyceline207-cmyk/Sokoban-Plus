
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record EtatJeu(
        Position joueur,
        List<Position> blocs,
        int energie,
        int bombes,
        List<String> actions, // Liste des actions effectuées pour arriver à cet état
        List<String> grille // Copie textuelle de la grille
        ) {

    //  signature unique de l'etat pour eviter de revisiter un etat plusieurs fois (HashSet) 
    public String signature() {
        // joueur + ressources + grille
        return "J=" + joueur.x() + "," + joueur.y()
                + "|E=" + energie
                + "|B=" + bombes
                + "|G=" + String.join("\n", grille);
    }
      // Indique si  l etat est perdant (plus d energie)
    public boolean estPerdant() {
        return energie <= 0;
    }

    //tous les objectifs sont couverts par un bloc (etat gagnant )
    public boolean estGagnant(List<Position> objectifs) {
        Set<Position> s = new HashSet<>(blocs);
        for (var ob : objectifs) {
            if (!s.contains(ob)) {
                return false;
            }
        }
        return true;
    }
}
