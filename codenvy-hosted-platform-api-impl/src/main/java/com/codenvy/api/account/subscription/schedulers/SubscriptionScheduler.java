/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.account.subscription.schedulers;


import org.eclipse.che.api.account.server.SubscriptionService;
import org.eclipse.che.api.account.server.SubscriptionServiceRegistry;
import org.eclipse.che.commons.schedule.ScheduleDelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Run checking of the subscriptions periodically
 *
 * @author Alexander Garagatyi
 */
@Singleton
public class SubscriptionScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionScheduler.class);

    private final SubscriptionServiceRegistry registry;

    @Inject
    public SubscriptionScheduler(SubscriptionServiceRegistry registry) {
        this.registry = registry;
    }

    @ScheduleDelay(initialDelay = 6,
            delay = 360,
            unit = TimeUnit.MINUTES)
    public void checkSubscriptions() {
        LOG.info("Subscription scheduler task is started");
        try {
            for (SubscriptionService subscriptionService : registry.getAll()) {
                subscriptionService.onCheckSubscriptions();
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        LOG.info("Subscription scheduler task is finished");
    }
}
