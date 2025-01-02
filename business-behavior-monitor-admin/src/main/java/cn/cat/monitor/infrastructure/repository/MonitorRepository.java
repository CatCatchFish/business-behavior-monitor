package cn.cat.monitor.infrastructure.repository;

import cn.cat.monitor.domain.model.valobj.GatherNodeExpressionVO;
import cn.cat.monitor.domain.repository.IMonitorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MonitorRepository implements IMonitorRepository {

    @Override
    public List<GatherNodeExpressionVO> queryGatherNodeExpressionVO(String systemName, String className, String methodName) {
        return null;
    }

}
