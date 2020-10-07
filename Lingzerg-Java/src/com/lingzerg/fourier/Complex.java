package com.lingzerg.fourier;

import java.text.NumberFormat;

public class Complex {
    private double real;//ʵ��
    private double image;//����
 
    NumberFormat nf = NumberFormat.getInstance();
    
    
    public Complex() {
        real = 0;
        image = 0;
        
        // ���ô˸�ʽ�в�ʹ�÷���
        nf.setGroupingUsed(false);
        // ��������С����������������λ����
        nf.setMaximumFractionDigits(4);
    }
 
    public Complex(double real) {
        this.real = real;
        this.image = 0;
    }
 
    public Complex(double real, double image) {
        this.real = real;
        this.image = image;
    }
 
    //�ӣ�(a+bi)+(c+di)=(a+c)+(b+d)i
    public Complex add(Complex complex) {
        double real = complex.getReal();
        double image = complex.getImage();
        double newReal = this.real + real;
        double newImage = this.image + image;
        return new Complex(newReal, newImage);
    }
 
    //����(a+bi)-(c+di)=(a-c)+(b-d)i
    public Complex sub(Complex complex) {
        double real = complex.getReal();
        double image = complex.getImage();
        double newReal = this.real - real;
        double newImage = this.image - image;
        return new Complex(newReal, newImage);
    }
 
    //�ˣ�(a+bi)(c+di)=(ac-bd)+(bc+ad)i
    public Complex mul(Complex complex) {
        double real = complex.getReal();
        double image = complex.getImage();
        double newReal = this.real * real - this.image * image;
        double newImage = this.image * real + this.real * image;
        return new Complex(newReal, newImage);
    }
 
    //�ˣ�a(c+di)=ac+adi
    public Complex mul(double multiplier) {
        return mul(new Complex(multiplier));
    }
 
    //����(a+bi)/(c+di)=(ac+bd)/(c^2+d^2) +((bc-ad)/(c^2+d^2))i
    public Complex div(Complex complex) {
        double real = complex.getReal();
        double image = complex.getImage();
        double denominator = real * real + image * image;
        double newReal = (this.real * real + this.image * image) / denominator;
        double newImage = (this.image * real - this.real * image) / denominator;
        return new Complex(newReal, newImage);
    }
 
    public double getReal() {
        return real;
    }
 
    public void setReal(double real) {
        this.real = real;
    }
 
    public double getImage() {
        return image;
    }
 
    public void setImage(double image) {
        this.image = image;
    }
 
    @Override
    public String toString() {
    	
    	
        String str = "";
        if (real != 0) {
            str += nf.format(real);
        } else {
            str += "0";
        }
        if (image < 0) {
            str += " *** " + nf.format(image) + "i";
        } else if (image > 0) {
            str += " *** " + nf.format(image) + "i";
        }
        return str;
    }
 
    //ŷ����ʽ e^(ix)=cosx+isinx
    public static Complex euler(double x) {
        double newReal = Math.cos(x);
        double newImage = Math.sin(x);
        return new Complex(newReal, newImage);
    }
 
 
 
    /**
     * ��double����ת��Ϊ��������
     *
     * @param doubleArray double����
     */
    public static Complex[] getComplexArray(double[] doubleArray) {
        int length = doubleArray.length;
        Complex[] complexArray = new Complex[length];
        for (int i = 0; i < length; i++) {
            complexArray[i] = new Complex(doubleArray[i]);
        }
        return complexArray;
    }
 
    /**
     * ����άdouble����ת��Ϊ��ά��������
     *
     * @param doubleArray double��ά����
     */
    public static Complex[][] getComplexArray(double[][] doubleArray) {
        int length = doubleArray.length;
        Complex[][] complexArray = new Complex[length][];
        for (int i = 0; i < length; i++) {
            complexArray[i] = getComplexArray(doubleArray[i]);
        }
        return complexArray;
    }
 
    /**
     * ��ά��������ת��
     *
     * @param a ��ά��������
     */
    public static Complex[][] transform(Complex[][] a) {
        int row = a.length;
        int column = a[0].length;
        Complex[][] result = new Complex[column][row];
        for (int i = 0; i < column; i++)
            for (int j = 0; j < row; j++)
                result[i][j] = a[j][i];
        return result;
    }
}