package structural.composite.ast;

public class NumbericConstant extends Expr {
    private double _value;

    public NumbericConstant(double value) {
        _value = value;
    }

    public double Evaluate() {
        return _value;
    }  
}
