package com.fairy.cloud.order.service.impl;

import com.fairy.cloud.mbg.mapper.OmsOrderItemMapper;
import com.fairy.cloud.mbg.mapper.OmsOrderMapper;
import com.fairy.cloud.mbg.model.dto.OmsOrderParamDTO;
import com.fairy.cloud.mbg.model.pojo.OmsOrderItemPO;
import com.fairy.cloud.mbg.model.pojo.OmsOrderPO;
import com.fairy.cloud.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 鹿少年
 * @date 2022/8/3 18:12
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OmsOrderMapper omsOrderMapper;
    @Autowired
    private OmsOrderItemMapper   omsOrderItemMapper;


    @Override
    public OmsOrderPO createOrder(OmsOrderParamDTO orderParam) {
        OmsOrderItemPO itemPO = new OmsOrderItemPO();
        BeanUtils.copyProperties(orderParam,itemPO);
        omsOrderItemMapper.insert(itemPO);
        OmsOrderPO omsOrderPO = new OmsOrderPO();
        omsOrderPO.setCreateTime(new Date());
        //用户id
        omsOrderPO.setMemberUsername(orderParam.getUserName());
        omsOrderPO.setMemberId(orderParam.getMemeberId());
        //用户名
        omsOrderPO.setOrderSn(omsOrderPO.getOrderSn());
        omsOrderPO.setPayAmount(orderParam.getProductPrice().multiply(BigDecimal.valueOf(orderParam.getProductQuantity())));
        omsOrderMapper.insert(omsOrderPO);

        return null;
    }
}
