package com.lianfeng.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.model.entity.Account;
import com.lianfeng.mapper.AccountMapper;
import com.lianfeng.service.IAccountService;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【account】的数据库操作Service实现
* @createDate 2024-12-12 21:24:19
*/
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
    implements IAccountService {

}




