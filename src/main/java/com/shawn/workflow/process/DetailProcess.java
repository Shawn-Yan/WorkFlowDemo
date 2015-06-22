/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.process;

import java.util.Map;

import com.shawn.workflow.entity.ContextDO;
import com.shawn.workflow.entity.ParamDO;
import com.shawn.workflow.framework.Worker;

/**
 * 具体处理器
 * @author shawn
 * @version $Id: DetailProcess.java, v 0.1 Jun 22, 2015 6:06:42 PM shawn Exp $
 */
public class DetailProcess extends Worker<Map<String, String>> {

    public DetailProcess(ContextDO context, ParamDO params) {
        super(context, params);
    }

    @Override
    public void firststep() {
    }

    @Override
    public void secondstep() {
    }

    @Override
    public void thirdstep() {
    }

    @Override
    public void fourthstep() {
    }

    @Override
    public void checkParams(ContextDO context) {
    }

}
