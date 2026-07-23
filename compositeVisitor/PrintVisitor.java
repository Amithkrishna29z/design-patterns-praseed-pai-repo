package compositeVisitor;

import java.util.ArrayDeque;
import java.util.Deque;

public class PrintVisitor implements ExprVisitor {
    private Deque<String> _stack = new ArrayDeque<>();

    public String getResult() {
        return _stack.peek();
    }

    public void visit(NumericConstant node) {
        _stack.push(String.valueOf(node.getValue()));
    }

    public void visit(BinaryPlus node) {
        String right = _stack.pop();
        String left = _stack.pop();
        _stack.push("("+left+" + "+right+")");
    }

    public void visit(UnaryMinus node) {
        _stack.push("-"+_stack.pop());
    }
}
