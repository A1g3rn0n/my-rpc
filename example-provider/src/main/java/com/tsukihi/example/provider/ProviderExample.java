package com.tsukihi.example.provider;

import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.bootstrap.ProviderBootstrap;
import com.tsukihi.myrpc.model.ServiceRegisterInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 */
@Slf4j
public class ProviderExample {

        public static void main(String[] args) {
            // 要注册的服务
            List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
            ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
            serviceRegisterInfoList.add(serviceRegisterInfo);

            // 服务提供者初始化
            ProviderBootstrap.init(serviceRegisterInfoList);
        }
}
