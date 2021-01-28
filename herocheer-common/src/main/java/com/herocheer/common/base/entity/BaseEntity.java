package com.herocheer.common.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @desc 公共实体对象
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable {

    @ApiModelProperty("主键id")
    private Long id;
    @ApiModelProperty("创建人名称")
    private String createdBy;
    @ApiModelProperty("创建时间")
    private Long createdTime;
    @ApiModelProperty("创建人id")
    private Long createdId;
    @ApiModelProperty("更新者名称")
    private String updateBy;
    @ApiModelProperty("更新时间")
    private Long updateTime;
    @ApiModelProperty("更新者id")
    private Long updateId;
    @ApiModelProperty("token")
    private String tokenId;
}
