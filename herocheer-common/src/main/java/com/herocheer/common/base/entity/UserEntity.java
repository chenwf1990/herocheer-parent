package com.herocheer.common.base.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenwf
 * @desc 用户基础实体
 * @date 2021/1/4
 * @company 厦门熙重电子科技有限公司
 */
@Data
public class UserEntity implements Serializable {
    private Long id;//用户id
    private String token;//令牌
    private String userName;//用户姓名
    private String phone;//手机号
    private int userType;//用户类型
    private String otherId;//三方id，预留
}

