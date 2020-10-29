package com.lingzerg.fourier;

import java.util.Arrays;

public class Fourier {
	public Fourier() {
		
	}
	
	/**
	 * ��ɢ����Ҷ�任
	 * @param array ��������
	 * @param minus ����ֵ��DFT=-1��IDFT=1
	 */
	public static Complex[] DFT(Complex[] array, int minus) {
		

		Complex[] list = array;
		
		
	    int length = list.length;
	    Complex[] complexArray = new Complex[length];
	    // minus * 2 * PI / N
	    double flag = minus * 2 * Math.PI / length;
	    for (int i = 0; i < length; i++) {
	        Complex sum = new Complex();
	        for (int j = 0; j < length; j++) {
	            //array[x] * e^((minus * 2 * PI * k / N)i)
	            Complex complex = Complex.euler(flag * i * j).mul(list[j]);
	            sum = complex.add(sum);
	        }
	        //�ۼ�
	        complexArray[i] = sum;
	    }
	    return complexArray;
	}
	
	/***
	 * ���ٸ���Ҷ�任,�ݹ��㷨, FFT�������̬
	 * @param list
	 * @param minus
	 * @return
	 */
	public static Complex[] FFTRecursion(Complex[] array,int minus) {
		
		Complex[] list = array;
		
		
		//lim�������ǳ���2��, ����lim=1, ����������ֻ��2��Ԫ��, �Ͳ�������
		if(list.length <= 1) {
			return new Complex[]{list[0]};
			 //return list;
		}
		int lim = list.length/2;
		
		Complex[] a0 = new Complex[lim];
		Complex[] a1 = new Complex[lim];
		
		for (int i = 0; i < lim; i++) {
			 a0[i] = list[i*2]; // 0 2 4 6
			 a1[i] = list[i*2+1]; //1 3 5 7
		}
		
		//�ݹ鵽���ڵ�
		a0 = FFTRecursion(a0,minus);
		a1 = FFTRecursion(a1,minus);
		
		double p = minus * 2  * Math.PI  / list.length;
		
		Complex wn = new Complex(Math.cos(p), Math.sin(p));
		Complex w = new Complex(1,0);
		
		Complex[] ak = new Complex[list.length];
		
		
		for (int k = 0; k < lim; k++) {
			
			//Complex w = new Complex(Math.cos(p), Math.sin(p));
			
			Complex wA1 =  w.mul(a1[k]);
			
			ak[k] = a0[k].add(wA1); // a0 + w*a1
			Complex cc = a0[k].sub(wA1);
			
			ak[k+lim] = cc; // a0 - w*a1
			
			w = w.mul(wn);
		}
		
		return ak;
	}

	static int lim = 1;
	
	/***
	 * ���ٸ���Ҷ�任���õ�������ļ����㷨, Cooley-Tukey�㷨
	 * @param list
	 * @param minus
	 * @return
	 */
	public static Complex[] FFTButterfly(Complex[] array,int minus) {
		
		Complex[] list = array;
		int[] rev = BitReverse(list.length);
		
		for (int i = 0; i < lim; i++) {
			if(i<rev[i]) {
				Complex temp = list[i];
				list[i] = list[rev[i]];
				list[rev[i]] = temp;
			}
		}
		
		for (int i = 1; i <= log2(lim); i++) {
			int m = 1 << i;
			double p = minus * 2  * Math.PI  / m;
			Complex wn = new Complex(Math.cos(p), Math.sin(p));
			for (int j = 0; j < lim; j+=m) {
				Complex w = new Complex(1);
				for (int k = 0; k < m/2; k++) {
					Complex t = w.mul(list[k+j+m/2]);
					Complex u = list[k+j];
					list[j+k] = u.add(t);
					list[j+k+m/2] = u.sub(t);
					w = w.mul(wn);
				}
			} 
		}
		return list;
	}
	
	/***
	 * ���ٸ���Ҷ�任 Stockham �㷨,����һ��ӳ��, ���ʺ�GPU
	 * @param list
	 * @param minus
	 * @return
	 */
	public static Complex[] FFTStockham (Complex[] array,int minus) {
		Complex[] list = array;
		int len = list.length;
		
		Complex[] templist = new Complex[8];
		
		for (int i = 1; i <= log2(len); i++) {
			int m = 1 << i;
			double p = minus * 2  * Math.PI  / m;
			Complex wn = new Complex(Math.cos(p), Math.sin(p)); //��λ��
			
			int btflyNo = 0;
			for (int j = 0; j < len; j+=m) {
				Complex w = new Complex(1); 
				
				for (int k = 0; k < m/2; k++) {
					
					int pow = i-1;
					
					int no0 = (int) (
							Math.floor(btflyNo/(Math.pow(2, pow)))*(Math.pow(2, pow+1)) + Math.floorMod(btflyNo, (int) Math.pow(2,pow))
							);
					int no1 = (int) ( 
							Math.floor(btflyNo/(Math.pow(2, pow)))*(Math.pow(2, pow+1)) + Math.floorMod(btflyNo, (int) Math.pow(2,pow)) + Math.pow(2, pow)
							);
					
					//System.out.println("btflyNo:"+btflyNo+",m:"+m+ ",j:"+j+ ",K:"+k+", no0:"+no0 + ",no1:" + no1);
					//System.out.println("btflyNo:"+btflyNo);
					
					Complex u = list[btflyNo];
					Complex t = w.mul(list[btflyNo+len/2]);
					
					templist[no0] = u.add(t);
					templist[no1] = u.sub(t);
					w = w.mul(wn); //��תw
					btflyNo++;
				}
			}
			//System.out.println("-----------------------------------");
			Complex[] temp = list;
			list = templist;
			templist = temp;
		}
		return 	list;
	}
	

	public static double log2(double N) {
		return log(N,2);
	}
	
	/***
	 * bit��ת
	 * @param n
	 * @return
	 */
	public static int[] BitReverse (int n) {
		
		int[] rev = new int[256];
		
		int len = 0;
		lim = 1;
		while(lim < n) {
			lim <<= 1;
			len ++;
		}
		
		for (int i = 0; i < lim; i++) {
			rev[i] = (rev[i>>1]>>1) | ((i & 1)<<(len-1)); 
			//rev[i>>1] ������ҵ������� i/2���±�
			//rev[i>>1]>>1 ����һλ, ȡ����ǰn-1λ
			// i & 1 ������ż�ж���λ��0����1 
			// << len- 1 ����len-1λ�����ƶ�����λ
			// Ȼ���ð�λ������ϲ�����
		}
		return Arrays.copyOf(rev, n);
	}
	
	static public double log(double value, double base) {
		return Math.log(value) / Math.log(base);
	}
	
	public static void swap(int a, int b)
	{
	    int temp;
	    temp = a;
	    a = b;
	    b = temp;
	}
	
	public static void swap(Object a, Object b)
	{
		Object temp;
	    temp = a;
	    a = b;
	    b = temp;
	}
	
	
}
