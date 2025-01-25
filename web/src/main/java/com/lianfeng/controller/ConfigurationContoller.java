package com.lianfeng.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lianfeng.common.response.R;
import com.lianfeng.model.entity.ConfigVariable;
import com.lianfeng.model.entity.TransmitConfiguration;
import com.lianfeng.model.entity.VariableDetails;
import com.lianfeng.service.IConfigVariableService;
import com.lianfeng.service.IDBTransmitConditionService;
import com.lianfeng.service.ITransmitConfigurationService;
import com.lianfeng.service.IVariableDetailsService;
import com.lianfeng.vo.Condition;
import com.lianfeng.vo.ConditionsVO;
import com.lianfeng.vo.ConfigVariableVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.lianfeng.common.constants.LFanConstants.IS_NO_DELETED;


/**
 * @version 1.8
 * @注释 条件参数配置项
 * @Author liuchuanping
 * @Date 2025-01-25 12:57
 */

@Api(tags = "条件参数配置项")
@RequestMapping("configuration")
@RestController
public class ConfigurationContoller {

    @Autowired
    private ITransmitConfigurationService iTransmitConfigurationService;
    @Autowired
    private IConfigVariableService iConfigVariableService;
    @Autowired
    private IVariableDetailsService iVariableDetailsService;
    @Autowired
    private DBTransmitConditionContoller dbTransmitConditionContoller;

    @ApiOperation(
            value = "用户配置展示",
            notes = "用户配置展示",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("listConfiguration")
    public R listConfiguration() {
        LambdaQueryWrapper<TransmitConfiguration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransmitConfiguration::getIsDeleted, IS_NO_DELETED);
        List<TransmitConfiguration> list = iTransmitConfigurationService.list(wrapper);
        return R.data(list);
    }

    @ApiOperation(

            value = "查看配置",
            notes = "查看配置",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("listVariable")
    public R listVariable(String ids) {
        LambdaQueryWrapper<VariableDetails> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariableDetails::getIsDeleted, IS_NO_DELETED);
        wrapper.eq(VariableDetails::getConfigVariableId, ids);
        List<VariableDetails> list = iVariableDetailsService.list(wrapper);
        return R.data(list);
    }

    @ApiOperation(
            value = "配置项目展示",
            notes = "配置项目展示",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("listConfigVariablee")
    public R listConfigVariablee(String ids) {
        LambdaQueryWrapper<ConfigVariable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigVariable::getIsDeleted, IS_NO_DELETED);
        wrapper.eq(ConfigVariable::getTransmitConfigurationId, ids);
        List<ConfigVariable> list = iConfigVariableService.list(wrapper);
        return R.data(list);
    }

    @ApiOperation(
            value = "删除用户配置展示",
            notes = "删除用户配置展示",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("removeConfiguration")
    public R removeConfiguration(String ids) {
        iTransmitConfigurationService.removeById(ids);
        return R.success();
    }

    @ApiOperation(
            value = "删除配置项目展示",
            notes = "删除配置项目展示",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("removeConfigVariablee")
    public R removeConfigVariable(String ids) {
        iVariableDetailsService.removeById(ids);
        return R.success();
    }

    @ApiOperation(

            value = "删除查看配置",
            notes = "删除查看配置",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("removeVariable")
    public R removeVariable(String ids) {
        iConfigVariableService.removeById(ids);
        return R.success();
    }

/*    @ApiOperation(
            value = "新增或修改配置项目展示",
            notes = "新增或修改配置项目展示",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("saveOrUpdateConfigVariablee")
    public R saveOrUpdateConfigVariable(VariableDetails variableDetails) {
        iVariableDetailsService.saveOrUpdate(variableDetails);
        return R.success();
    }

    @ApiOperation(
            value = "新增或修改用户配置展示",
            notes = "新增或修改用户配置展示",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("saveOrUpdateConfiguration")
    public R saveOrUpdateConfiguration(TransmitConfiguration transmitConfiguration) {
        iTransmitConfigurationService.saveOrUpdate(transmitConfiguration);
        return R.success();
    }

    @ApiOperation(
            value = "新增或修改查看配置",
            notes = "新增或修改查看配置",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("saveOrUpdateVariable")
    public R saveOrUpdateVariable(ConfigVariable configVariable) {
        iConfigVariableService.saveOrUpdate(configVariable);
        return R.success();
    }*/

    @ApiOperation(
            value = "新增或修改",
            notes = "新增或修改",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("save")
    public R save(@RequestBody ConfigVariableVo configVariableVo) {
        iVariableDetailsService.saveOrUpdate(configVariableVo.getVariableDetails());
        iConfigVariableService.saveOrUpdate(configVariableVo.getConfigVariable());
        iTransmitConfigurationService.saveOrUpdate(configVariableVo.getTransmitConfiguration());
        return R.success();
    }

    @ApiOperation(
            value = "启动按钮",
            notes = "启动按钮",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("runTransmits")
    public R runTransmits(String ids) {
        LambdaQueryWrapper<TransmitConfiguration> trWrapper = new LambdaQueryWrapper<>();
        trWrapper.eq(TransmitConfiguration::getId,ids);
        List<TransmitConfiguration> list = iTransmitConfigurationService.list(trWrapper);

        LambdaQueryWrapper<ConfigVariable> configVariableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configVariableLambdaQueryWrapper.eq(ConfigVariable::getTransmitConfigurationId,list.get(0).getId());
        List<ConfigVariable> ConfigVariableList = iConfigVariableService.list(configVariableLambdaQueryWrapper);

        for (ConfigVariable configVariable : ConfigVariableList) {
            ConditionsVO conditionsVO = new ConditionsVO();
            LambdaQueryWrapper<VariableDetails> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(VariableDetails::getConfigVariableId, configVariable.getId());
            List<VariableDetails> variableDetailsList = iVariableDetailsService.list(wrapper);

            List<Condition> condition = new ArrayList<>();
            for (VariableDetails variableDetails : variableDetailsList) {
                Condition item = new Condition();
                item.setField(variableDetails.getField());
                item.setOperator(variableDetails.getOperator());
                item.setValue(variableDetails.getValue());
                condition.add(item);
            }
            conditionsVO.setTableName(configVariable.getName());//添加表名到参数中
            conditionsVO.setCondition(condition);//添加表名到参数中
            dbTransmitConditionContoller.transmitsCondition(conditionsVO);
            System.out.println(conditionsVO);
        }

        return R.success();
    }
}
