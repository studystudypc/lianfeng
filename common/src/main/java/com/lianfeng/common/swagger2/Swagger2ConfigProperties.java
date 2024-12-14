package com.lianfeng.common.swagger2;

import com.lianfeng.common.constants.LFanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * swagger2配置属性实体
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger2")
public class Swagger2ConfigProperties {

    private boolean show = true;

    private String groupName = "LF";

    private String basePackage = LFanConstants.BASE_COMPONENT_SCAN_PATH;

    private String title = "LF-server";

    private String description = "LF-server";

    private String termsOfServiceUrl = "http://127.0.0.1:${server.port}";

    private String contactName = "LCP";

    private String contactUrl = "https://check.maokuaiji.com";

    private String contactEmail = "1101637245@qq.com";

    private String version = "1.0";

}
