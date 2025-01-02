package cn.cat.monitor.infrastructure.dao;

import cn.cat.monitor.infrastructure.po.MonitorDataMapNodeLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataMapNodeLinkDao {

    List<MonitorDataMapNodeLink> queryMonitorDataMapNodeLinkByMonitorId(String monitorId);

}
