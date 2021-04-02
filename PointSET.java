/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class PointSET {
    private SET<Point2D> pointTree;

    // construct an empty set of points
    public PointSET() {
        pointTree = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointTree.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointTree.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        pointTree.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return pointTree.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D p : pointTree) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        Queue<Point2D> inRange = new Queue<>();
        for (Point2D p : pointTree) {
            if (p.x() >= rect.xmin() && p.x() <= rect.xmax()) {
                if (p.y() >= rect.ymin() && p.y() <= rect.ymax()) {
                    inRange.enqueue(p);
                }
            }
        }
        return inRange;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        Point2D near = pointTree.min();
        double nearDist = near.distanceTo(p);
        for (Point2D cmp : pointTree) {
            double cmpDist = cmp.distanceTo(p);
            if (cmpDist < nearDist) {
                near = cmp;
                nearDist = cmpDist;
            }
        }
        return near;
    }

    public static void main(String[] args) {
        PointSET test = new PointSET();
        String mT;
        if (test.isEmpty()) mT = "yes";
        else mT = "no";
        StdOut.println("~~~~~~~~ Empty ~~~~~~~~");
        StdOut.println("Is test empty? " + mT);
        StdOut.println("How many points are in test? " + test.size());
        StdOut.println("~~~~~~~~~~~~~~~~~~~~~~~\n");

        StdOut.println("~~~~~~~~ Add p0 and p1 ~~~~~~~~");
        Point2D p0 = new Point2D(0.3, 0.52);
        Point2D p1 = new Point2D(0.91, 0.34);
        test.insert(p0);
        String c0;
        String c1;
        if (test.contains(p0)) c0 = "yes";
        else c0 = "no";
        if (test.contains(p1)) c1 = "yes";
        else c1 = "no";
        StdOut.println("Does test contain p0? " + c0);
        StdOut.println("Does test contain p1? " + c1);
        test.insert(p1);
        if (test.contains(p1)) c1 = "yes";
        else c1 = "no";
        StdOut.println("NOW does test contain p1? " + c1);
        if (test.isEmpty()) mT = "yes";
        else mT = "no";
        StdOut.println("NOW is test empty? " + mT);
        StdOut.println("How many points are in test? " + test.size());
        StdOut.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        StdOut.println("~~~~~~~~ Rect Range ~~~~~~~~");
        Point2D p2 = new Point2D(0.66, 0.33);
        Point2D p3 = new Point2D(0.25, 0.55);
        Point2D p4 = new Point2D(0.5, 0.12);
        Point2D p5 = new Point2D(0.2, 0.33);
        Point2D p6 = new Point2D(0.33, 0.432);
        Point2D p7 = new Point2D(0.92, 0.26);
        Point2D p8 = new Point2D(0.32, 0.3);
        Point2D p9 = new Point2D(0.83, 0.8);
        Point2D p10 = new Point2D(0.2, 0.27);
        Point2D p11 = new Point2D(0.8, 0.1);
        test.insert(p2);
        test.insert(p3);
        test.insert(p4);
        test.insert(p5);
        test.insert(p6);
        test.insert(p7);
        test.insert(p8);
        test.insert(p9);
        test.insert(p10);
        test.insert(p11);
        String c5;
        String cx;
        if (test.contains(p5)) c5 = "yes";
        else c5 = "no";
        StdOut.println("Does test contain p5? " + c5);
        if (test.contains(new Point2D(0.23, 0.55))) cx = "yes";
        else cx = "no";
        StdOut.println("Does test contain px? " + cx);
        StdOut.println("How many points are in test? " + test.size());
        test.draw();

        RectHV rect = new RectHV(0.623, 0.1, 0.98, 0.5);
        rect.draw();
        Iterable<Point2D> inRange = test.range(rect);

        StdDraw.setPenColor(StdDraw.RED);
        for (Point2D p : inRange) {
            StdOut.println("Point at: " + p);
            p.draw();
        }
        StdOut.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        StdOut.println("~~~~~~~~ Nearest ~~~~~~~~");
        Point2D nearTo = new Point2D(0.4, 0.78);
        StdDraw.setPenColor(StdDraw.GREEN);
        nearTo.draw();
        Point2D nearest = test.nearest(nearTo);
        StdDraw.setPenColor(StdDraw.BLUE);
        nearest.draw();
        StdOut.println("Near To: " + nearTo);
        StdOut.println("Nearest: " + nearest);

    }
}
