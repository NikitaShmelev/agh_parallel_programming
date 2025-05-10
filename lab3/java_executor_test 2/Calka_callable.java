import java.util.concurrent.Callable;


public class Calka_callable implements Callable<Double> {

	private double dx;
	private double xp;
	private double xk;
	private int    N;

	public Calka_callable(double xp, double xk, double dx) {
		this.xp = xp;
		this.xk = xk;
		this.N  = (int) Math.ceil((xk - xp) / dx);
		this.dx = (xk - xp) / N;
	}

	private double getFunction(double x) {
		return Math.sin(x);
	}

	public double compute_integral() {
		double calka = 0.0;
		for (int i = 0; i < N; i++) {
			double x1 = xp + i * dx;
			double x2 = x1 + dx;
			calka += 0.5 * (getFunction(x1) + getFunction(x2)) * dx;
		}
		return calka;
	}

	// ❷ metoda wymagana przez Callable
	@Override
	public Double call() {
		return compute_integral();
	}
}
