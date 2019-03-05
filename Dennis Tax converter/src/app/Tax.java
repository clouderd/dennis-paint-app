package app;

public class Tax {

	double grossIncome;
	String state;
	int dependents;
	static int customerCounter;

	Tax(double gi, String st, int depen) {

		grossIncome = gi;
		state = st;
		dependents = depen;
		customerCounter++;
		System.out.println("Preparing the tax data for customer #" + customerCounter);
	}

	public double calcInt() {
		return (grossIncome * 0.33 - dependents * 100);
	}

	public static void convertToEuros(double taxInDollars) {
		System.out.println("Tax in euros: " + taxInDollars / 1.25);
	}

	public static void main(String[] args) {

		Tax t1 = new Tax(60000, "ON", 2);
		Tax t2 = new Tax(65000, "TX", 4);

		double hisTax = t2.calcInt();

		t1.convertToEuros(t1.calcInt());
		t2.convertToEuros(hisTax);
	}

}
