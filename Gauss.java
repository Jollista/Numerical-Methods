import java.io.*;
import java.util.Scanner;

public class Gauss
{
	private double[][] myMat;
	private int[] myOrder;

	public static void main(String[] args)
	{
		new Gauss();
	}

	public Gauss()
	{
		getEquations();

		//get s factor
		double[] temp = new double[myMat.length];
		double[] s = new double[myMat.length];
		for (int i = 0; i < temp.length; i++)
		{
			for (int j = 0; j < myMat[i].length-1; j++)
				temp[j] = Math.abs(myMat[i][j]);
			s[i] = getMax(temp);
		}

		//get pivot and eliminate until final step
		double[][][] out = eliminate(s);
		
		//print contents
		for (int i = 0; i < out.length; i++)
		{
			System.out.println("out[" + i + "]:");
			for (int j = 0; j < out[i].length; j++)
			{
				for (int k = 0; k < out[i][j].length; k++)
					System.out.print(out[i][j][k] + " ");
				System.out.println();
			}
			System.out.println();
		}

		double[] sol = solve(out[out.length-1]);
		for (int i = 0; i < sol.length; i++)
			System.out.println("x" + (i+1) + " = " + sol[i]);
	}

	//get equations from a file or manual input
	public void getEquations()
	{
		//Ask user to enter the number of equations
		Scanner in = new Scanner(System.in);
		System.out.print("Number of equations: ");
		String temp = in.nextLine();

		//number of equations/coefficients
		int numEq = Integer.parseInt(temp);
		//initialize myMat
		myMat = new double[numEq][numEq+1];

		//Give choice to enter coefficients
		System.out.print("Enter coefficients via command line? (y/n): ");
		temp = in.nextLine().toUpperCase();
		if (temp.equals("Y") || temp.equals("YES"))
		{
			//Enter via command line
			for (int i = 0; i < numEq; i++)
				{
					for (int j = 0; j < numEq+1; j++)
					{
						if (j == numEq)
							System.out.print("Sum: ");
						else
							System.out.print("Coef.: ");
						myMat[i][j] = in.nextInt();
					}
				}
			in.close();
		}
		else
		{
			//Read from file
			System.out.print("Filename: ");
			String filename = in.nextLine();
			try {
				//read from file into myMat
				Scanner read = new Scanner(new File(filename));
				for (int i = 0; i < numEq; i++)
				{
					for (int j = 0; j < numEq+1; j++)
						myMat[i][j] = read.nextInt();
					if (read.hasNext())
						read.nextLine();
				}
				read.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	//get max value in double array
	public double getMax(double[] a)
	{
		if (a.length == 2)
			return Math.max(a[0], a[1]);
		if (a.length == 1)
			return a[0];
		
		double[] sol = new double[a.length/2+1];
		for (int i = 0; i < a.length; i+=2)
		{
			if(i+1 < a.length)
				sol[i/2] = Math.max(a[i], a[i+1]);
			else
				sol[i/2] = a[i];
		}
		
		return getMax(sol);
	}

	//returns index of pivot equation
	public int getPivot(double[] s, double[][] mat, int lead, int[] ignore)
	{
		double[] ratio = new double[mat.length];
		for (int i = 0; i < mat.length; i++)
			if(ignore[i] == 0)
				ratio[i] = Math.abs(mat[i][lead]/s[i]);
		double key = getMax(ratio);
		for (int i = 0; i < ratio.length; i++)
			if (key == ratio[i])
				return i;
		return -1;
	}

	//use gaussian method to eliminate equations in a system, with steps
	public double[][][] eliminate(double[] s)
	{
		double[][][] output = new double[myMat.length][myMat.length][myMat.length+1]; //array of matrixes as they are eliminated
		int[] ignore = new int[output.length]; //ignore previous pivots, and keep track of order used as pivots
		output[0] = myMat.clone(); //first matrix is copy of myMat
		int pivotIndex;

		for (int level = 0; level < output.length-1; level++)
		{
			pivotIndex = getPivot(s, output[level], level, ignore);
			ignore[pivotIndex] = output.length-level-1;
			for (int i = 0; i < output[level].length; i++)
			{
				if (pivotIndex == i || ignore[i] > 0)
				{
					//skip pivot and prev pivots
					for (int j = 0; j < output[level][i].length; j++)
						output[level+1][i][j] = output[level][i][j];
				}
				else
				{
					double mult = output[level][i][level]/output[level][pivotIndex][level]; //(leading coefficient) / (leading coefficient of pivot)
					for (int j = 0; j < output[level][i].length; j++)
					{
						if (pivotIndex != i) //skip pivot and previous inputs
							output[level+1][i][j] = output[level][i][j] - output[level][pivotIndex][j] * mult; //next matrix's coeff at same location = original coefficient - pivot * mult
					}
				}
			}
		}
		myOrder = ignore;
		return output;
	}

	//solve a system of basic algebraic equations
	public double[] solve(double[][] system)
	{
		double[] solution = new double[system.length];
		for (int i = 0; i < system.length; i++) //go in reverse pivot order and solve equations
		{
			for (int j = 0; j < myOrder.length; j++) //find reverse pivot order
			{
				if (myOrder[j] == i)
				{
					//solveHelper(system[j], i);
					double equation[] = system[j];
					double x = equation[equation.length-1]; //x = sum
					for (int k = equation.length-2; k >= equation.length-2-i; k--) //start at end and subtract from sum until x val, then divide
					{
						if (k != equation.length-2-i) //constant times solved variable
							x = x - (equation[k] * solution[k]);
						else //if leading coefficient
							x=x / equation[k];
					}
					solution[solution.length-1-i] = Math.round(x);
				}
			}
		}
		return solution;
	}
}