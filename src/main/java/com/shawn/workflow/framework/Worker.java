/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.framework;

import java.util.Map;

import org.apache.commons.logging.impl.Log4JLogger;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.truth.Truth;
import com.shawn.workflow.entity.ContextDO;
import com.shawn.workflow.entity.ParamDO;
import com.shawn.workflow.event.Event;
import com.shawn.workflow.event.EventSubscriber;

/**
 * 工作流实现的父类
 * @author shawn
 * @version $Id: Worker.java, v 0.1 Jun 21, 2015 1:58:00 PM shawn Exp $
 */
public abstract class Worker<T> {
    public Log4JLogger            logger    = new Log4JLogger(this.getClass().getName());

    public ContextDO              context;
    public ParamDO                params;

    //接口参数Map
    protected Map<String, String> paramMaps = Maps.newConcurrentMap();

    private final EventBus        eventBus  = new EventBus("MINE_DB_CHECK");
    private Event                 event;

    public Worker(ContextDO context, ParamDO params) {
        this.context = context;
        this.params = params;

        eventBus.register(new EventSubscriber());
    }

    public abstract void checkParams(ContextDO context);

    public abstract void firststep();

    public abstract void secondstep();

    public abstract void thirdstep();

    public abstract void fourthstep();

    public void publishEvent() {
        if (null != event) {
            logger.info("发布DB校验事件:[" + event.getClass().getSimpleName() + "]");
            eventBus.post(event);
        } else {
            Truth.ASSERT.withFailureMessage("【DBCheckEvent初始化未完成】").that(event).isNotNull();
        }
    }

}
