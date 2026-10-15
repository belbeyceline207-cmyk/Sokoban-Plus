
import java.util.Scanner;

public final class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("--- Sokoban+ (console) ---");
        System.out.println();
        afficherAide(); // afficher la liste des commandes
       // chargement du  niveau
        Level niveau = choisirNiveau(sc);
        Game jeu = nouveauJeu(niveau);

        boolean quitter = false;

        while (!quitter) {

            afficherEtatJeu(niveau, jeu);

            if (jeu.isWon()) {
                System.out.println("Bravo, niveau terminé !");
                break;
            }

            if (jeu.isLost()) {
                System.out.println("Vous avez perdu...");
                break;
            }

            System.out.print("> ");
            String commande = sc.nextLine().trim().toLowerCase();

            if (commande.isEmpty()) {
                continue;
            }

            switch (commande) {
                   //quitter ke jeu
                case "exit", "quit" ->
                    quitter = true;
                   // afficher l'aide
                case "help" ->
                    afficherAide();
                  // recommencer le niveau courant
                case "restart" -> {
                    jeu = nouveauJeu(niveau);
                    System.out.println("Niveau recommencé.");
                }
                  // changer de niveau
                case "level" -> {
                    niveau = choisirNiveau(sc);
                    jeu = nouveauJeu(niveau);
                }
                  // lancer le solveur automatique
                case "solve" -> {
                    Solver solver = new Solver();
                    SolveResult resultat = solver.solve(jeu);

                    System.out.println("États explorés : " + resultat.explored());

                    if (resultat.actions().isEmpty()) {
                        System.out.println("Aucune solution trouvée.");
                        continue;
                    }

                    System.out.println("Solution trouvée ("
                            + resultat.actions().size() + " coups) : "
                            + resultat.actions());

                    // rejeu automatique
                    for (String cmd : resultat.actions()) {
                        afficherEtatJeu(niveau, jeu);
                        boolean ok = appliquerCommande(jeu, cmd);
                        if (!ok) {
                            System.out.println("Erreur lors du rejeu.");
                            break;
                        }
                    }
                }
                 // sinon on essaye une commande normale
                default -> {
                    boolean ok = appliquerCommande(jeu, commande);
                    if (!ok) {
                        System.out.println("Commande invalide ou impossible.");
                    }
                }
            }
        }

        sc.close();
        System.out.println("Fin du jeu.");
    }

    // afficher les commandes disponibles
    private static void afficherAide() {
        System.out.println("Commandes disponibles :");
        System.out.println("  z q s d   -> déplacer le joueur");
        System.out.println("  b <dir>   -> utiliser une bombe (ex: b z)");
        System.out.println("  restart   -> recommencer le niveau");
        System.out.println("  level     -> changer de niveau");
        System.out.println("  solve     -> solveur automatique");
        System.out.println("  help      -> afficher l'aide");
        System.out.println("  exit      -> quitter");
        System.out.println();
    }

    // demande à l'utilisateur de choisir un niveau
    private static Level choisirNiveau(Scanner sc) {

        String[] fichiers = {
            "levels/niveau1.json",
            "levels/niveau2.json",
            "levels/niveau3.json"
        };

        while (true) {
            System.out.println();
            System.out.println("Choisir un niveau :");
            System.out.println("  1) Facile");
            System.out.println("  2) Difficile");
            System.out.println("  3) Sans solution");
            System.out.print("> ");

            String choix = sc.nextLine().trim();

            try {
                int indice = Integer.parseInt(choix) - 1;
                if (indice >= 0 && indice < fichiers.length) {
                    return Loader.load(fichiers[indice]); // charge le niveau JSON
                }
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement du niveau.");
            }

            System.out.println("Choix invalide.");
        }
    }

    // Initialisation du jeu  à partir d un niveau
    private static Game nouveauJeu(Level niveau) {
        return Game.creerJeuDepuisGrille(
                niveau.grille,
                niveau.energieInitiale
        );
    }

    /* Affichage du jeu */
    private static void afficherEtatJeu(Level niveau, Game jeu) {
        System.out.println();
        System.out.println("--- Niveau : " + niveau.nom + " ---");
        System.out.print(jeu.afficherGrille());
        System.out.println("Énergie : "
                + jeu.getPlayer().getEnergie()
                + " | Explosifs : "
                + jeu.getPlayer().getBombes());
    }

     // applique une commande clavier au jeu
    private static boolean appliquerCommande(Game jeu, String commande) {

        // déplacement simple
        if (commande.length() == 1) {
            Direction dir = Direction.depuisCommande(commande.charAt(0));
            return dir != null && jeu.deplacer(dir);
        }

        // bombe
        if (commande.length() == 3
                && commande.charAt(0) == 'b'
                && commande.charAt(1) == ' ') {

            Direction dir = Direction.depuisCommande(commande.charAt(2));
            return dir != null && jeu.exploser(dir);
        }

        return false;
    }
}
