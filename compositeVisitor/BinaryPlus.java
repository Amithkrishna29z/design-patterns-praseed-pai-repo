package compositeVisitor;

public class BinaryPlus extends Expr {
    private Expr _left;
    private Expr _right;

    public BinaryPlus(Expr left, Expr right) {
        _left = left;
        _right = right;
    }

    public Expr getLeft() {return _left;}
    public Expr getRight() {return _right;}

    public void accept(ExprVisitor visitor) {
        _left.accept(visitor);
        _right.accept(visitor);
        visitor.visit(this);
    }
}
