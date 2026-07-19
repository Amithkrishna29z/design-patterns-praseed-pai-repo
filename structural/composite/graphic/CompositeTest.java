package structural.composite.graphic;

import java.util.ArrayList;
import java.util.List;

interface Graphic {
    public void print();
}

class CompositeGraphic implements Graphic {
    private List<Graphic> mChildGraphics = new ArrayList<Graphic>();

    public void print() {
        for (Graphic graphic : mChildGraphics) {
            graphic.print();
        }
    }

    public void add(Graphic graphic) {
        mChildGraphics.add(graphic);
    }

    public void remove(Graphic graphic) {
        mChildGraphics.remove(graphic);
    }
}

class Ellipse implements Graphic {
    public void print() {
        System.out.println("Ellipse");
    }
}

class Rectangle implements Graphic {
    public void print() {
        System.out.println("Rectangle");
    }
}

class Arc extends Ellipse {
    public void print() {
        System.out.println("Arc");
    }
}

public class CompositeTest {
    public static void main(String[] args) {
        Ellipse ellipse1 = new Ellipse();
        Ellipse ellipse2 = new Ellipse();
        Graphic arc = new Arc();
        Graphic rect = new Rectangle();

        CompositeGraphic graphic = new CompositeGraphic();
        CompositeGraphic graphic1 = new CompositeGraphic();
        CompositeGraphic graphic2 = new CompositeGraphic();

        graphic1.add(ellipse1);
        graphic1.add(ellipse2);
        graphic1.add(arc);

        graphic2.add(rect);

        graphic.add(graphic1);
        graphic.add(graphic2);

        graphic.print();
    }

}
