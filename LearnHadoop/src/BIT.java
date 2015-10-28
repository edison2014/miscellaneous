import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class BIT {
    
    Map<Integer, Integer> mii = new HashMap<>();
    int n;
    
    public void updateFreq(int i, int d){
        i=i+1;
        while(i != 0){
            
            if(!mii.containsKey(i)){
                mii.put(i, d);
            }else{
                mii.put(i, mii.get(i)+d);
            }
            i = i + (i & -i);
            
        }
        
    }

    public int prefixsum(int i){
        
        int sum = 0;
        i++;
        while(i != 0){
            
            sum += mii.containsKey(i)?mii.get(i):0;
            i = i - (i & -i);
            
        }
        return sum;
    }

    public int findMedian(){
        int sum = prefixsum(n - 1);
        int mid = (sum + 1)/2;
        int res = 0;
        int bit;
        sum = 0;
        int i = 4;
        System.out.println("mid is = "+mid);
        while(i >= 0){
            bit = 1 << i;
            if(mid >= mii.get(res +  bit)){
                mid -= mii.get(res + bit);
                res += bit;
            }
            --i;
        }
        return mid == 0 ? res - 1 : res;
    }

    public int findMedian1(){
        int dex = 1 << 5, t, bit = 1 << 4, mid = (n + 1)/2;  
        while(bit > 0 && dex >= 1){
            t = dex - bit;
            if(mid <= mii.get(t)){
                dex = t;
            }else{
                mid -= mii.get(t);
            }
            bit >>= 1;
        }
        return dex - 1;
    }
    
    public int findExact(int ind){
      
      if(ind == 0)
        return mii.get(1);
      else{
        int pre = ind;
        ind++;
        int common = ind - (ind & -ind);
        int sum = mii.get(ind);
        while(pre != common){
          sum -= mii.get(pre);
          pre -= pre & -pre;
        }
        return sum;
      }
      
    }

    public static void main(String[] args) {
        
        BIT sol = new BIT();
        
        sol.n = 20;
        int[] a = new int[sol.n];
        Random ran = new Random();
        for(int i = 0; i < sol.n; ++i){
            a[i] = ran.nextInt(sol.n);
            sol.updateFreq(i, a[i]);
            
        }
        
        System.out.println(Arrays.toString(a));
        for(int i = 0; i < sol.n; ++i){
            int j = i; //ran.nextInt(sol.n);
            System.out.println(j+" "+sol.prefixsum(j));
            
        }
        System.out.println(Arrays.toString(a));
        for(int i = 0; i < sol.n; ++i){
            System.out.print(sol.findExact(i)+", ");
            
        }
        System.out.println("median is "+sol.findMedian());
    }

}
