package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author william@StarImmortal
 * @date 2022/9/22
 */
@RestController
@RequestMapping("/websocket")
@Api(tags = "WebSocket")
public class WebSocketController {

	@Autowired
	private WebSocket webSocket;

	@ApiOperation(value = "发送广播消息")
	@PostMapping("/broadcast/send")
	public R sendBroadcastMessage(@RequestParam String message) {
		webSocket.sendAllMessage(message);
		return R.success();
	}

	@ApiOperation(value = "发送单点消息")
	@PostMapping("/single/send")
	public R sendSingleMessage(@RequestParam String userId, @RequestParam String message) {
		webSocket.sendOneMessage(userId, message);
		return R.success();
	}

}
