package com.eksler.vadim.pulsinggame.models;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vadim§eksler on 27/03/2018.
 */

public class Circle {
    private RectF rectF;
    private int offset;
    private int center;
    private List<Arc> arcs;
    private int radius = 0;
    private Random rnd = new Random();

    public Circle(int offset, int center) {
        this.offset = offset;
        this.center = center;
        generateCircle();
        rectF = new RectF(center, center, center, center);
    }

    public void update() {
        radius += offset;
        rectF = new RectF(rectF.left - offset,
                rectF.top - offset,
                rectF.right + offset,
                rectF.bottom + offset);
    }

    public RectF getRectF() {
        return rectF;
    }

    public void reset() {
        radius = 0;
        generateCircle();
        rectF = new RectF(center, center, center, center);
    }

    private void generateCircle() {
        int spaces = rnd.nextInt(3) + 1;
        arcs = generateArcs(spaces, 30);
    }

    private List<Arc> generateArcs(int spaces, int len) {
        arcs = new ArrayList<>();
        int sector = 360 / spaces;
        for (int i = 0; i < 360; i += sector) {
            int spaceStart = i + rnd.nextInt(sector - len);
            Arc arc1 = new Arc(i, spaceStart);
            Arc arc2 = new Arc(spaceStart + len, i + sector);
            arcs.add(arc1);
            arcs.add(arc2);
        }
        return arcs;
    }


    public List<Arc> getArcs() {
        return arcs;
    }

    public int getRadius() {
        return radius;
    }
}
