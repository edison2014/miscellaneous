import java.util.Arrays;
import java.util.Random;


public class TwoDBIT {
  int[][] array = null;
  int[][] tree = null;
  
  public void update(int x, int y, int val){
    x = x + 1;
    y = y + 1;
    while(x < tree.length){
      int z = y;
      while(y < tree[0].length){
        tree[x][y] += val;
        y += y & -y;
      }
      y = z;
      x += x & -x;
    }
    
  }
  
  public int presum(int x, int y){
    x ++;
    y ++;
    int sum = 0;
    while(x > 0){
      int z = y;
      while(y > 0){
        sum += tree[x][y];
        y -= y & -y;
      }
      y = z;
      x -= x & -x;
    }
    return sum;
  }
  
  public static void main(String[] args) {
    TwoDBIT ins = new TwoDBIT();
    int m = 5;
    int n = 6;
    Random ran = new Random();
    ins.array = new int[m][n];
    ins.tree = new int[m+1][n+1];
    int[][] presum = new int[m][n];
    for(int i = 0; i < m; ++i){
      for(int j = 0; j < n; ++j){
        ins.array[i][j] = ran.nextInt(5);
        ins.update(i, j, ins.array[i][j]);
        presum[i][j] = ins.presum(i, j);
      }
    }
    for(int[] a : ins.array)
      System.out.println(Arrays.toString(a));
    System.out.println("-------------------");
    for(int[] a : ins.tree)
      System.out.println(Arrays.toString(a));
    System.out.println("-------------------");
    for(int[] a : presum)
      System.out.println(Arrays.toString(a));
    System.out.println(ins.presum(2, 2));
  }

}
