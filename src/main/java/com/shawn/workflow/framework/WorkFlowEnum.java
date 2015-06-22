/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.framework;

import java.util.Deque;

import org.apache.commons.logging.impl.Log4JLogger;

import com.google.common.collect.Queues;

/**
 * 工作流枚举
 * @author shawn
 * @version $Id: WorkflowEnum.java, v 0.1 Jun 21, 2015 2:07:03 PM shawn Exp $
 */
public enum WorkFlowEnum {

    //设置业务流程
    BusinessProcess() {
        @Override
        void transit() {
            ActionsEnum.INIT.setNext("FIRST");
        }
    };

    private Log4JLogger        logger  = new Log4JLogger("WorkFlow.class");
    private Deque<ActionsEnum> actions = Queues.newArrayDeque();

    private WorkFlowEnum() {
        this.actions = Queues.newArrayDeque();
    }

    public Deque<ActionsEnum> getActions() {
        return actions;
    }

    public void setActions(Deque<ActionsEnum> actions) {
        this.actions = actions;
    }

    abstract void transit();

    //遍历枚举存储到队列中
    public Deque<ActionsEnum> traversal() {
        ActionsEnum action = ActionsEnum.INIT;
        StringBuffer sb = new StringBuffer();
        this.actions.clear();
        while (null != action && !ActionsEnum.isFinalState(action.name())) {
            sb.append("[" + action + "]->");
            this.actions.offer(action);
            action = ActionsEnum.getActionEnumByValue(action.getNext());
        }
        this.actions.offer(ActionsEnum.FINAL);
        logger.info(sb.append("[" + ActionsEnum.FINAL + "]"));
        return this.actions;
    }
}
