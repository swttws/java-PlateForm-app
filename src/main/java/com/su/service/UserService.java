package com.su.service;

import com.su.common.ResultBean;
import com.su.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.su.pojo.view.UserView;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-20
 */
public interface UserService extends IService<User> {

    ResultBean register(UserView userView);

    ResultBean senEmail(String email);

    ResultBean login(UserView user);

    ResultBean logout(String userName);
}
