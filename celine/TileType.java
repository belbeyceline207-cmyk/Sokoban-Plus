
public enum TileType {

    MUR('#'), // Mur indestructible
    BLOC_FIXE_DESTRUCTIBLE('X'), // Bloc fixe destructible
    SOL_VIDE(' '), // Sol vide
    JOUEUR('@'), // Position initiale du joueur (pas une vraie tuile)
    OBJECTIF('.'), // Objectif (overlay recommandé)
    BLOC_LEGER('o'), // Bloc léger
    BLOC_GLISSANT('O'), // Bloc glissant
    BLOC_ENERGIVORE('*'), // Bloc énergivore
    ENERGIE('+'), // +5 énergie
    EXPLOSIVE('E'); // Explosif ramassable

    private final char caractere;

    TileType(char caractere) {
        this.caractere = caractere;
    }

    // getter 
    public char getCaractere() {
        return caractere;
    }

    //  Conversion caractere -> TileType 
    public static TileType convertirCaractere(char c) {
        return switch (c) {
            case '#' ->
                MUR;
            case 'X' ->
                BLOC_FIXE_DESTRUCTIBLE;
            case ' ' ->
                SOL_VIDE;
            case '@' ->
                JOUEUR;
            case '.' ->
                OBJECTIF;
            case 'o' ->
                BLOC_LEGER;
            case 'O' ->
                BLOC_GLISSANT;
            case '*' ->
                BLOC_ENERGIVORE;
            case '+' ->
                ENERGIE;
            case 'E' ->
                EXPLOSIVE;
            default ->
                SOL_VIDE;
        };
    }

    //  Règles de jeu //
    //  bloc poussable ?
    public boolean isBlock() {
        return this == BLOC_LEGER || this == BLOC_GLISSANT || this == BLOC_ENERGIVORE;
    }
    //mur indestructible ?

    public boolean isWall() {
        return this == MUR;
    }
    //mur destructible (X) ?

    public boolean isDestructible() {
        return this == BLOC_FIXE_DESTRUCTIBLE;
    }
    //le joueur peut entrer dans la case sans pousser ?

    public boolean isWalkable() {
        return switch (this) {
            case SOL_VIDE, ENERGIE, EXPLOSIVE ->
                true;
            default ->
                false;
        };
    }

    //un bloc peut être deplace sur cette case (l'objectif est considere comme vide car on le gere apres avec une liste)
    public boolean isFreeForBlock() {
        return this == SOL_VIDE;
    }
}
