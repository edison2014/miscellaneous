package parallel;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class H2OSemaphore {
  
  static Semaphore h = new Semaphore(0, true);
  static Semaphore o = new Semaphore(0, true);
  
  static class O implements Runnable {
    
    public void run(){
      try {
        o.acquire(2);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      h.release(2);
      System.out.println("O");
    }
    
  }

  static class H implements Runnable {
    
    public void run(){
      o.release(1);
      try {
        h.acquire(1);
        System.out.println("H");
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
  }

  public static void main(String[] args) {
    
    int n = 30;
    Random ran = new Random();
    for(int i = 0; i < n; ++i){
      if(ran.nextInt(n) % 3 == 0){
        new Thread(new O()).start();
      }else{
        new Thread(new H()).start();
      }
    }

  }

}
