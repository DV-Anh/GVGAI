package ontology;

/**
 * Created by Daniel on 16.03.2017.
 */
public class Types {
    public static enum ACTIONS {
        ACTION_NIL,
        ACTION_UP,
        ACTION_LEFT,
        ACTION_DOWN,
        ACTION_RIGHT,
        ACTION_USE,
        ACTION_ESCAPE;
    }

    public static enum WINNER {
        PLAYER_DISQ(-100),
        NO_WINNER(-1),
        PLAYER_LOSES(0),
        PLAYER_WINS(1);

        private int key;
        WINNER(int val) {key=val;}
        public int key() {return key;}
    }
}
