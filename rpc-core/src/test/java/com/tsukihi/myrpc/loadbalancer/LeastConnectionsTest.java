package com.tsukihi.myrpc.loadbalancer;

import com.tsukihi.myrpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;


public class LeastConnectionsTest {

    LoadBalancer loadBalancer = new LeastConnectionsLoadBalancer();


    @Test
    public void select() {
        // 请求参数
        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put("methodName", "apple");



        // 服务列表
        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceName("myService1");
        serviceMetaInfo1.setServiceVersion("1.0");
        serviceMetaInfo1.setServiceHost("localhost");
        serviceMetaInfo1.setServicePort(1234);


        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo2.setServiceName("myService2");
        serviceMetaInfo2.setServiceVersion("1.0");
        serviceMetaInfo2.setServiceHost("baidu.com");
        serviceMetaInfo2.setServicePort(80);

        ServiceMetaInfo serviceMetaInfo3 = new ServiceMetaInfo();
        serviceMetaInfo3.setServiceName("myService3");
        serviceMetaInfo3.setServiceVersion("1.0");
        serviceMetaInfo3.setServiceHost("xx.com");
        serviceMetaInfo3.setServicePort(80);


        ServiceMetaInfo serviceMetaInfo4 = new ServiceMetaInfo();
        serviceMetaInfo4.setServiceName("myService4");
        serviceMetaInfo4.setServiceVersion("1.0");
        serviceMetaInfo4.setServiceHost("bb.com");
        serviceMetaInfo4.setServicePort(80);


        ServiceMetaInfo serviceMetaInfo5 = new ServiceMetaInfo();
        serviceMetaInfo5.setServiceName("myService5");
        serviceMetaInfo5.setServiceVersion("1.0");
        serviceMetaInfo5.setServiceHost("cc.com");
        serviceMetaInfo5.setServicePort(80);
//        loadBalancer = new LeastConnectionsLoadBalancer(servers);


        List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(serviceMetaInfo1, serviceMetaInfo2, serviceMetaInfo3, serviceMetaInfo4, serviceMetaInfo5);
        // 连续调用 n 次

        requestParams.put("clientIP", "5.5.76.198");
        ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);

        requestParams.clear();
        requestParams.put("ClientIP", "10.10.10.1");
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);

        requestParams.clear();
        requestParams.put("clientIP", "8.8.8.8");
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);


        requestParams.clear();
        requestParams.put("clientIP", "245.245.4.1");
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);


        requestParams.clear();
        requestParams.put("clientIP", "5.5.76.198");
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);


        requestParams.clear();
        requestParams.put("clientIP", "142.49.217.3");
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        // 释放服务
        loadBalancer.releaseServer(serviceMetaInfo);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);


        requestParams.clear();
        requestParams.put("clientIP", "8.8.8.8");
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);


        requestParams.clear();
        requestParams.put("clientIP", "5.5.5.5");
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);
    }
}
