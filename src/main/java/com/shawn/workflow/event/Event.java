/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.event;

import com.shawn.workflow.entity.ContextDO;

/**
 * 
 * @author shawn
 * @version $Id: Event.java, v 0.1 Jun 22, 2015 4:30:50 PM shawn Exp $
 */
public class Event {
    private ContextDO context;

    public Event(ContextDO context) {
        this.context = context;
    }

    /**
     * Getter method for property <tt>context</tt>.
     * 
     * @return property value of context
     */
    public ContextDO getContext() {
        return context;
    }

    /**
     * Setter method for property <tt>context</tt>.
     * 
     * @param context value to be assigned to property context
     */
    public void setContext(ContextDO context) {
        this.context = context;
    }

}
