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
    
    public static long constrainedRandomBits(Random rng, List<Constraint> constraints) {
        return constrainedRandomBits(rng, 0, ~0, constraints);
    }

    public static long constrainedRandomBits(Random rng, long lowerBound, long upperBound, List<Constraint> constraints) {
        assert (lowerBound & upperBound) == lowerBound;
        long bits = lowerBound | (rng.nextLong() & upperBound);
            rngCalls++;
        for (int solvedConstraints = 0; solvedConstraints < constraints.size(); solvedConstraints++) {
            Constraint constraint = constraints.get(solvedConstraints);
            if (!constraint.isValid(bits)) {
                solvedConstraints = 0;
                long mask = constraint.getSquares();
                long maskedBits = randomBits(rng, lowerBound & mask, upperBound & mask, constraint.getMineCount());
                bits = maskedBits | (bits & ~mask);
                assert constraint.isValid(bits);
            }
        }
        return bits;
    }
    
    public static long randomBits(Random rng, int targetCount) {
        return randomBits(rng, 0, ~0, targetCount);
    }

    public static long rngCalls = 0;
    public static long randomBits(Random rng, long lowerBound, long upperBound, int targetCount) {
        assert (lowerBound & upperBound) == lowerBound;
        assert Long.bitCount(lowerBound) <= targetCount && targetCount <= Long.bitCount(upperBound);
        int bitsCount = Long.bitCount(lowerBound);
        while (bitsCount != targetCount) {
            long bits = lowerBound | (rng.nextLong() & upperBound);
            rngCalls++;
            bitsCount = Long.bitCount(bits);
            if (bitsCount > targetCount) {
                upperBound = bits;
            } else {
                lowerBound = bits;
            }
        }
        return lowerBound;
    }

    public static long toFlag(int square) {
        return 1L << square;
    }

    public static long neighbors(int square) {
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
