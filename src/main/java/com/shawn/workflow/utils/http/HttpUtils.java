/**
 * Shawn.com Inc.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.shawn.workflow.utils.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.impl.Log4JLogger;

/**
 * 
 * @author shawn
 * @version $Id: HttpUtils.java, v 0.1 Jun 22, 2015 9:13:16 PM shawn Exp $
 */
public class HttpUtils {

    public static ConcurrentMap<String, HttpClient> httpClientCache             = new ConcurrentHashMap<String, HttpClient>();

    private static final Log4JLogger                logger                      = new Log4JLogger(
                                                                                    "HttpClient.class");
    public static String                            lastOrderId                 = "";
    //Set-Cookie=JSESSIONID=R1sJUDEUiSju+tZuuEL2VQ6B1zoHX3imapi; Path=; HttpOnly
    public static String                            lastALIPAYINTLJSESSIONID    = "";

    public final static Pattern                     orderIdPattern              = Pattern
                                                                                    .compile("orderId=[\\w]+&");
    public final static Pattern                     alipayIntlJsessionIdPattern = Pattern
                                                                                    .compile("JSESSIONID=.+imapi");

    public static HttpClient getClientFromCache(String url) {

        HttpClient client = httpClientCache.get(url);
        if (client == null) {
            MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams params = connectionManager.getParams();
            params.setConnectionTimeout(5000);
            params.setSoTimeout(20000);
            params.setDefaultMaxConnectionsPerHost(20); //
            params.setMaxTotalConnections(20);

            HttpClient newClient = new HttpClient(connectionManager);
            client = httpClientCache.putIfAbsent(url, newClient);
            if (client == null) {
                client = newClient;
            }
        }
        logger.info("获取httpClient实例: url=" + url + ", httpClient=" + client);
        return client;
    }

    public static String post(String url, Map<String, String> param) {
        HttpClient client = getClientFromCache(url);
        //client.setRedirectStrategy(new LaxRedirectStrategy());
        PostMethod method = new PostMethod(url) {
            @Override
            public boolean getFollowRedirects() {
                return true;
            }
        };
        try {
            // 添加POST方法的参数.
            if (param != null) {
                for (String key : param.keySet()) {
                    NameValuePair p = new NameValuePair(key, param.get(key));
                    method.addParameter(p);
                }
            }

            logger.info("发送POST请求:" + url + ", param=" + param);
            client.executeMethod(method);
            if (method.getStatusCode() == 200) {
                String rst = method.getResponseBodyAsString();
                logger.info("POST结果：" + rst);
                return rst;
            } else {
                logger.warn("http异常！http状态：" + method.getStatusCode() + ","
                            + method.getStatusText());
            }
        } catch (HttpException e) {
            logger.error("[通讯异常]" + url, e);
        } catch (IOException e) {
            logger.error("[通讯异常]" + url, e);
        } finally {
            // 释放本次httpConnection的占用，交由connectionManager管理便于重用。
            method.releaseConnection();
        }
        return "";
    }

    public static String get(String url, Map<String, String> param) {
        HttpClient client = getClientFromCache(url);
        String ins = "";
        StringBuffer sb = new StringBuffer(url);
        if (param != null) {
            sb.append("?");
            for (String key : param.keySet()) {
                try {
                    sb.append(key).append("=").append(URLEncoder.encode(param.get(key), "UTF-8"))
                        .append("&");
                } catch (UnsupportedEncodingException e) {
                    logger.error("", e);
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        url = sb.toString();
        url = url.replaceAll("\n", "").replaceAll("\r", "");
        GetMethod method = new GetMethod(url) {
            @Override
            public boolean getFollowRedirects() {
                return false;
            }
        };

        logger.info("发送get请求:" + sb);
        try {
            // Execute the method.  
            List<String> urlList = new ArrayList<String>();
            urlList.add(url);
            while (!urlList.isEmpty()) {
                method.setURI(new URI(url));
                int statusCode = client.executeMethod(method);
                logger.info("http状态:" + statusCode);

                if (statusCode == HttpStatus.SC_OK) {
                    ins = method.getResponseBodyAsString();
                    logger.info("get结果：成功");
                    Header[] headers = method.getResponseHeaders();
                    for (int i = 0; i < headers.length; i++) {
                        logger.info("header " + i + ":" + headers[i].getName() + "="
                                    + headers[i].getValue());
                        if (headers[i].getName().equals("Set-Cookie")) {
                            if (headers[i].getValue().startsWith("JSESSIONID")) {
                                lastALIPAYINTLJSESSIONID = patternJsessionId(headers[i].getValue());
                                logger.info(">>>>>lastALIPAYINTLJSESSIONID:"
                                            + lastALIPAYINTLJSESSIONID);
                            }
                        }
                    }
                } else if (statusCode == 302) {
                    Header header = method.getResponseHeader("location");

                    logger.info("重定向->" + header.getValue());
                    //保存重定向地址
                    lastOrderId = patternOrderId(header.getValue());
                    logger.info(">>>>lastOrderId:" + lastOrderId);
                    urlList.add(header.getValue());

                } else {
                    logger.warn("http异常！" + statusCode);
                }
                urlList.remove(0);
            }

        } catch (HttpException e) {
            logger.error("[通讯异常]" + url, e);
        } catch (IOException e) {
            logger.error("[通讯异常]" + url, e);
        } finally {
            method.releaseConnection();

        }
        return ins;
    }

    private static String patternOrderId(String str) {
        //https://icashier.test.alipay.net/payment/checkout.htm?orderId=0610006001netfdab4b2a182615a2423&locale=en_US
        Matcher m = orderIdPattern.matcher(str);
        while (m.find()) {
            str = m.group();
        }
        Pattern p = Pattern.compile("[\\w]+");
        m = p.matcher(str);
        while (m.find()) {
            str = m.group();
        }
        return str;
    }

    private static String patternJsessionId(String cookie) {
        //Set-Cookie=JSESSIONID=InUFsBjsH+4WdBuw61w9bkOfdIYFSDimapi; Path=; HttpOnly

        Matcher m = alipayIntlJsessionIdPattern.matcher(cookie);
        while (m.find()) {
            cookie = m.group();
        }
        return cookie.substring(11);
    }

    public static void main(String[] args) {
        System.out
            .println(patternOrderId("https://icashier.test.alipay.net/payment/checkout.htm?orderId=0610006001netfdab4b2a182615a2423&locale=en_US"));
        System.out
            .println(patternJsessionId("Set-Cookie=JSESSIONID=InUFsBjsH+4WdBuw61w9bkOfdIYFSDimapi; Path=; HttpOnly"));
    }

}
