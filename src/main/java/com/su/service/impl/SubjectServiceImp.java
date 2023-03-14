package com.su.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.ResultBean;
import com.su.pojo.Subject;
import com.su.mapper.SubjectMapper;
import com.su.pojo.view.OneTitleView;
import com.su.pojo.view.TwoTitleView;
import com.su.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-26
 */
@Service
public class SubjectServiceImp extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    //查询一级标题一级对应的二级标题
    @Override
    public ResultBean getOneAndTwo() {
        //查询所有一级标题
        List<Subject> subjectList =
                baseMapper.selectList(Wrappers.<Subject>lambdaQuery().eq(Subject::getType, 1));
        //封装到OneView
        List<OneTitleView> oneTitleViewList=new ArrayList<>();
        subjectList.forEach(subject -> oneTitleViewList.
                        add(new OneTitleView(subject.getTitle(),subject.getId(),null)));
        //查询一级标题下所有二级标题
        oneTitleViewList.forEach(oneTitleView -> {
            List<Subject> twoTitleList = baseMapper.selectList(Wrappers.<Subject>lambdaQuery()
                    .eq(Subject::getType, 2)
                    .eq(Subject::getParentId, oneTitleView.getValue()));
            //转换数据
            List<TwoTitleView> twoViewList=new ArrayList<>();
            twoTitleList.forEach(subject -> twoViewList.add(
                    new TwoTitleView(subject.getTitle(),subject.getId())
            ));
            oneTitleView.setChildren(twoViewList);
        });
        return ResultBean.success().data(oneTitleViewList);
    }

    //获取所有一级标题
    @Override
    public List<Subject> getAllOneSubject() {
        List<Subject> subjects = baseMapper.selectList(Wrappers.<Subject>lambdaQuery()
                .eq(Subject::getType, 1));
        return subjects;
    }
}
