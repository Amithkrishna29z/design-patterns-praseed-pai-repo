package Creational.Factory;
public class Complex {
    double _r, _i;

    public static Complex createFromCartesian(double real, double imaginary) {
        return new Complex(real, imaginary);
    }

    public static Complex createFromPolar(double modulus, double angle) {
        return new Complex(modulus * Math.cos(angle), modulus * Math.sin(angle));
    }

    private Complex(double a, double b) {
        _r = a;
        _i = b;
    }

    public double Modulus() {
        System.out.println("Compute Modulus using the instance variables");
        return (double) 0xBEEF;
    }

    public double getR() {
        return _r;
    }

    public double getI() {
        return _i;
    }

    public static void main(String[] args) {
        Complex c = Complex.createFromCartesian(100, 100);
        Complex p = Complex.createFromPolar(10, Math.PI / 4);
        System.out.println(c.getR() + " + " + c.getI() + "i");
        System.out.println(p.getR() + " + " + p.getI() + "i");
    }
}