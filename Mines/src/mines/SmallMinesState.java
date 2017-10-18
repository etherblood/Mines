package mines;

/**
 *
 * @author Philipp
 */
public class SmallMinesState {

    private long mines;
    private long visible;

    public SmallMinesState() {
    }

    public SmallMinesState(long mines) {
        this.mines = mines;
    }

    public void discoverSquare(int square, boolean isMine) {
        long flag = Util.toFlag(square);
        assert (visible & flag) == 0;
        visible |= flag;
        boolean discoverFailed = ((mines & flag) != 0) != isMine;
        if(discoverFailed) {
            setGameOver();
        }
//        return success;
    }

    public void setGameOver() {
        mines = ~0;
    }
    
    public boolean isGameOver() {
        return playerWon() || playerLost();
    }
    
    public boolean playerWon() {
        return visible == ~0;
    }
    
    public boolean playerLost() {
        return mines == ~0;
    }

    public long getMines() {
        return mines;
    }

    public long getVisible() {
        return visible;
    }

    public void copyFrom(SmallMinesState state) {
        mines = state.mines;
        visible = state.visible;
    }

    public void setMines(long mines) {
        this.mines = mines;
    }

    public void setVisible(long visible) {
        this.visible = visible;
    }
}
