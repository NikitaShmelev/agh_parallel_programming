// Main.java
public class Main {
    public static void main(String[] args) {

        // integrate sin(x) from 0 to π with step 0.001
        double xp = 0.0;
        double xk = Math.PI;
        double dx = 0.001;

        Calka_callable integral = new Calka_callable(xp, xk, dx);
        double result = integral.compute_integral();

        System.out.printf("∫₀^π sin(x) dx ≈ %.9f%n", result);
    }
}
