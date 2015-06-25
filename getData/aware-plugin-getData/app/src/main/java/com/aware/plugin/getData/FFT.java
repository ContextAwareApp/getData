package com.aware.plugin.getData;

import android.util.Log;

/**
 * Created by R. FRIGERIO and L. THOMAS
 * This code was taken from https://www.ee.columbia.edu/~ronw/code/MEAPsoft/doc/html/FFT_8java-source.html
 * with only minor modifications on the actual code,suppression of the rest and addition of some comments.
 * It has a a GPLv2 license attached to it, however it is derived from a very classical algorithm
 * (Cooley and Tukey's method for FFT) and could be quickly reimplemented if this license is too stringent.
 *
 * The role of this class is simply performing the fast fourier transform in an optimized way.
 * A fourier transform allows us to decompose a temporal signal into a sum of periodic signals with
 * different frequencies.
 * In the case of activity recognition we expect that X/Y/Z acceleration will display periodic components,
 * wich will correspond to greater amplitude at corresponding frequencies in the FFT and this is why we are
 * interested in performing the FFT.
 *
 * Rather than computing the FFT using the formula, this algorithm uses "divide and conquer" to simplify the problem.
 * It does not use any recursive calls however (this is rarely efficient as regards memory usage).
 */

public class FFT {

    int n, m;

    // Lookup tables. Only need to recompute when size of FFT changes.
    double[] cos;
    double[] sin;

    public FFT(int n) {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        // Make sure n is a power of 2
        if (n != (1 << m)) {
            Log.i("Context", "Wrong length");
            throw new RuntimeException("FFT length must be power of 2 " + n);
        }
       // Log.i("FFT","Precomputing");
        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }

    }

    public void fft(double[] x, double[] y) {
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;

        if ((x.length != n)&&(y.length != n)){
            Log.i("Context", "x and y not of same size");
            throw new RuntimeException("The vectors containing data and receiving the result are not of the right dimension");
        }

        // Bit-reverse
        j = 0;
        n2 = n / 2;

        for (i = 1; i < n - 1; i++) {

            n1 = n2;
            while (j >= n1) {

                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;


            if (i < j) {

                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }
        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2) {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }
}