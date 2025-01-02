package cn.cat.monitor.infrastructure.dao;

import cn.cat.monitor.infrastructure.po.MonitorData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataDao {

    List<MonitorData> queryMonitorDataList(MonitorData monitorDataReq);

    void insert(MonitorData monitorDataReq);

}
