package com.herocheer.common.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenwf
 * @desc
 * @date 2021/1/25
 * @company 厦门熙重电子科技有限公司
 */
@Data
public class UploadFileVO {
    @ApiModelProperty("图片相对地址")
    private String relativeFilePath;

    @ApiModelProperty("图片地址")
    private String filePath;
}
