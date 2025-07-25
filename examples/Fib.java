public class Fact {
	// calculates fibonacci sequence
    	private static void fib(int n) {
		int a = 0;
		int b = 1;
		int c = a + b;
		for (int i=0; i<n; i++) {
			System.out.println(a);
			a = b;
			b = c;
			c = a + b;
		}
	}

     public static void main(String[] args) {
         System.out.println("Fibonnaci(10):");
	    fib(10);
	}
 }