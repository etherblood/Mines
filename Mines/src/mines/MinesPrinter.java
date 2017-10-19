package mines;

import java.lang.reflect.Field;
import mines.state.FastMinesState;

/**
 *
 * @author Philipp
 */
public class MinesPrinter {

    public String getVisibleStateString(FastMinesState state) {
        return stateString(state, 'X', ' ', ' ');
    }

    public String getFullStateString(FastMinesState state) {
        return stateString(state, 'X', 'x', ' ');
    }

    private String stateString(FastMinesState state, char visibleMine, char hiddenMine, char hiddenEmpty) {
        try {
            Field field = FastMinesState.class.getDeclaredField("mines");
            field.setAccessible(true);
            long mines = field.getLong(state);
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
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
}
