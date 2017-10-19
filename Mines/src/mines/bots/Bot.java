package mines.bots;

import mines.state.MinesStateReadable;

/**
 *
 * @author Philipp
 */
public interface Bot {
    int findMove(MinesStateReadable state);
}
