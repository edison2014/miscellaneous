import java.util.Arrays;


public class TwoEggs {

  public static void main(String[] args) {
    
    int n = 100;
    int[] times = new int[n + 1];
    int[] starts = new int[n + 1];

    times[0] = times[1] = 1;
    starts[0] = 0; starts[1] = 1;
    
    
    for(int i = 2; i <= n; ++i){
      times[i] = Integer.MAX_VALUE;
      for(int j = 1; j <= i; ++j){
        int temp = Math.max(j, 1 + times[i - j]);
        if(temp <= times[i]){
          times[i] = temp;
          starts[i] = j;
        }
      }
    }
    System.out.println(Arrays.toString(times));
    System.out.println(Arrays.toString(starts));
  }

}
