package mines;

import java.util.List;
import java.util.Random;

/**
 *
 * @author Philipp
 */
public class Util {

    private static final long[] NEIGHBORHOOD = new long[64];

    static {
        for (int from = 0; from < 64; from++) {
            for (int to = 0; to < 64; to++) {
                int x = x(from) - x(to);
                int y = y(from) - y(to);
                if (x * x + y * y <= 2) {
                    NEIGHBORHOOD[from] |= toFlag(to);
                }
            }
        }
    }

    public static int randomBit(Random rng, long bitFlags) {
        return nthBit(bitFlags, rng.nextInt(Long.bitCount(bitFlags)));
    }

    public static int nthBit(long bitFlags, int index) {
        for (int i = 0; i < index; i++) {
            bitFlags &= bitFlags - 1;
        }
        return Long.numberOfTrailingZeros(bitFlags);
    }

    public static long selectRandomBits(Random rng, long min, long max, int selectCount) {
        assert (min & max) == min;
        assert Long.bitCount(min) <= selectCount && selectCount <= Long.bitCount(max);
        int minCount = Long.bitCount(min);
        while (minCount != selectCount) {
            long bits = min | (rng.nextLong() & max);
            minCount = Long.bitCount(bits);
            if (minCount > selectCount) {
                max = bits;
            } else {
                min = bits;
            }
        }
        return min;
    }

    public static long selectConstrainedRandomBits(Random rng, long min, long max, List<Constraint> constraints) {
        assert (min & max) == min;
        int solvedConstraints = 0;
        int iterations = 0;
        long bits = min | (rng.nextLong() & max);
        while (solvedConstraints < constraints.size()) {
            Constraint constraint = constraints.get(iterations % constraints.size());
            if (!constraint.isValid(bits)) {
                long lower = min & constraint.getMask();
                long upper = max & constraint.getMask();
                long masked = Util.selectRandomBits(rng, lower, upper, constraint.getCount());
                bits = masked | (bits & ~constraint.getMask());
                assert constraint.isValid(bits);
                solvedConstraints = 1;
            } else {
                solvedConstraints++;
            }
            iterations++;
        }
        return bits;
    }

    public static long toFlag(int square) {
        return 1L << square;
    }

    public static long neighborhood(int square) {
        return NEIGHBORHOOD[square];
    }

    public static int x(int square) {
        return square & 7;
    }

    public static int y(int square) {
        return square >>> 3;
    }

    public static int square(int x, int y) {
        return x | (y << 3);
    }
}
