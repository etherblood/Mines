package mines.bots;

import mines.Util;
import mines.state.MinesState;

/**
 *
 * @author Philipp
 */
public class SecureMover {

    public void applySecureMoves(MinesState state) {
        if(state.isOver()) {
            return;
        }

        boolean repeat;
        long flags = 0;
        do {
            repeat = false;
            long iterator = state.getRevealed();
            while (iterator != 0) {
                int square = Long.numberOfTrailingZeros(iterator);

                long neighbors = Util.neighbors(square);
                long neighborFlags = flags & neighbors;
                int neighborFlagCount = Long.bitCount(neighborFlags);
                long unknownNeighbors = neighbors & ~state.getRevealed() & ~neighborFlags;
                int neighborMineCount = state.countNeighborMines(square) - neighborFlagCount;
                if (neighborMineCount == 0 && unknownNeighbors != 0) {
                    makeMoves(state, unknownNeighbors);
                    iterator |= unknownNeighbors;
                    repeat = true;
                }
                int unknownNeighborCount = Long.bitCount(unknownNeighbors);
                if (neighborMineCount == unknownNeighborCount && (flags | unknownNeighbors) != flags) {
                    flags |= unknownNeighbors;
                    repeat = true;
                }

                iterator ^= Util.toFlag(square);
            }
        } while (repeat);
    }

    private void makeMoves(MinesState state, long moves) {
        while (moves != 0) {
            int move = Long.numberOfTrailingZeros(moves);
            state.reveal(move);
            moves ^= Util.toFlag(move);
        }
    }
}
