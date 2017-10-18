package mines.bots.mcts;

/**
 *
 * @author Philipp
 */
public interface ScoringFunction {
    float score(float totalScore, float childTotal, float childScore);
}
