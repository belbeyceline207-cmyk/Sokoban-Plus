
import java.util.ArrayList;
import java.util.List;

public class Game {

    // couts energie (simple)
    private static final int MOVE_COST = 1;
    private static final int ENERGY_PUSH_COST = 2;
    private static final int ENERGY_PUSH_COST_HEAVY = 3;

    private final Grid grid; // grille du niveau (murs, blocs, bonus)
    private final Player player;// joueur du jeu
    private final List<int[]> objectifs;// positions des objectifs (.)

    private boolean gagner;// vrai si le joueur a gagné
    private boolean perdu;// vrai si le joueur a perdu

    Game(Grid grid, Player player, List<int[]> objectifs) // constructeur 
     {
        this.grid = grid;
        this.player = player;
        this.objectifs = objectifs;
        mettreAJourEtatJeu();
    }

    // Creation du jeu d'apres un tableau de caracteres (niveau)
    public static Game creerJeuDepuisGrille(String[] level, int energieInitiale) {
        List<String> lignes = List.of(level);
        Grid grille = Grid.creerDepuisLignes(lignes);

        // joueur hors grille (grille sans @ ni .)
        int px = -1;
        int py = -1;

        List<int[]> buts = new ArrayList<>();
        // on cherche le joueur et enregistre les positions des objectifs
        for (int y = 0; y < lignes.size(); y++) {
            String ligne = lignes.get(y);
            for (int x = 0; x < ligne.length(); x++) {
                char c = ligne.charAt(x);
                if (c == TileType.JOUEUR.getCaractere()) {
                    px = x;
                    py = y;
                }
                if (c == TileType.OBJECTIF.getCaractere()) {
                    buts.add(new int[]{x, y});
                }
            }
        }
        Player player = new Player(px, py, energieInitiale); // creer le joueur avce son energie
        return new Game(grille, player, buts);
    }

    // les getters : Retourne le joueur
    public Player getPlayer() {
        return player;
    }

    public boolean isWon()  // Retourne vrai si le niveau est gagné
    {
        return gagner;
    }

    public boolean isLost() // Retourne vrai si le joueur a perdu
    {
        return perdu;
    }

