package com.herocheer.common.base.entity;

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
    private Long id;
    private String createdBy;
    private Long createdTime;
    private Long createdId;
    private String updateBy;
    private Long updateTime;
    private Long updateId;
    private String tokenId;
}
