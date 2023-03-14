package com.su.mapper;

import com.su.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
