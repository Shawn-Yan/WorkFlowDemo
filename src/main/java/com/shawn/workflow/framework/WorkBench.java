/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.framework;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.impl.Log4JLogger;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Queues;
import com.google.common.truth.Truth;
import com.shawn.workflow.entity.ContextDO;
import com.shawn.workflow.entity.ParamDO;
import com.shawn.workflow.process.DetailProcess;

/**
 * 
 * @author shawn
 * @version $Id: WorkBench.java, v 0.1 Jun 21, 2015 2:08:09 PM shawn Exp $
 */
public class WorkBench {
    private Log4JLogger        logger         = new Log4JLogger(this.getClass().getSimpleName());

    private static WorkBench   uniqueInstance = null;
    private Worker             worker;
    private WorkFlowEnum       workFlow;
    private Deque<ActionsEnum> stateQueue     = Queues.newArrayDeque();

    private ContextDO          context;

    //单例模式
    private WorkBench() {

    }

    public static WorkBench getInstance() {
        if (null == uniqueInstance) {
            synchronized (WorkBench.class) {
                if (null == uniqueInstance) {
                    uniqueInstance = new WorkBench();
                }
            }
        }
        return uniqueInstance;
    }

    public void init(WorkFlowEnum wf, Class<DetailProcess> cls, String context, String checkPoints) {
        logger.info("Init the workbench");

        stateQueue.clear();
        this.workFlow = wf;
        workFlow.transit();

        if (context == null) {
            this.context = JSON.parseObject(context, ContextDO.class);
        }

        try {
            InputStream processJsonStream = getClass().getResourceAsStream(
                "/processor/" + cls.getSimpleName() + ".json");
            ParamDO params = new ParamDO();

            //构造Worker
            Constructor constructor = cls
                .getConstructor(ContextDO.class, ParamDO.class, List.class);
            this.worker = (Worker) constructor.newInstance(context, params);
            this.worker.checkParams(this.context);

            logger.info("Added workder:" + cls.getName());
        } catch (Exception e) {
            tearDown();
            Truth.ASSERT.withFailureMessage("【框架初始化失败,检查一下CSV文件格式】").that(false).isTrue();
        }

    }

    public void invoke() {
        logger.info("Invoke the woker flow the workFlow");
        stateQueue.clear();
        stateQueue = workFlow.traversal();

        while (null != stateQueue.peek()) {
            ActionsEnum state = stateQueue.poll();
            String action = state.getAction();

            if (StringUtils.isNotBlank(action)) {
                fire(action, worker);
            }
        }
        logger.info("Invoke the woker flow the workFlow End");
    }

    @SuppressWarnings({ "unused", "unchecked" })
    private void fire(String action, Worker worker) {
        try {
            @SuppressWarnings("rawtypes")
            Class cls = worker.getClass();
            Method method = cls.getMethod(action, null);
            method.invoke(worker, null);
        } catch (InvocationTargetException e) {
            Truth.ASSERT.withFailureMessage(String.valueOf(e.getTargetException())).that(false)
                .isTrue(); // 断言异常
        } catch (Exception e) {
            logger.error(e); //反射异常，正常情况下走不到这里
        }
    }

    public void tearDown() {
        logger.info("Tear down the workbench");
        stateQueue.clear();
        context = null;
        worker = null;
    }

}
