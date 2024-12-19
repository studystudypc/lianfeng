package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import com.lianfeng.mapper.DatabaseMapper;
import com.lianfeng.service.IDatabaseService;
import com.lianfeng.service.IDictService;
import com.lianfeng.service.impl.DatabaseServicelmpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version 1.8
 * @注释  Dict模块的控制器实体
 * @Author liuchuanping
 * @Date 2024-12-14 22:19
 */
@Api(tags = "Dict模块")
@RequestMapping("dict")
@RestController
public class DictContoller {

}
