package compositeVisitor;

public class CompositeVisitorTest {
    public static void main(String[] args) {
       Expr e = new UnaryMinus(new BinaryPlus(new NumericConstant(10), new NumericConstant(20)));

       PrintVisitor printer = new PrintVisitor();
       e.accept(printer);
       System.out.println(printer.getResult());

       EvaluateVisitor evaluator = new EvaluateVisitor();
       e.accept(evaluator);
       System.out.println("Value is "+evaluator.getResult());
    }
}
