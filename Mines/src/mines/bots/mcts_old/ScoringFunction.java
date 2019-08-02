package mines.bots.mcts_old;

/**
 *
 * @author Philipp
 */
public interface ScoringFunction {
    float score(float totalScore, float childTotal, float childScore);
}
