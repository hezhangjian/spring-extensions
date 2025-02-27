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

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileScheduled {
    /**
     * @see Profile#value()
     */
    String[] value();

    /**
     * @see Scheduled#cron()
     */
    String cron() default "";

    /**
     * @see Scheduled#fixedRate()
     */
    long fixedRate() default -1;

    /**
     * @see Scheduled#fixedDelay()
     */
    long fixedDelay() default -1;

    /**
     * @see Scheduled#initialDelay()
     */
    long initialDelay() default -1;
}
