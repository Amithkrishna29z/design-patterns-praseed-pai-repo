package compositeVisitor;

public class UnaryMinus extends Expr {
    private Expr _right;

    public UnaryMinus(Expr right) {
        _right = right;
    }

    public Expr getRight() {
        return _right;
    }

    public void accept(ExprVisitor visitor) {
        _right.accept(visitor);
        visitor.visit(this);
    }
}
