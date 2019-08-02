package mines.bots.mcts_old;


public class VisitScore implements ScoringFunction {

    @Override
    public float score(float totalScore, float childTotal, float childScore) {
        return childTotal;
    }

}
