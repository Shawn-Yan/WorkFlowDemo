/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.main;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.impl.Log4JLogger;
import org.testng.TestNG;
import org.testng.annotations.Test;

import com.google.common.base.Stopwatch;
import com.shawn.workflow.framework.WorkBench;
import com.shawn.workflow.framework.WorkFlowEnum;
import com.shawn.workflow.process.DetailProcess;

/**
 * 
 * @author shawn
 * @version $Id: DetailClass.java, v 0.1 Jun 22, 2015 6:01:16 PM shawn Exp $
 */
public class DetailClass extends TestNG {
    private Log4JLogger logger = new Log4JLogger(this.getClass().getSimpleName());

    @Test(dataProvider = "DriverDataProvider", description = "具体事务类")
    public void modifyTradePrice(String id, String description, String context, String checkPoints,
                                 Boolean isNeedRun) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        if (!isNeedRun)
            return;

        WorkBench workBench = WorkBench.getInstance();
        workBench.init(WorkFlowEnum.BusinessProcess, DetailProcess.class, context, checkPoints);
        workBench.invoke();
        workBench.tearDown();

        logger.info("用例执行时长:" + stopwatch.elapsed(TimeUnit.SECONDS));
    }

    //DriverDataProvider数据驱动

}
