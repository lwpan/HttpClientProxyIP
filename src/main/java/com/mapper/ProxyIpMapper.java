package com.mapper;

import com.httpclientproxy.mycode.ProxyIp;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Tianjinjin on 2016/11/7.
 */
@Component
public interface ProxyIpMapper {
    List<ProxyIp> getProxyIpList();
}
