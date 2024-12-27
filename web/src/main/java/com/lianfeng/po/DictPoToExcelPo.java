package com.lianfeng.po;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lianfeng.common.serializer.CustomIntegerDeserializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2024-12-14 22:18
 */
@Data
@ApiModel(value = "Dict参数")
public class DictPoToExcelPo {
    @JsonDeserialize(using = CustomIntegerDeserializer.class)
    @JsonProperty("dict_id")
    private Integer dictId;
    @JsonProperty("vendor")
    private String vendor;
    @JsonProperty("key_words")
    private String keyWords;
    @JsonProperty("account")
    private String account;

}
