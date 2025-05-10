class Watek extends Thread {
    private final int charIndex;
    private final Obraz obraz;

    public Watek(int charIndex, Obraz obraz) {
        this.charIndex = charIndex;
        this.obraz = obraz;
    }

    @Override
    public void run() {
        int count = obraz.calculate_for_char(charIndex);
        // obraz.print_for_char(charIndex, count, this.getName());
    }
}