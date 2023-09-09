package quazt;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.Date;
import java.util.List;

public class Main {
    // https://stackoverflow.com/questions/15020625/quartz-how-to-shutdown-and-restart-the-scheduler
    // https://www.freeformatter.com/cron-expression-generator-quartz.html
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        ThreadPBH threadPBH = new ThreadPBH();
        scheduler.getListenerManager()
                .addJobListener(new HelloJobListener(), KeyMatcher.keyEquals(threadPBH.getJobKey()));

        // scheduler.getListenerManager()
        //         .addJobListener(new HelloJobListener(), KeyMatcher.keyEquals(threadPBH.getJobKey()));
        //
        scheduler.scheduleJob(threadPBH.getCurrentJob(), threadPBH.getTrigger());
        System.out.println("===========1==========");
        scheduler.start();
        print(scheduler);
        Thread.sleep(8_000);
        // scheduler.pauseJob(threadPBH.getJobKey());
        scheduler.unscheduleJob(threadPBH.getTrigger().getKey());
        System.out.println("===========2==========");
        print(scheduler);
        Thread.sleep(8_000);
        threadPBH = new ThreadPBH();
        scheduler.scheduleJob(threadPBH.getNewJob(), threadPBH.getTrigger());
        System.out.println("===========3==========");
        print(scheduler);
        Thread.sleep(8_000);
        scheduler.shutdown(true);
    }

    static void print(Scheduler scheduler) throws SchedulerException {
        System.out.println(1);
        for (String groupName : scheduler.getJobGroupNames()) {
            System.out.println(2);
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                System.out.println(3);
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();

                // get job's trigger
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                Date nextFireTime = triggers.get(0).getNextFireTime();

                System.out.println(" ==== [jobName] : " + jobName + " [groupName] : "
                        + jobGroup + " - " + nextFireTime.getTime());

            }

        }
    }
}
