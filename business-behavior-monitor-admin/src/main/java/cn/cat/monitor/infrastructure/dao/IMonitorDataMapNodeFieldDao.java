package cn.cat.monitor.infrastructure.dao;

import cn.cat.monitor.infrastructure.po.MonitorDataMapNodeField;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataMapNodeFieldDao {

    List<MonitorDataMapNodeField> queryMonitorDataMapNodeList(MonitorDataMapNodeField monitorDataMapNodeField);

}
