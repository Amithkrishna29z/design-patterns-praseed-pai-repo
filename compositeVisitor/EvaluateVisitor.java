package compositeVisitor;

import java.util.ArrayDeque;
import java.util.Deque;

public class EvaluateVisitor implements ExprVisitor {
    private Deque<Double> _stack = new ArrayDeque<>();

    public double getResult() {
        return _stack.peek();
    }

    public void visit(NumericConstant node) {
        _stack.push(node.getValue());
    }

    public void visit(BinaryPlus node) {
        double right = _stack.pop();
        double left = _stack.pop();
        _stack.push(left+right);
    }

    public void visit(UnaryMinus node) {
        _stack.push(-_stack.pop());
    }
}
