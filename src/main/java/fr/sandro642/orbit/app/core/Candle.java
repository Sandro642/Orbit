package fr.sandro642.orbit.app.core;

public class Candle {
    public long t;
    public double o, h, l, c, sma, rsi;

    public Candle(long t, double o, double h, double l, double c) {
        this.t = t;
        this.o = o;
        this.h = h;
        this.l = l;
        this.c = c;
    }
}
