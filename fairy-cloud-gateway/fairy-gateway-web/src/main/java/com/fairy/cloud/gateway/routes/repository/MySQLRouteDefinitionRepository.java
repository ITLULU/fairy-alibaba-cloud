package com.fairy.cloud.gateway.routes.repository;

import com.fairy.cloud.gateway.api.dao.GatewayRouteArgsDao;
import com.fairy.cloud.gateway.api.dao.GatewayRouteDao;
import com.fairy.cloud.gateway.api.entity.po.GatewayRouteArgsPO;
import com.fairy.cloud.gateway.api.entity.po.GatewayRouteDefinition;
import com.fairy.cloud.gateway.api.entity.po.GatewayRoutePO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class MySQLRouteDefinitionRepository implements RouteDefinitionRepository {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;
    @Autowired
    private GatewayRouteDao gatewayRouteDao;
    @Autowired
    private GatewayRouteArgsDao gatewayRouteArgsDao;

    /**
     * Gateway启动时会通过RouteDefinitionRouteLocator.getRoutes方法
     * 将路由规则RouteDefinition转换为Route
     * this.routeDefinitionLocator.getRouteDefinitions().map(this::convertToRoute);
     * 加载到内存中
     **/
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<GatewayRouteDefinition> routePOS = gatewayRouteDao.getGatewayRoute();
        return Flux.fromIterable(GatewayRouteDefinition.toRouteDefinition(routePOS));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<Void> save(Mono<RouteDefinition> route) {
        RouteDefinition definition = route.block();
        if (ObjectUtils.isEmpty(definition.getId())) {
            return Mono.error(new IllegalArgumentException("id may not be empty"));
        }
        GatewayRoutePO routePO = GatewayRoutePO.toGatewayRoute(definition);
        List<GatewayRouteArgsPO> filter = GatewayRouteArgsPO.toGatewayRouteFilterArgs(definition.getFilters(),definition.getId());
        List<GatewayRouteArgsPO> predicate = GatewayRouteArgsPO.toGatewayRoutePredictArgs(definition.getPredicates(),definition.getId());
        //1:先查询
        GatewayRoutePO gatewayRoutePOS = gatewayRouteDao.findGatewayRouteByRouteId(definition.getId());
        if (!Objects.isNull(gatewayRoutePOS)) {
            //更新
            gatewayRouteDao.updateGatewayRoute(routePO);
            gatewayRouteArgsDao.batchUpdateGatewayArgs(filter);
            gatewayRouteArgsDao.batchUpdateGatewayArgs(predicate);
        } else {
            //2: 保存
            gatewayRouteDao.saveGatewayRoute(routePO);
            gatewayRouteArgsDao.bathSaveGatewayArgs(filter);
            gatewayRouteArgsDao.bathSaveGatewayArgs(predicate);
        }

        return Mono.empty();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<Void> delete(Mono<String> routeId) {
//        return routeId.flatMap(r -> {
//            if (ObjectUtils.isEmpty(r)) {
//                return Mono.error(new IllegalArgumentException("id may not be empty"));
//            }
//            //删除路由以及路由 参数
//            gatewayRouteDao.deleteRoute(r);
//            return Mono.empty();
//        });

        String routId = routeId.block();
        if (ObjectUtils.isEmpty(routId)) {
            return Mono.defer(() -> Mono.error(
                new NotFoundException("RouteDefinition not found: " + routeId)));
        }
        gatewayRouteDao.deleteRoute(routId);
        return Mono.empty();
    }


}
