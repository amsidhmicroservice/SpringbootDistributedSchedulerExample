package com.amsidh.mvc;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@SpringBootApplication
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class SpringbootDistributedSchedulerExampleApplication {
    private final HazelcastInstance hazelcastInstance;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDistributedSchedulerExampleApplication.class, args);
    }

    //@Scheduled(cron = "${cron.expression}")
    @Scheduled(cron = "0 */1 * ? * *") // Every 1 minute
    public void executeMyScheduler() throws InterruptedException {

        // This can also be a member of the class.
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        Lock lock = hazelcastInstance.getCPSubsystem().getLock("mySchedulerName");

        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                // do your schedule tasks here

                long nextLong = new Random().nextLong();

                log.info("Long number displayed {} on time {}", nextLong, new Date());
            } finally {
                // don't forget to release lock whatever happens: end of task or any exceptions.
                //log.info("don't forget to release lock whatever happens: end of task or any exceptions.");
                lock.unlock();
            }
        } else {
            // warning: lock has been released by timeout!
            log.warn("lock has been released by timeout!");
        }
    }
}
