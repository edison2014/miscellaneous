package parallel;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Token implements Delayed {

  int id;
  long startSeconds;

  Token(int id, long delay) {
    this.id = id;
    startSeconds = System.currentTimeMillis() + delay;
  }
  
  public void setStartTime(long delay) {
    startSeconds = System.currentTimeMillis() + delay;
  }  

  @Override
  public int compareTo(Delayed o) {
    long t2 = ((Token) o).startSeconds;
    if (startSeconds != t2)
      return startSeconds - t2 > 0 ? 1 : -1;
    else
      return 0;
  }


  @Override
  public long getDelay(TimeUnit unit) {
    return unit.convert(startSeconds - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
  }

}


public class Throttle {
  private static DelayQueue<Token> dqt = new DelayQueue<>();

  static {
    Random ran = new Random();
    for (int i = 0; i < 5; ++i) {
      dqt.add(new Token(i, ran.nextInt(1500)));
    }
  }

  class Wind implements Runnable {
    
    int id;
    Wind(int id){
      this.id = id;
    }

    @Override
    public void run() {
      try {
        long start = System.currentTimeMillis();
        Token t = dqt.take();
        t.setStartTime(1500);
        dqt.put(t);
        long end = System.currentTimeMillis();
        TimeUnit unit = TimeUnit.SECONDS;
        System.out.println("id : " +id + " start at "+ unit.convert(start, TimeUnit.MILLISECONDS) + ", end at " + unit.convert(end, TimeUnit.MILLISECONDS));
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
    
  }

  public static void main(String[] args) throws InterruptedException {
    
    ExecutorService tpool = Executors.newCachedThreadPool();
    Random ran = new Random();
    Throttle th = new Throttle();
    int id = 0;
    
      for(int i = 0; i < 50; ++i){
        
        Thread.sleep(ran.nextInt(500));
        tpool.submit(th.new Wind(id));
        id++;
        
      }
    
      tpool.shutdown();
  }

}
