
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
  // Solveur automatique base sur une recherche en largeur 
public final class Solver {

    // commandes possibles (deplacements et bombes)
    private static final String[] COMMANDES = {"z", "q", "s", "d"};
    private static final String[] BOMBES = {"b z", "b q", "b s", "b d"};

    public SolveResult solve(Game start) {

        // on récupère les objectifs du jeu
        List<Position> objectifs = start.listeObjectifs();

        // état de départ (ssans actions)
        EtatJeu initial = start.versEtatJeu(List.of());
        Queue<EtatJeu> file = new LinkedList<>(); // file pour la BFS
        Set<String> dejaVu = new HashSet<>();  // ensemble des signatures déjà visitées

        file.add(initial);
        dejaVu.add(initial.signature());

        int explored = 0;
         // boucle principale BFS
        while (!file.isEmpty()) {
            // on prend le premier état
            EtatJeu courant = file.poll();
            explored++;

            // victoire ?
            if (courant.estGagnant(objectifs)) {
                return new SolveResult(courant.actions(), explored);
            }
            // état perdu : ignore
            if (courant.estPerdant()) {
                continue;
            }
            // tester toutes les actions possibles
            List<String> actions = actionsPossibles(courant);
            for (String cmd : actions) {
                EtatJeu suivant = appliquer(courant, objectifs, cmd);
                if (suivant == null) {
                    continue;
                }
                String sig = suivant.signature();
                if (!dejaVu.contains(sig)) {
                    dejaVu.add(sig);
                    file.add(suivant);
                }
            }
        }
        // aucune solution trouvée
        return new SolveResult(List.of(), explored);
    }

    //Actions possibles dans un etat 
    private List<String> actionsPossibles(EtatJeu s) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < COMMANDES.length; i++) {
            res.add(COMMANDES[i]);
        }
        for (int i = 0; i < BOMBES.length; i++) {
            res.add(BOMBES[i]);
        }
        return res;
    }

    // applique une commande a un etat et retourne le nouvel etat
    private EtatJeu appliquer(EtatJeu courant, List<Position> buts, String cmd) {
        // reconstruire le jeu à partir de l etat
        Game jeu = Game.jeuDepuisEtatJeu(courant, buts);
        boolean succes = false;  // pour indiquer si l action a fonctionne ou non

        // deplacement simple
        if (cmd.length() == 1) {
            char c = cmd.charAt(0);
            Direction dir = Direction.depuisCommande(c);
            if (dir != null) {
                succes = jeu.deplacer(dir);
            }
        } else { // utiliser bombe "b x"
            char c = cmd.charAt(2);
            Direction dir = Direction.depuisCommande(c);

            if (dir != null) {
                succes = jeu.exploser(dir);
            }
        }
        if (!succes) {
            return null;
        }
        // reccopier les actions deja faites
        List<String> nouvellesActions = new ArrayList<>();
        for (String a : courant.actions()) {
            nouvellesActions.add(a);
        }
        nouvellesActions.add(cmd);

        // retourner le nouvel etat du jeu 
        return jeu.versEtatJeu(nouvellesActions);
    }
}
