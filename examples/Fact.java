public class Fact {
    private static int factorial(int x) {
        if (x <= 1) {
            return 1;
        }

        int f = 1;
        for (int i=1; i<=x; i++) {
            f *= i;
        }
        return f;
    }

     public static void main(String[] args) {
		for (int i=0; i<=10; i++)
         		System.out.println(i + "! = " + factorial(i));
     }
 }