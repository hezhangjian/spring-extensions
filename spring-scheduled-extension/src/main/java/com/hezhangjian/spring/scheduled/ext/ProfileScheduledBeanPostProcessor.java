/*
 * Copyright 2025 hezhangjian <hezhangjian97@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hezhangjian.spring.scheduled.ext;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.ReflectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Configuration
public class ProfileScheduledBeanPostProcessor implements BeanPostProcessor, EnvironmentAware {
    private Environment environment;

    private final TaskScheduler taskScheduler;

    public ProfileScheduledBeanPostProcessor(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        ReflectionUtils.doWithMethods(beanClass, method -> {
            ProfileScheduled annotation = AnnotationUtils.getAnnotation(method, ProfileScheduled.class);
            if (annotation != null) {
                // Check if current profile matches annotation's profiles
                boolean profileActive = Arrays.stream(annotation.value())
                        .anyMatch(profile -> environment.matchesProfiles(profile));

                if (profileActive) {
                    // Register the method as a scheduled task
                    ScheduledMethodRunnable runnable = new ScheduledMethodRunnable(bean, method);
                    if (!annotation.cron().isEmpty()) {
                        CronTrigger cronTrigger = new CronTrigger(annotation.cron());
                        taskScheduler.schedule(runnable, cronTrigger);
                    } else if (annotation.fixedRate() >= 0) {
                        taskScheduler.scheduleAtFixedRate(runnable, Duration.ofMillis(annotation.fixedRate()));
                    } else if (annotation.fixedDelay() >= 0) {
                        if (annotation.initialDelay() > 0) {
                            Instant instant = Instant.now().plusMillis(annotation.initialDelay());
                            taskScheduler.scheduleWithFixedDelay(runnable, instant, Duration.ofMillis(annotation.fixedDelay()));
                        } else {
                            taskScheduler.scheduleWithFixedDelay(runnable, Duration.ofMillis(annotation.fixedDelay()));
                        }
                    }
                }
            }
        });
        return bean;
    }
}
