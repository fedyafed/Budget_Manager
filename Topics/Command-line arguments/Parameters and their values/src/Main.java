class Problem {

    public static void main(String[] args) {
        for (int i = 0; i < args.length / 2; i++) {
            System.out.println(args[i * 2] + "=" + args[i * 2 + 1]);
        }
    }
}