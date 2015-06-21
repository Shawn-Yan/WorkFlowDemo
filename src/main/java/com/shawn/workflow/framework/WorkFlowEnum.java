/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.framework;

import org.apache.commons.logging.impl.Log4JLogger;

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

    private Log4JLogger logger = new Log4JLogger("WorkFlow.class");

    //private Deque<ActionsEnum> Queues = 

    abstract void transit();

}
