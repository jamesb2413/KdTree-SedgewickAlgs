/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private Node root;
    private int n;

    // construct an empty set of points
    public KdTree() {
        root = new Node();
        n = 0;
    }

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node lb;  // left/bottom subtree
        private Node rt;  // right/top subtree
    }

    // is the set empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // number of points in the set
    public int size() {
        return n;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        // first node
        if (root.p == null) {
            root.p = p;
            root.rect = new RectHV(0.0, 0.0, 1.0, 1.0);
            n++;
            return;
        }
        // others
        insert(root, p, true);
    }

    private void insert(Node subRoot, Point2D p, boolean v) {
        Point2D rootP = subRoot.p;
        RectHV rootR = subRoot.rect;
        if (rootP.equals(p)) return;
        // left sub-tree
        if ((v && p.x() < rootP.x()) || (!v && p.y() < rootP.y())) {
            // add if child is null
            if (subRoot.lb == null) {
                Node xChild = new Node();
                xChild.p = p;
                if (v) xChild.rect = new RectHV(rootR.xmin(), rootR.ymin(),
                                                rootP.x(), rootR.ymax());
                else xChild.rect = new RectHV(rootR.xmin(), rootR.ymin(),
                                              rootR.xmax(), rootP.y());
                subRoot.lb = xChild;
                n++;
            }
            // else recurse
            else insert(subRoot.lb, p, !v);
        }
        // right sub-tree
        else {
            if (subRoot.rt == null) {
                Node yChild = new Node();
                yChild.p = p;
                if (v) yChild.rect = new RectHV(rootP.x(), rootR.ymin(),
                                                rootR.xmax(), rootR.ymax());
                else yChild.rect = new RectHV(rootR.xmin(), rootP.y(),
                                              rootR.xmax(), rootR.ymax());
                subRoot.rt = yChild;
                n++;
            }
            else insert(subRoot.rt, p, !v);
        }
    }


    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return contains(root, p, true);
    }

    private boolean contains(Node subRoot, Point2D p, boolean v) {
        Point2D rootP = subRoot.p;
        if (rootP.equals(p)) return true;
        // left sub-tree
        if ((v && p.x() < rootP.x()) || (!v && p.y() < rootP.y())) {
            // false if child is null
            if (subRoot.lb == null) {
                return false;
            }
            // else recurse
            else return contains(subRoot.lb, p, !v);
        }
        // right sub-tree
        else {
            if (subRoot.rt == null) return false;
            else return contains(subRoot.rt, p, !v);
        }
    }


    // draw all points to standard draw
    public void draw() {
        draw(root, true);
    }

    private void draw(Node subRoot, boolean v) {
        if (subRoot == null) return;
        RectHV rect = subRoot.rect;

        if (v) {
            double x = subRoot.p.x();
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x, rect.ymin(), x, rect.ymax());
        }
        else {
            double y = subRoot.p.y();
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rect.xmin(), y, rect.xmax(), y);
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        subRoot.p.draw();
        StdDraw.setPenRadius();

        if (subRoot.lb != null) draw(subRoot.lb, !v);
        if (subRoot.rt != null) draw(subRoot.rt, !v);
    }


    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        Queue<Point2D> inRange = new Queue<>();
        range(root, rect, inRange, true);
        return inRange;
    }

    private void range(Node sub, RectHV rect, Queue<Point2D> q, boolean v) {
        // base case
        if (sub == null) return;

        Point2D rootP = sub.p;
        Node lb = sub.lb;
        Node rt = sub.rt;
        double x = rootP.x();
        double y = rootP.y();

        // working left and right
        if (v) {
            // both sides could have points in range
            if (rect.xmin() <= x && x <= rect.xmax()) {
                // rootP is in range
                if (rect.ymin() <= y && y <= rect.ymax()) q.enqueue(rootP);
                range(lb, rect, q, false);
                range(rt, rect, q, false);
            }
            // left side could have points in range
            else if (rect.xmin() <= x) {
                range(lb, rect, q, false);
            }
            // right side could have points in range
            else range(rt, rect, q, false);
        }
        // working up and down
        else {
            // both sides could have points in range
            if (rect.ymin() <= y && y <= rect.ymax()) {
                // rootP is in range
                if (rect.xmin() <= x && x <= rect.xmax()) q.enqueue(rootP);
                range(lb, rect, q, true);
                range(rt, rect, q, true);
            }
            // bottom could have points in range
            else if (rect.ymin() <= y) range(lb, rect, q, true);
                // top could have points in range
            else range(rt, rect, q, true);
        }
    }


    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (n == 0) return null;
        return nearest(root, p, 10.0);
    }

    private Point2D nearest(Node subRoot, Point2D p, double soFar) {
        if (subRoot == null) return null;
        // distance from point in current node to p
        Point2D thisP = subRoot.p;
        double pointD = thisP.distanceTo(p);
        if (pointD < soFar) soFar = pointD;

        RectHV lbRect = subRoot.lb.rect;
        RectHV rtRect = subRoot.rt.rect;
        double lbRDist = lbRect.distanceTo(p);
        double rtRDist = rtRect.distanceTo(p);

        // left subtree may have nearest point
        if (lbRDist < soFar) {
            Point2D lbNearest = nearest(subRoot.lb, p, soFar);
            double lbNDist = lbNearest.distanceTo(p);
            // both subtrees may have nearer point
            if (rtRDist < soFar) {
                // nearest must be in left subtree
                if (lbNDist < rtRDist) {
                    return lbNearest;
                }
                // both subtrees may have nearer point
                else {
                    Point2D rtNearest = nearest(subRoot.rt, p, soFar);
                    double rtNDist = rtNearest.distanceTo(p);
                    if (lbNDist <= rtNDist && lbNDist < soFar) return lbNearest;
                    else if (rtNDist < soFar) return rtNearest;
                    else return thisP;
                }
            }
            else return lbNearest;
        }
        // only right subtree may have nearest point
        if (rtRDist < soFar) {
            Point2D rtNearest = nearest(subRoot.rt, p, soFar);
            double rtNDist = rtNearest.distanceTo(p);
            if (rtNDist < soFar) return rtNearest;
            else return thisP;
        }
        else return thisP;

    }

    public static void main(String[] args) {
        KdTree test = new KdTree();
        String mT;
        if (test.isEmpty()) mT = "yes";
        else mT = "no";
        StdOut.println("~~~~~~~~ Empty ~~~~~~~~");
        StdOut.println("Is test empty? " + mT);
        StdOut.println("How many points are in test? " + test.size());
        StdOut.println("~~~~~~~~~~~~~~~~~~~~~~~\n");

        StdOut.println("~~~~~~~~ Add a few ~~~~~~~~");
        Point2D p0 = new Point2D(0.3, 0.52);
        Point2D p1 = new Point2D(0.91, 0.34);
        Point2D p12 = new Point2D(0.8, 0.34);
        Point2D p13 = new Point2D(0.21, 0.58);
        test.insert(p13);
        String c0;
        String c1;
        if (test.contains(p0)) c0 = "yes";
        else c0 = "no";
        if (test.contains(p1)) c1 = "yes";
        else c1 = "no";
        StdOut.println("Does test contain p0? " + c0);
        StdOut.println("Does test contain p1? " + c1);
        test.insert(p12);
        test.insert(p1);
        test.insert(p0);
        if (test.contains(p1)) c1 = "yes";
        else c1 = "no";
        StdOut.println("NOW does test contain p1? " + c1);
        if (test.isEmpty()) mT = "yes";
        else mT = "no";
        StdOut.println("NOW is test empty? " + mT);
        StdOut.println("How many points are in test? " + test.size());
        test.draw();
        StdOut.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        StdOut.println("~~~~~~~~ Rect Range ~~~~~~~~");
        Point2D p2 = new Point2D(0.26, 0.33);
        Point2D p3 = new Point2D(0.25, 0.55);
        Point2D p4 = new Point2D(0.5, 0.12);
        Point2D p5 = new Point2D(0.6, 0.33);
        Point2D p6 = new Point2D(0.13, 0.432);
        Point2D p7 = new Point2D(0.92, 0.26);
        Point2D p8 = new Point2D(0.2, 0.9);
        Point2D p9 = new Point2D(0.83, 0.8);
        Point2D p10 = new Point2D(0.7, 0.27);
        Point2D p11 = new Point2D(0.8, 0.4);
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

        RectHV rect = new RectHV(0.15, 0.3, 0.46, 0.6);
        StdDraw.setPenColor(StdDraw.GREEN);
        rect.draw();
        Iterable<Point2D> inRange = test.range(rect);

        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.01);
        for (Point2D p : inRange) {
            StdOut.println("Point at: " + p);
            p.draw();
        }
        StdOut.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

/*
        StdOut.println("~~~~~~~~ Nearest ~~~~~~~~");
        Point2D nearTo = new Point2D(0.4, 0.78);
        StdDraw.setPenColor(StdDraw.GREEN);
        nearTo.draw();
        Point2D nearest = test.nearest(nearTo);
        StdDraw.setPenColor(StdDraw.BLUE);
        nearest.draw();
        StdOut.println("Near To: " + nearTo);
        StdOut.println("Nearest: " + nearest);
        */
    }
}
