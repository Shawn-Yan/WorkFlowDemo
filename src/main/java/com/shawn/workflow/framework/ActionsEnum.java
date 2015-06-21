/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.framework;

import org.apache.commons.lang.StringUtils;

/**
 * 工作流基本动作枚举
 * @author shawn
 * @version $Id: ActionEnum.java, v 0.1 Jun 21, 2015 2:07:23 PM shawn Exp $
 */
public enum ActionsEnum {
    INIT("", ""),

    FIRST("firststep", "SECOND"),

    SECOND("secondstep", "THIRD"),

    THIRD("thirdstep", "FOURTH"),

    FOURTH("fourthstep", "FINAL"),

    FINAL("", "");

    private ActionsEnum(String action, String next) {
        this.action = action;
        this.next = next;
    }

    public static ActionsEnum getActionEnumByValue(String name) {
        for (ActionsEnum action : ActionsEnum.values()) {
            if (action.name().equalsIgnoreCase(name))
                return action;
        }
        return null;
    }

    public static boolean isFinalState(String value) {
        if (StringUtils.equalsIgnoreCase(FINAL.name(), value)) {
            return true;
        } else {
            return false;
        }
    }

    private String action;
    private String next;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
