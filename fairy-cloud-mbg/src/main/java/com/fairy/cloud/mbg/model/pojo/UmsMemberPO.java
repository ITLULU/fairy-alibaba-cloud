package com.fairy.cloud.mbg.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fairy.common.entity.po.BasePo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员表
 *
 * @author é¹¿å°å¹´
 * @email
 * @date 2022-08-02 21:00:44
 */
@Data
@TableName("ums_member")
public class UmsMemberPO extends BasePo implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 帐号启用状态:0->禁用；1->启用
     */
    private Boolean enabled;
    /**
     * 账号是否过期
     */
    private Boolean accountNonExpired;
    /**
     * 认证没有过期
     */
    private Boolean credentialsNonExpired;
    /**
     * 账号没锁
     */
    private Boolean accountNonLocked;
    /**
     * 性别：0->未知；1->男；2->女
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 所做城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 用户来源
     */
    private Integer sourceType;
}
