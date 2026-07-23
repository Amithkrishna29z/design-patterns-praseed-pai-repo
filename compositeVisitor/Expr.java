package compositeVisitor;

abstract class Expr {
    public abstract void accept(ExprVisitor visitor);
}
