package com.herocheer.common.base.entity;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("用户id")
    private Long id;//用户id
    @ApiModelProperty("令牌")
    private String token;//令牌
    @ApiModelProperty("用户姓名")
    private String userName;//用户姓名
    @ApiModelProperty("手机号")
    private String phone;//手机号
    @ApiModelProperty("用户类型")
    private int userType;//用户类型
    @ApiModelProperty("三方id，预留")
    private String otherId;//三方id，预留
}

