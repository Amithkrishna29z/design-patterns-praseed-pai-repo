package compositeVisitor;

public class NumericConstant extends Expr {
    private double _value;

    public NumericConstant(double value) {
        _value = value;
    }

    public double getValue() {
        return _value;
    }

    public void accept(ExprVisitor visitor) {
        visitor.visit(this);
    }  
}
