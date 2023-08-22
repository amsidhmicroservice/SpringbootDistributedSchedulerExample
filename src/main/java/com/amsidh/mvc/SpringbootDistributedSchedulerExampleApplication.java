package com.amsidh.mvc;

import com.hazelcast.cluster.Cluster;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class SpringbootDistributedSchedulerExampleApplication {

    @Qualifier("hazelcastInstance")
    private final HazelcastInstance hazelcastInstance;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDistributedSchedulerExampleApplication.class, args);
    }

    //@Scheduled(cron = "${cron.expression}")
    @Scheduled(cron = "0 */1 * ? * *") // Every 1 minute
    public void executeMyScheduler() {
        String leaderAddress = getOldestMember().getSocketAddress().toString();
        String currentAddress = hazelcastInstance.getCluster().getLocalMember().getSocketAddress().toString();
        if (currentAddress.equals(leaderAddress)) {
            IScheduledExecutorService scheduler = hazelcastInstance.getScheduledExecutorService("distributed-scheduler");
            scheduler.schedule(new MySchedulerJob(), 10, TimeUnit.SECONDS);
        }
    }

    private Member getOldestMember() {
        Cluster cluster = hazelcastInstance.getCluster();
        Member oldestMember = null;
        for (Member member : cluster.getMembers()) {
            if (oldestMember == null || member.getUuid().compareTo(oldestMember.getUuid()) < 0) {
                oldestMember = member;
            }
        }
        return oldestMember;
    }
    static class MySchedulerJob implements Serializable, Runnable {
        @Override
        public void run() {
            long randomLong = new Random().nextLong();
            log.info("Random Long number generated is {} on time {}", randomLong, new Date());
        }
    }
}
