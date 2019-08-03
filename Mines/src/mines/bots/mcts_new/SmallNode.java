package mines.bots.mcts_new;

import java.util.Iterator;

public class SmallNode {

    private final int move;
    private float score, visits;
    private SmallNode sibling;
    private SmallNode child;

    public SmallNode(int move) {
        this.move = move;
    }

    public void applyScore(float score) {
        assert 0 <= score && score <= 1;
        this.score += score;
        visits++;
    }

    public int getMove() {
        return move;
    }

    public float getScore() {
        return score;
    }

    public float getVisits() {
        return visits;
    }

    public SmallNode getNextSibling() {
        return sibling;
    }

    public SmallNode getFirstChild() {
        return child;
    }

    public SmallNode getChild(int move) {
        SmallNode next = child;
        while (next != null) {
            if (next.move == move) {
                return next;
            }
            next = next.sibling;
        }
        return null;
    }

    public void appendChild(SmallNode node) {
        if (child == null) {
            child = node;
            return;
        }
        child.appendSibling(node);
    }

    private void appendSibling(SmallNode node) {
        SmallNode next = this;
        while (next.sibling != null) {
            next = next.sibling;
        }
        next.sibling = node;
    }

    public Iterator<SmallNode> childIterator() {
        return iterator(child);
    }

    private static Iterator<SmallNode> iterator(SmallNode node) {
        return new Iterator<SmallNode>() {
            private SmallNode next = node;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public SmallNode next() {
                try {
                    return next;
                } finally {
                    next = next.getNextSibling();
                }
            }
        };
    }
}
