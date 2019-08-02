package mines.bots.mcts_old;

public class UctScore implements ScoringFunction {
    private final static float EPSILON = 1e-6f;
    private final static float CONSTANT = (float) Math.sqrt(2);

    @Override
    public float score(float totalScore, float childTotal, float childScore) {
        childTotal += EPSILON;
        totalScore += 1;
        float exploitation = childScore / childTotal;
        float exploration = CONSTANT * (float) (Math.sqrt(Math.log(totalScore) / childTotal));
        float uctValue = exploitation + exploration;
        return uctValue;
    }

}
