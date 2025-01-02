package cn.cat.monitor.infrastructure.dao;

import cn.cat.monitor.infrastructure.po.MonitorDataMapNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IMonitorDataMapNodeDao {

    MonitorDataMapNode queryMonitoryDataMapNodeList(MonitorDataMapNode monitorDataMapNode);

}
