package com.aware.plugin.getData;


import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;


/**
 * Created by
 * R. FRIGERIO and T. LUCAS (Ensimag, 2A)
 *
 * This class will perform PCA on three features.
 * It is use to "straighten" a phone : we register data produced by,
 * for instance, an accelerometer. We do not want the results to depend
 * on the orientation of the phone (indeed this is in many situations a random phenomenon,
 * and this would therefore produce unreductible variance and be a major hindrance to machine learning
 * methods such as logistic regression).
 *
 * These computations will use open-source librairies.
 * The formulas involved are simple and could be easily implemented
 * however these librairies are well optimized and it is important
 * for us to be as fast as possible
 *
 * How does the PCA work?
 * Covariance is a scalar product : one can build an base made of othogonal vectors (they then have
 * no covariance between one another). In addition the vectors of the new base are order by decreasing norm or
 * their respective eigenValues.
 */


public class PCA {

    public Array2DRowRealMatrix original_matrix;

    /*
     Will instanciate the class given 3 arrays containing the
     X,Y,Z data
     */
    public PCA(double[] x, double[] y, double[] z){
        if ((x.length != y.length) || (y.length !=z.length)){
            throw new IllegalArgumentException("Les vecteurs dans changeFrame ne font pas la m�me longeur");
        }
        int size = x.length;
        double[][] matrix = new double[3][size];

        //Make a matrix with the three vectors
        matrix[0] = x; matrix[1] = y; matrix[2] = z;
        //instanciate an Array2DRowRealMatrix with the previous matrix
        this.original_matrix = new Array2DRowRealMatrix(matrix);
        Log.i("Context","original matrix : Column Dimension : " + this.original_matrix.getColumnDimension() + " and row " + this.original_matrix.getRowDimension());
        Log.i("Context","Returning from instanciation of PCA");
    }
    public double[][] determine_PCA(){
        //Log.i("Context","Starting to perform PCA");

        //Compute the covariance matrix
        this.original_matrix =(Array2DRowRealMatrix) this.original_matrix.transpose();
        Covariance cov = new Covariance(this.original_matrix);
        RealMatrix cov_mat = cov.getCovarianceMatrix();

        //Get the eigenvectors
        EigenDecomposition eigen = new EigenDecomposition(cov_mat);
        ArrayRealVector eig0 = (ArrayRealVector) eigen.getEigenvector(0);
        ArrayRealVector eig1 = (ArrayRealVector) eigen.getEigenvector(1);
        ArrayRealVector eig2 = (ArrayRealVector) eigen.getEigenvector(2);

        double eigVal0 = eigen.getRealEigenvalue(0), eigVal1 = eigen.getRealEigenvalue(1),
                eigVal2 = eigen.getRealEigenvalue(2);

        //Order them by decreasing norm of their eigenvalues.
        if(Math.abs(eigVal0) < Math.abs(eigVal1)){
            ArrayRealVector temp = eig0;
            double valTemp = eigVal0;
            eigVal0 = eigVal1; eigVal1 = valTemp;
            eig0 = eig1; eig1 = temp;
        }
        if(Math.abs(eigVal1) < Math.abs(eigVal2)){
            ArrayRealVector temp = eig1;
            double valTemp = eigVal1;
            eigVal1 = eigVal2; eigVal2 = valTemp;
            eig1 = eig2; eig2 = temp;
        }
        if(Math.abs(eigVal0) < Math.abs(eigVal1)){
            ArrayRealVector temp = eig0;
            eig0 = eig1; eig1 = temp;
        }
        double[] arr_eig0 = eig0.toArray(), arr_eig1 = eig1.toArray(),
                 arr_eig2 = eig2.toArray();

        //we want to return a double[][]
        double[][] new_compo = new double[3][3];
            for(int j = 0; j<3; j++){
                new_compo[0][j] = arr_eig0[j];
                new_compo[1][j] = arr_eig1[j];
                new_compo[2][j] = arr_eig2[j];
            }
        return new_compo;
    }

    public double[][] changeBase(double[][] mat_change, double[] x, double[] y, double[] z){
        int SIZE = x.length;
        double[][] result = new double[3][SIZE];
        if ((x.length != y.length) || (y.length != z.length)){
          Log.i("Context","Wrong lengths");
        }
        for (int k = 0; k< SIZE; k++){
            result[0][k] = mat_change[0][0] * x[k] + mat_change[0][1] * y[k] + mat_change[0][2];
            result[1][k] = mat_change[1][0] * x[k] + mat_change[1][1] * y[k] + mat_change[1][2];
            result[2][k] = mat_change[2][0] * x[k] + mat_change[2][1] * y[k] + mat_change[2][2];
        }
        return result;
    }

    public double[] changeBase(double[][] mat_change, double[] x, double[] y, double[] z, int ind){
        int SIZE = x.length;
        double[] result = new double[SIZE];
        if ((x.length != y.length) || (y.length != z.length)){
            Log.i("Context","Wrong lengths");
        }
        for (int k = 0; k< SIZE; k++){
            result[k] = mat_change[ind][0] * x[k] + mat_change[ind][1] * y[k] + mat_change[ind][2];
        }
        return result;
    }


}
