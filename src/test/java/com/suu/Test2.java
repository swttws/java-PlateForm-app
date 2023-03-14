package com.suu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.imageaudit20191230.models.ScanImageResponse;
import com.aliyun.imageaudit20191230.models.ScanImageResponseBody;
import com.aliyun.tea.TeaException;
import com.su.PlateFormApplication;
import com.su.common.ResultBean;
import com.su.ga.gene.PaperIndividual;
import com.su.mapper.QuestionMapper;
import com.su.pojo.Question;
import com.su.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import springfox.documentation.spring.web.json.Json;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(classes = PlateFormApplication.class)
@RunWith(SpringRunner.class)
public class Test2 {
    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.imageaudit20191230.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "imageaudit.cn-shanghai.aliyuncs.com";
        return new com.aliyun.imageaudit20191230.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        // 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.imageaudit20191230.Client client = Test2.createClient("accessKeyId", "accessKeySecret");
        com.aliyun.imageaudit20191230.models.ScanImageRequest.ScanImageRequestTask task0 = new com.aliyun.imageaudit20191230.models.ScanImageRequest.ScanImageRequestTask()
                .setImageTimeMillisecond(1L)
                .setInterval(-1)
                .setImageURL("");
        com.aliyun.imageaudit20191230.models.ScanImageRequest scanImageRequest = new com.aliyun.imageaudit20191230.models.ScanImageRequest()
                .setTask(java.util.Arrays.asList(
                        task0
                ))
                .setScene(java.util.Arrays.asList(
                        "porn","ad","terrorism","live"
                ));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            ScanImageResponse scanImageResponse = client.scanImageWithOptions(scanImageRequest, runtime);
            List<ScanImageResponseBody.ScanImageResponseBodyDataResults> results = scanImageResponse.getBody().getData().getResults();
            results.get(0).getSubResults().get(0).getSuggestion();
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }


    @Test
    public void test(){
        User user=new User(1,"23");
        User user1=new User(2,"23");
        User user2=new User(3,"23");
        List<User> list= Arrays.asList(user,user1,user2);
        String s = JSON.toJSONString(list);
        System.out.println(s);
        List<User> users = JSONArray.parseArray(s, User.class);
        System.out.println(users.get(0).getId());


    }

    @Autowired
    private QuestionMapper questionMapper;

    @Test
    public void test33(){
        Random random=new Random();
        String[] strs={"java","spring","mybatis","docker","rabbitmq","nacos","es"};
        String[] select={"A","B","C","D"};
        int[] a={4,5,6,7,8,9,10,19,20,21,22};
        for (int i = 0; i < 200; i++) {
            Question question = new Question();
            question.setTitle(strs[random.nextInt(7)]+" 相关单选题"+random.nextInt());
            question.setSelectOptions("A.答案,B.答案,C.答案,D.答案" );
            question.setAnswer(select[random.nextInt(4)]);
            question.setAnalysis("没有解析");
            question.setScope(5);
            question.setSubjectId(a[random.nextInt(11)]);
            question.setType(1);
            question.setDifficulty(random.nextInt(3)+1);
            questionMapper.insert(question);
        }

    }

    @Test
    public void test333(){
        Random random=new Random();
        String[] strs={"java","spring","mybatis","docker","rabbitmq","nacos","es"};
        String[] select={"A","B","C","D"};
        int[] a={4,5,6,7,8,9,10,19,20,21,22};
        for (int i = 0; i < 150; i++) {
            Question question = new Question();
            question.setTitle(strs[random.nextInt(7)]+" 相关多选题"+random.nextInt());
            question.setSelectOptions("A.答案,B.答案,C.答案,D.答案" );
            question.setAnswer(select[random.nextInt(2)]+select[random.nextInt(2)+2]);
            question.setAnalysis("没有解析");
            question.setScope(10);
            question.setSubjectId(a[random.nextInt(11)]);
            question.setType(2);
            question.setDifficulty(random.nextInt(3)+1);
            questionMapper.insert(question);
        }

    }

    //判断题
    @Test
    public void test33333(){
        Random random=new Random();
        String[] strs={"java","spring","mybatis","docker","rabbitmq","nacos","es"};
        String[] select={"A","B"};
        int[] a={4,5,6,7,8,9,10,19,20,21,22};
        for (int i = 0; i < 150; i++) {
            Question question = new Question();
            question.setTitle(strs[random.nextInt(7)]+" 相关判断题"+random.nextInt());
            question.setSelectOptions("A.对,B.错" );
            question.setAnswer(select[random.nextInt(2)]);
            question.setAnalysis("没有解析");
            question.setScope(5);
            question.setSubjectId(a[random.nextInt(11)]);
            question.setType(3);
            question.setDifficulty(random.nextInt(3)+1);
            questionMapper.insert(question);
        }

    }

    //大题
    @Test
    public void test333333(){
        Random random=new Random();
        String[] strs={"java","spring","mybatis","docker","rabbitmq","nacos","es"};
        String[] aa={"java中","好好学习","2332"};
        int[] a={4,5,6,7,8,9,10,19,20,21,22};
        for (int i = 0; i < 250; i++) {
            Question question = new Question();
            question.setTitle(strs[random.nextInt(7)]+" 相关大题"+random.nextInt());
//            question.setSelectOptions("A.对,B.错" );
            question.setAnswer(strs[random.nextInt(7)]+","+aa[random.nextInt(3)]
            + UUID.randomUUID());
//            question.setAnalysis("没有解析");
            question.setScope(15);
            question.setSubjectId(a[random.nextInt(11)]);
            question.setType(4);
            question.setDifficulty(random.nextInt(3)+1);
            questionMapper.insert(question);
        }

    }


    @Test
    public void teste(){
        String[] strs={"java","spring","mybatis","docker","rabbitmq"};
        Random random=new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(strs[random.nextInt(5)]);
        }
    }

    @Autowired
    private QuestionService questionService;

    @Test
    public void test55(){
        ResultBean paper = questionService.getPaper(1, 2.3,1);
    }

    @Test
    public void  te(){
        List<Integer> list=Arrays.asList(1,2,3,4,5,6,7,7,8);
        List<Integer> collect = list.stream().filter(i -> i < 5).collect(Collectors.toList());
        list.forEach(System.out::println);
        collect.forEach(System.out::println);
    }


}

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
class  User{
    private int id;
    private String name;
}
