import java.io.*;
import java.util.*;

public class JacobiGauss
{
	private double[][] myMat;
	private double[] mySol;

	public static void main (String[] args)
	{
		System.out.print("Jacobi or Gauss-Seidel? (J/G): ");
		Scanner kb = new Scanner(System.in);
		String formula = kb.nextLine();
		if (formula.toUpperCase().equals("J"))
			new JacobiGauss(true);
		else
			new JacobiGauss(false);
	}

	public JacobiGauss(boolean jacobi)
	{	
		getEquations();
		mySol = new double[myMat.length];
		
		//get stopping error
		Scanner kb = new Scanner(System.in);
		System.out.print("Desired stopping error: ");
		double stoppingError = Double.parseDouble(kb.nextLine());

		//get starting solutions for iterative methods
		for (int i = 0; i < myMat.length; i++)
		{
			System.out.print("Starting solution " + i + ": ");
			mySol[i] = Double.parseDouble(kb.nextLine());
		}
		kb.close();

		double norm = stoppingError;
		int count = 0;
		//stop when L2 norm < stoppingError or when iterated 50 times
		while(norm >= stoppingError && count < 50)
		{
			if (jacobi)
				solveJacobian();
			else
				solveGauss();
			
			//print x column vector (row format) after each iteration
			printXColumn();
			
			//calculate L2 norm after each iteration
			norm = l2Norm();
			count++;
		}
		
		//if error not met in 50 iterations, stop, print that error was not reached
		//and print values at 50th iteration as final solution
		if (count >= 50)
			System.out.println("Maxed out at 50 iterations. Error not met.");
		System.out.println("\nFinal Answer:");
		printXColumn();
		System.out.println();
	}

	private void printXColumn() 
	{
		System.out.print("[");
		for (int i = 0; i < mySol.length-1; i++)
			System.out.print(mySol[i] + " ");
		System.out.println(mySol[mySol.length-1] + "]T");

	}

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
			for (int i = 0; i < myMat.length; i++)
				{
					for (int j = 0; j < myMat[i].length; j++)
					{
						if (j == numEq)
							System.out.print("Sum: ");
						else
							System.out.print("Coef.: ");
						myMat[i][j] = in.nextInt();
					}
				}
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
					if (read.hasNextLine())
						read.nextLine();
				}
				read.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void solveGauss()
	{
		double temp = 0;
		for (int i = 0; i < myMat.length; i++)
		{
			temp = myMat[i][myMat[i].length-1]; //temp = right side of equation

			//multiply expected solution by constants and add
			for (int j = 0; j < myMat.length; j++)
			{
				if (i != j)
					temp -= myMat[i][j]*mySol[j];
			}
			
			//divide total product 
			temp /= myMat[i][i];

			//add to mySol[]
			mySol[i] = temp;
		}
	}

	public void solveJacobian()
	{
		double[] solutions = new double[mySol.length];
		double temp = 0;
		for (int i = 0; i < myMat.length; i++)
		{
			temp = myMat[i][myMat[i].length-1]; //temp = right side of equation

			//multiply expected solution by constants and add
			for (int j = 0; j < myMat.length; j++)
			{
				if (i != j)
					temp -= myMat[i][j]*mySol[j];
			}
			
			//divide total product 
			temp /= myMat[i][i];

			//add temp to solutions[]
			solutions[i] = temp;
		}
		//update mySol
		mySol = solutions.clone();
	}

	public double l2Norm()
	{
		double sum = 0;
		for (int i = 0; i < mySol.length; i++)
			sum += mySol[i]*mySol[i];
		return Math.sqrt(sum);
	}
}