import java.util.Date;
import java.util.TimerTask;
public class MyTask extends TimerTask {
  @Override
  public void run() {
    System.out.println("Run my Task " + new Date());
  }
}