    // Affichage console
    // affiche le joueur en overlay , les blocs et murs, et les objectifs 
    public String afficherGrille() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (x == player.getX() && y == player.getY()) {
                    sb.append('@');
                } else {
                    TileType t = grid.get(x, y);
                    if (t != TileType.SOL_VIDE) {
                        sb.append(t.getCaractere());
                    } else {
                        if (estObjectif(x, y)) {
                            sb.append('.');
                        } else {
                            sb.append(' ');
                        }
                    }
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // Deplacement
    // gere le deplacement du joueur et les interactions avec la case cible
    public boolean deplacer(Direction dir) {
        // calculer la nouvelle position
        int nx = player.getX() + dir.getDx();
        int ny = player.getY() + dir.getDy();

        TileType destination = grid.get(nx, ny);

        if (gagner || perdu) {
            return false;
        }
        if (!grid.estDansLaGrille(nx, ny)) {
            return false;
        }
        // mur ou bloc fixe
        if (destination.isWall() || destination.isDestructible()) {
            return false;
        }
        //si c'est un bloc on le pousse
        if (destination.isBlock()) {
            return essayerPousser(dir);
        }
        // sol classique
        if (destination.isWalkable()) {
            if (!player.energieCheck(MOVE_COST)) {
                mettreAJourEtatJeu();
                return false;
            }
            player.consommerEnergie(MOVE_COST); // consommer l'energie
            player.deplacerVers(nx, ny);// deplacer le joueur
            appliquerEffetCase(nx, ny, destination);// appliquer le bonus
            mettreAJourEtatJeu();
            return true;
        }

        return false;
    }

    //Bombe : on verifie s'il reste des bombes, si mur destructible on le detruit
    public boolean exploser(Direction dir) {
        int x = player.getX() + dir.getDx();
        int y = player.getY() + dir.getDy();

        if (gagner || perdu) {
            return false;
        }
        if (!player.peutExploser()) {
            return false;
        }
        if (!grid.estDansLaGrille(x, y)) {
            return false;
        }
        if (grid.get(x, y) == TileType.BLOC_FIXE_DESTRUCTIBLE) {
            grid.set(x, y, TileType.SOL_VIDE);
            player.consomeExplosif();
            mettreAJourEtatJeu();
            return true;
        }
        return false;
    }

    // Effets des cases
    // energie +5 / explosif +1 bombre
    private void appliquerEffetCase(int x, int y, TileType t) {
        if (t == TileType.ENERGIE) {
            player.ajouterEnergie(5);
            grid.set(x, y, TileType.SOL_VIDE);
        }
        if (t == TileType.EXPLOSIVE) {
            player.ajouterExplosif(1);
            grid.set(x, y, TileType.SOL_VIDE);
        }
    }

    //Pousser
    private boolean essayerPousser(Direction dir) {
        int bx = player.getX() + dir.getDx();
        int by = player.getY() + dir.getDy();

        int ax = bx + dir.getDx();
        int ay = by + dir.getDy();

        if (!grid.estDansLaGrille(ax, ay)) {
            return false;
        }

        TileType apres = grid.get(ax, ay);
        TileType bloc = grid.get(bx, by);

        if (!apres.isFreeForBlock()) {
            return false;
        }

        int cout;
        // Calculer l'energie à consommer bloc lourd = plus cher
        if (bloc == TileType.BLOC_ENERGIVORE) {
            cout = ENERGY_PUSH_COST_HEAVY;
        } else {
            cout = ENERGY_PUSH_COST;
        }

        if (!player.energieCheck(cout)) {
            mettreAJourEtatJeu();
            return false;
        }

        player.consommerEnergie(cout);
        grid.set(ax, ay, bloc);
        grid.set(bx, by, TileType.SOL_VIDE);
        player.deplacerVers(bx, by); // deplacer le joueur et le bloc 
        mettreAJourEtatJeu();

        return true;
    }

    //// Fin de jeu
    // Met à jour victoire et défaite
    private void mettreAJourEtatJeu() {
        perdu = estPerdant();
        gagner = estGagnant();
    }

    // on verifie si tous les objectifs sont recouverts par un bloc ou non
    private boolean estGagnant() {
        for (int[] ob : objectifs) {
            if (!grid.get(ob[0], ob[1]).isBlock()) {
                return false;
            }
        }
        return true;
    }

   //  Retourne vrai si lénergie est à zéro
    private boolean estPerdant() {
        return player.getEnergie() <= 0;
    }

    // on verifie si une position correspond a un objectif
    private boolean estObjectif(int x, int y) {
        for (int[] ob : objectifs) {
            if (ob[0] == x && ob[1] == y) {
                return true;
            }
        }
        return false;
    }

    // liste des objectifs 
    public List<Position> listeObjectifs() {
        List<Position> res = new ArrayList<>();
        for (int[] ob : objectifs) {
            res.add(new Position(ob[0], ob[1]));
        }
        return res;
    }

    // etape 2
    // EtatJeu (solveur)
    // cree un etat du jeu pour le solveur 
    // une photo du jeu a un instant (une copie)
    public EtatJeu versEtatJeu(List<String> actions) {
        List<String> capture = new ArrayList<>();
        for (int y = 0; y < grid.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < grid.getWidth(); x++) {
                sb.append(grid.get(x, y).getCaractere());
            }
            capture.add(sb.toString());
        }
        List<Position> blocs = new ArrayList<>();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (grid.get(x, y).isBlock()) {
                    blocs.add(new Position(x, y));
                }
            }
        }
        return new EtatJeu(
                new Position(player.getX(), player.getY()),
                List.copyOf(blocs),
                player.getEnergie(),
                player.getBombes(),
                List.copyOf(actions),
                List.copyOf(capture)
        );
    }

    // recree un jeu a partir d'un etat
    public static Game jeuDepuisEtatJeu(EtatJeu s, List<Position> objectifsPos) {
        Grid grille = Grid.creerDepuisLignes(s.grille());
        List<int[]> buts = new ArrayList<>();
        for (Position p : objectifsPos) {
            buts.add(new int[]{p.x(), p.y()});
        }
        Player player = new Player(s.joueur().x(), s.joueur().y(), s.energie());
        for (int i = 0; i < s.bombes(); i++) {
            player.ajouterExplosif(1);
        }
        return new Game(grille, player, buts);
    }
}
