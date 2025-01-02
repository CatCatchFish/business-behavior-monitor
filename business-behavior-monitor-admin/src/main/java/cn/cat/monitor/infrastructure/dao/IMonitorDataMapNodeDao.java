package cn.cat.monitor.infrastructure.dao;

import cn.cat.monitor.infrastructure.po.MonitorDataMapNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataMapNodeDao {

    List<MonitorDataMapNode> queryMonitoryDataMapNodeList(MonitorDataMapNode monitorDataMapNode);

    List<MonitorDataMapNode> queryMonitoryDataMapNodeByMonitorId(String monitorId);

}
