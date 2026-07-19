package structural.composite.ast;

public class MainTest {
    public static void main(String[] args) {
        Expr e = new UnaryMinus(new BinaryPlus(new NumbericConstant(10), new NumbericConstant(10)));
        double val = e.Evaluate();
        System.out.println("Value is " + val);
    }
}
