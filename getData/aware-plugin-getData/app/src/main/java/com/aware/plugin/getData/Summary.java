package com.aware.plugin.getData;

import org.apache.commons.math3.stat.StatUtils;

//import static org.apache.commons.math3.stat.StatUtils.geometricMean;
//import static org.apache.commons.math3.stat.StatUtils.populationVariance;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;


/**
 * Created by Romain FRIGERIO and Thomas LUCAS
 * this class will be used to compute summaries of an array
 */
public class Summary {
    private double[] data;

    //Statistical moments/quantiles
    private double mean;
    private double std;
    private double max;
    private double min;
    private double skewness;
    private double kurtosis;
    private int longeur;

    //Mesures of the variability of the array.
    //private double variability;

    /*public double getVariability() {
        return variability;
    }
    */
    public int getLongeur() {
        return longeur;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public double getSkewness() {
        return skewness;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStd() {
        return std;
    }

    public double getMean() {
        return mean;
    }

    public double[] getData() {
        return data;
    }

    public Summary(double[] data) {
        this.data = data;
        /*
        this.mean = StatUtils.geometricMean(this.data);
        this.std = StatUtils.populationVariance(this.data, this.mean);
        */
        Mean mean = new Mean();
        this.mean = mean.evaluate(this.data);
        StandardDeviation std = new StandardDeviation();
        this.std = std.evaluate(this.data);
        this.max = StatUtils.max(this.data);
        this.min = StatUtils.min(this.data);
        Skewness skew = new Skewness();
        this.skewness = skew.evaluate(this.data);
        Kurtosis kurt = new Kurtosis();
        this.kurtosis = kurt.evaluate(this.data);
        this.longeur = 6;
    }

    public double[] getSummary() {
        double[] result = new double[this.getLongeur()];
        result[0] = this.getMax();
        result[1] = this.getMin();
        result[2] = this.getMean();
        result[3] = this.getStd();
        result[4] = this.getSkewness();
        result[5] = this.getKurtosis();
        return result;
    }

    public String getSummaryString() {
        String result = "";
        result += this.getMax() + ",";
        result += this.getMin() + ",";
        result += this.getMean() + ",";
        result += this.getStd() + ",";
        result += this.getSkewness() + ",";
        result += this.getKurtosis() + ",";
        return result;
    }
}
