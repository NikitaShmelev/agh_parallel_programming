import java.util.Random;

class Obraz {
    private int size_n;
    private int size_m;
    private char[][] tab;
    private char[] tab_symb;
    private int[] histogram;
    private int[] hist_parallel;

    public Obraz(int n, int m) {
        this.size_n = n;
        this.size_m = m;
        tab = new char[n][m];
        tab_symb = new char[94];

        final Random random = new Random();

        for(int k=0; k<94; k++) {
            tab_symb[k] = (char)(k+33);
        }

        for(int i=0; i<n; i++) {
            for(int j=0; j<m; j++) {
                tab[i][j] = tab_symb[random.nextInt(94)];
                System.out.print(tab[i][j]+" ");
            }
            System.out.print("\n");
        }
        System.out.print("\n\n");

        histogram = new int[94];
        hist_parallel = new int[94];
        clear_histogram();
    }

    public void clear_histogram() {
        for(int i=0; i<94; i++) {
            histogram[i] = 0;
            hist_parallel[i] = 0;
        }
    }

    public synchronized void calculate_histogram() {
        for(int i=0; i<size_n; i++) {
            for(int j=0; j<size_m; j++) {
                for(int k=0; k<94; k++) {
                    if(tab[i][j] == tab_symb[k]) histogram[k]++;
                }
            }
        }
    }

    public synchronized int calculate_for_char(int charIndex) {
        int count = 0;
        for(int i=0; i<size_n; i++) {
            for(int j=0; j<size_m; j++) {
                if(tab[i][j] == tab_symb[charIndex]) {
                    count++;
                }
            }
        }
        hist_parallel[charIndex] = count;
        return count;
    }

    public synchronized void print_for_char(int charIndex, int count, String threadName) {
        System.out.print(threadName + ": " + tab_symb[charIndex] + " ");
        for(int i=0; i<count; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    public void print_histogram() {
        for(int i=0; i<94; i++) {
            System.out.print(tab_symb[i]+" "+histogram[i]+"\n");
        }
    }

    public synchronized boolean compare_histograms() {
        for(int i=0; i<94; i++) {
            if(histogram[i] != hist_parallel[i]) {
                return false;
            }
        }
        return true;
    }
}