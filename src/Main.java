import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        var ed = new Editor();
        try {
            ed.createAndShowGUI(
                 	"""
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
         System.out.println("12! = " + factorial(12));
     }
 }""");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
