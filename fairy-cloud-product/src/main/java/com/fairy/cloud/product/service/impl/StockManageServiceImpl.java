package com.fairy.cloud.product.service.impl;

import com.fairy.cloud.mbg.mapper.SmsFlashPromotionProductRelationMapper;
import com.fairy.cloud.mbg.model.SmsFlashPromotionProductRelation;
import com.fairy.cloud.product.model.PmsProductParam;
import com.fairy.cloud.product.service.StockManageService;
import com.fairy.common.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;



@Service
@Slf4j
public class StockManageServiceImpl implements StockManageService {


    @Autowired
    private SmsFlashPromotionProductRelationMapper flashPromotionProductRelationMapper;


    @Override
    public Integer incrStock(Long productId, Long skuId, Integer quanlity, Integer miaosha, Long flashPromotionRelationId) {
        return null;
    }

    @Override
    public Integer descStock(Long productId, Long skuId, Integer quanlity, Integer miaosha, Long flashPromotionRelationId) {
        return null;
    }

    /**
     * 获取产品库存
     * @param productId
     * @param flashPromotionRelationId
     * @return
     */
    @Override
    public CommonResponse<Integer> selectStock(Long productId, Long flashPromotionRelationId) {

        SmsFlashPromotionProductRelation miaoshaStock = flashPromotionProductRelationMapper.selectByPrimaryKey(flashPromotionRelationId);
        if(ObjectUtils.isEmpty(miaoshaStock)){
            return CommonResponse.fail("不存在该秒杀商品！");
        }

        return CommonResponse.success(miaoshaStock.getFlashPromotionCount());
    }


    //验证秒杀时间
    private boolean volidateMiaoShaTime(PmsProductParam product) {
        //当前时间
        Date now = new Date();
        //todo 查看是否有秒杀商品,秒杀商品库存
        if(product.getFlashPromotionStatus() != 1
                || product.getFlashPromotionEndDate() == null
                || product.getFlashPromotionStartDate() == null
                || now.after(product.getFlashPromotionEndDate())
                || now.before(product.getFlashPromotionStartDate())){
            return false;
        }
        return true;
    }

}
