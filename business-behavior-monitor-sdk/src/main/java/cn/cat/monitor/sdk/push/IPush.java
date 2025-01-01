package cn.cat.monitor.sdk.push;

import cn.cat.monitor.sdk.model.LogMessage;

public interface IPush {

    void open(String host, int port);

    void send(LogMessage logMessage);

}
