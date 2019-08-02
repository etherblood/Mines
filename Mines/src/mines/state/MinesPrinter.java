package mines.state;

import mines.Util;

/**
 *
 * @author Philipp
 */
public class MinesPrinter {

    public String constraintStateString(MineConstraints constraints) {
        //TODO: render mines after gameover
        long revealed = constraints.getRevealed();
        StringBuilder builder = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                builder.append('[');
                int square = Util.square(x, y);
                long flag = Util.toFlag(square);
                if ((flag & revealed) != 0) {
                    builder.append(constraints.countNeighborMines(square));
                } else {
                    builder.append(' ');
                }
                builder.append(']');
            }
            if (y != 0) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    public String getVisibleStateString(MinesState state) {
        long revealed = state.getRevealed();
        StringBuilder builder = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                builder.append('[');
                int square = Util.square(x, y);
                long flag = Util.toFlag(square);
                if ((flag & revealed) != 0) {
                    builder.append(state.countNeighborMines(square));
                } else {
                    builder.append(' ');
                }
                builder.append(']');
            }
            if (y != 0) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    public String getVisibleStateString(FastMinesState state) {
        return stateString(state, 'X', ' ', ' ');
    }

    public String getFullStateString(FastMinesState state) {
        return stateString(state, 'X', '*', ' ');
    }

    private String stateString(FastMinesState state, char visibleMine, char hiddenMine, char hiddenEmpty) {
        long mines = state.mines;
        StringBuilder builder = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                builder.append('[');
                int square = Util.square(x, y);
                boolean mine = (mines & Util.toFlag(square)) != 0;
                boolean shown = (state.getRevealed() & Util.toFlag(square)) != 0;
                if (mine) {
                    builder.append(shown ? visibleMine : hiddenMine);
                } else {
                    long neighborhood = Util.neighbors(square);
                    builder.append(shown ? Integer.toString(Long.bitCount(neighborhood & mines)) : hiddenEmpty);
                }
                builder.append(']');
            }
            if (y != 0) {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
