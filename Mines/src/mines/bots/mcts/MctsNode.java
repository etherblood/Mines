package mines.bots.mcts;

/**
 *
 * @author Philipp
 */
public class MctsNode {

    private MctsNode[] childs;
    private float score;
    private float visits;

    public boolean isInitialized() {
        return childs != null;
    }

    public void initChilds(int size) {
        assert childs == null;
        childs = new MctsNode[size];
    }

    public MctsNode getChild(int x) {
        if (childs[x] == null) {
            childs[x] = new MctsNode();
        }
        return childs[x];
    }

    public void setChild(int x, MctsNode child) {
        childs[x] = child;
    }

    public float visitScore() {
        return visits;
    }

    public void increaseVisits(float value) {
        visits += value;
    }

    public float score() {
        return score;
    }

    public float winrate(int player) {
        return score / visitScore();
    }

    public void increaseScores(float result) {
        score += result;
    }
}
