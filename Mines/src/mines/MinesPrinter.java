package mines;

/**
 *
 * @author Philipp
 */
public class MinesPrinter {

    public String getVisibleStateString(long mines, long visible) {
        return stateString(mines, visible, 'X', ' ', ' ');
    }

    public String getFullStateString(long mines, long visible) {
        return stateString(mines, visible, 'X', 'x', ' ');
    }

    private String stateString(long mines, long visible, char visibleMine, char hiddenMine, char hiddenEmpty) {
        StringBuilder builder = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                builder.append('[');
                int square = Util.square(x, y);
                boolean mine = (mines & Util.toFlag(square)) != 0;
                boolean shown = (visible & Util.toFlag(square)) != 0;
                if (mine) {
                    builder.append(shown ? visibleMine : hiddenMine);
                } else {
                    long neighborhood = Util.neighborhood(square);
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
