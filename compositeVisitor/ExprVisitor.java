package compositeVisitor;

interface ExprVisitor {
    void visit(NumericConstant node);
    void visit(BinaryPlus node);
    void visit(UnaryMinus node); 
}
