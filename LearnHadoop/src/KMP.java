import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class KMP {
  
  public List<Integer> matching(String s, String p){
    
    int[] prefix = new int[p.length()];
    prefix[0] = -1;
    for(int i = 1; i < p.length(); ++i){
      int j = prefix[i-1];
      while(j != -1 && p.charAt(j+1) == p.charAt(i)){
        j = prefix[j];
      }
      prefix[i]=p.charAt(j+1)==p.charAt(i)?(j+1):j;
    }
    System.out.println("prefix:"+Arrays.toString(prefix));
    List<Integer> res = new ArrayList<>();
    
    int j = 0;
    for(int i = 0; i < s.length(); ++i){
      if(j < p.length() && s.charAt(i) == p.charAt(j)){
        if(j == p.length() - 1){
          res.add(i - p.length() + 1);
          j = prefix[j];
        }
        ++j;
      }else{
        int k = j - 1;
        while(k != -1 && p.charAt(k + 1) != s.charAt(i)){
          k=prefix[k];
        }
        j = p.charAt(k+1) == s.charAt(i) ? (k+1) : k;
        ++j;
      }
    }
    return res;
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    KMP sol = new KMP();
    System.out.println(sol.matching("abababa", "aba"));
  }

}
