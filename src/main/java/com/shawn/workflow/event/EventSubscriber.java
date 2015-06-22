/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.event;

import org.apache.commons.logging.impl.Log4JLogger;

import com.google.common.eventbus.Subscribe;

/**
 * 
 * @author shawn
 * @version $Id: EventSubscriber.java, v 0.1 Jun 22, 2015 11:26:46 AM shawn Exp $
 */
public class EventSubscriber {
    private Log4JLogger logger = new Log4JLogger(this.getClass().getSimpleName());

    @Subscribe
    public void handleDetailEvent(DetailEvent detailEvent) {
        detailEvent.getContext();
        //监听处理时间
        logger.info("##############Detail Event ##############");
        logger.info("########handle Detail Event #############");
        logger.info("############Detail Event End#############");
    }
}
