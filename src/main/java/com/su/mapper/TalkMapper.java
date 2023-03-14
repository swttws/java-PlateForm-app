package com.su.mapper;

import com.su.pojo.Talk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
@Mapper
public interface TalkMapper extends BaseMapper<Talk> {

    List<Talk> selectTalk(@Param("id") Integer id);
}
