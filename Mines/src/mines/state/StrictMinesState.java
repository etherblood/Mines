package mines.state;

import mines.Util;

/**
 *
 * @author Philipp
 */
public class StrictMinesState extends AbstractStrictMinesStateReadable<MinesState> implements MinesState {

    public StrictMinesState(MinesState state) {
        super(state);
    }

    @Override
    public void reveal(int square) {
        if ((state.getRevealed() & Util.toFlag(square)) == 0) {
            throw new IllegalArgumentException("tried to reveal an already revealed square");
        }
        state.reveal(square);
    }

}
