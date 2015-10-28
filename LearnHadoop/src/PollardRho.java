import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class PollardRho {

  public static void main(String[] args) {
    int n = 1323;
    int i = 1;
    int x = new Random().nextInt(n);
    int y = x;
    int k = 2;
    Set<Integer> visited = new HashSet<>();
    
    while(true){
      i++;
      x = (x * x - 1 ) % n;
      int d = BigInteger.valueOf(x - y).gcd(BigInteger.valueOf(n)).intValue();
      if(d != 1 && d != n) System.out.print(":");
      System.out.println("x = " + x + ", d = " + d);
      if(visited.contains(x)) break;
      visited.add(x);
      if(i == k){
        k <<= 1;
        y = x;
      }
    }
  }

}
