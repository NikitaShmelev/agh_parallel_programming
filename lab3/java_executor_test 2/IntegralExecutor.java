import java.util.concurrent.*;
import java.util.*;

public class IntegralExecutor {

    // --- Niezależne parametry ---
    private static final double DX       = 1.0e-4;                           // dokładność
    private static final int    NTHREADS = Runtime.getRuntime().availableProcessors();
    private static final int    NTASKS   = NTHREADS * 4;                      // np. 4× więcej zadań

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        double XP = 0.0;                       // początek przedziału
        double XK = Math.PI;                   // koniec przedziału

        ExecutorService pool = Executors.newFixedThreadPool(NTHREADS);
        List<Future<Double>> futures = new ArrayList<>(NTASKS);

        // --- Pętla 1: tworzenie i submit zadań ---
        double subWidth = (XK - XP) / NTASKS;
        for (int i = 0; i < NTASKS; i++) {
            double subXp = XP + i * subWidth;
            double subXk = (i == NTASKS - 1) ? XK : subXp + subWidth; // ostatni domyka przedział

            Calka_callable task = new Calka_callable(subXp, subXk, DX);
            futures.add(pool.submit(task));
        }

        // --- Pętla 2: odbiór wyników ---
        double total = 0.0;
        for (Future<Double> f : futures) {
            total += f.get(); // blokuje do ukończenia zadania
        }

        pool.shutdown();

        // --- Raport ---
        System.out.printf(
                "∫_[%.1f, %.2f] sin(x) dx ≈ %.12f (wartość dokładna: 2.0)%n",
                XP, XK, total
        );
    }
}
