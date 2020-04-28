package de.mazdermind.gintercom.shared.configuration.framework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskSchedulerConfig {
	@Bean("gintercomTaskScheduler")
	public TaskScheduler createTaskScheduler() {
		return new ThreadPoolTaskScheduler();
	}
}