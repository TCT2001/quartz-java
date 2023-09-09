import java.util.Date;
import java.util.Timer;

public class DemoTaskOnce {
    public static void main(String[] args) {
        MyTask myTask = new MyTask();
        Timer timer = new Timer();
        System.out.println("Currnet time: " + new Date());
        timer.schedule(myTask, 5000);

        //    Calendar calendar = Calendar.getInstance();
        //    calendar.add(Calendar.SECOND, 5);
        //    Date dateSchedule = calendar.getTime();
        //    timer.schedule(myTask, dateSchedule);
    }
}