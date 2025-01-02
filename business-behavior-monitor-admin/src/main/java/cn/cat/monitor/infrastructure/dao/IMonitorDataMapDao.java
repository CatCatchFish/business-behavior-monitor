package cn.cat.monitor.infrastructure.dao;

import cn.cat.monitor.infrastructure.po.MonitorDataMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataMapDao {

    String queryMonitorNameByMonitoryId(String monitorId);

    List<MonitorDataMap> queryMonitorDataMapList();

}
