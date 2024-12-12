package com.lianfeng.web.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.model.entity.Account;
import com.lianfeng.web.mapper.AccountMapper;
import com.lianfeng.web.service.AccountService;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【account】的数据库操作Service实现
* @createDate 2024-12-12 21:24:19
*/
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
    implements AccountService {

}




