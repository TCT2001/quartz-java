package quazt;

import org.quartz.*;

import java.util.TimeZone;

public class ThreadPBH {
    private final JobKey jobKey;
    private JobDetail job;

    // https://www.freeformatter.com/cron-expression-generator-quartz.html
    // 0 0 12 1 * ?
    private final Trigger trigger;

    public ThreadPBH() {
        jobKey = new JobKey("HelloName", "group1");
        job = JobBuilder.newJob(HelloJob.class)
                .withIdentity(jobKey)
                .build();

        // https://www.freeformatter.com/cron-expression-generator-quartz.html
        // 0 0 12 1 * ?
        trigger = TriggerBuilder.newTrigger().withIdentity("HelloTriggerName", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?").inTimeZone(
                        TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
                )).build();
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public JobDetail getCurrentJob() {
        return job;
    }

    public JobDetail getNewJob() {
        job = JobBuilder.newJob(HelloJob.class)
                .withIdentity(jobKey)
                .build();
        return job;
    }

    public Trigger getTrigger() {
        return trigger;
    }
}
