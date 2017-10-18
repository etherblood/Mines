package mines.bots;

import mines.SmallMinesState;

/**
 *
 * @author Philipp
 */
public interface Bot {
    Move findMove(SmallMinesState state);
}
