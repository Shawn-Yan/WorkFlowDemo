/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.event;

import org.apache.commons.logging.impl.Log4JLogger;

import com.shawn.workflow.entity.ContextDO;

/**
 * 
 * @author shawn
 * @version $Id: DetailEvent.java, v 0.1 Jun 22, 2015 4:31:21 PM shawn Exp $
 */
public class DetailEvent extends Event {
    private Log4JLogger logger = new Log4JLogger(this.getClass().getSimpleName());

    public DetailEvent(ContextDO context) {
        super(context);
        logger.info("Detal Event");
    }
}
