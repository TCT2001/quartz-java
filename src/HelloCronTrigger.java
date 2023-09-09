import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import quazt.HelloJob;
import quazt.HelloJobListener;

import java.util.TimeZone;

public class HelloCronTrigger {
	public static int PARAM = 0;
	// https://viblo.asia/p/java-quartz-scheduler-gAm5ywAVZdb#_1-job-task-0
	public static void main(String[] args) throws Exception {

		final JobKey jobKey = new JobKey("HelloName", "group1");
		final JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity(jobKey).build();

		// https://www.freeformatter.com/cron-expression-generator-quartz.html
		// 0 0 12 1 * ?
		final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("HelloTriggerName", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?").inTimeZone(
						TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
				)).build();

		final Scheduler scheduler = new StdSchedulerFactory().getScheduler();

		// Listener attached to jobKey
		scheduler.getListenerManager().addJobListener(new HelloJobListener(), KeyMatcher.keyEquals(jobKey));

		//
		scheduler.scheduleJob(job, trigger);
		scheduler.start();
		System.out.println(123);
		Thread.sleep(10_000);

	}
}
