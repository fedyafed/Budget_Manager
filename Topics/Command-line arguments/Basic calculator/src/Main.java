class Problem {

    public static void main(String[] args) {
        String operator = args[0];
        int a1 = Integer.parseInt(args[1]);
        int a2 = Integer.parseInt(args[2]);
        switch (operator){
            case "+":
                System.out.println(a1 + a2);
                break;
            case "-":
                System.out.println(a1 - a2);
                break;
            case "*":
                System.out.println(a1 * a2);
                break;
            default:
                System.out.println("Unknown operator");
                break;
        }
    }
}