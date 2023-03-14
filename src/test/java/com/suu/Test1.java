package com.suu;

import com.aliyun.imageaudit20191230.models.ScanTextRequest;
import com.aliyun.imageaudit20191230.models.ScanTextResponse;
import com.aliyun.imageaudit20191230.models.ScanTextResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.su.PlateFormApplication;
import com.su.pojo.view.Aliyvn;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.security.RunAs;
import java.util.List;
@SpringBootTest(classes = PlateFormApplication.class)
@RunWith(SpringRunner.class)
public class Test1 {

    @Autowired
    private Aliyvn aliyvn;

    @Test
    public void test(){
        System.out.println(aliyvn.getAccesskeyId());
    }

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
        // "YOUR_ACCESS_KEY_ID", "YOUR_ACCESS_KEY_SECRET" 的生成请参见：https://help.aliyun.com/document_detail/175144.html
        // 如果您是用的子账号AccessKey，还需要为子账号授予权限AliyunVIAPIFullAccess，请参见：https://help.aliyun.com/document_detail/145025.html
        com.aliyun.imageaudit20191230.Client client =
                Test1.createClient
                        ("LTAI5tRF5zmXhxzYP6SEmsFg",
                                "bGYVhdrhuJWAhAiVdwcastgLRqNB9Y");
        ScanTextRequest.ScanTextRequestTasks tasks = new ScanTextRequest.ScanTextRequestTasks()
                .setContent("12");
        tasks.setContent("毒品");
        ScanTextRequest.ScanTextRequestLabels labels = new ScanTextRequest.ScanTextRequestLabels()
                .setLabel("ad").setLabel("abuse");
        com.aliyun.imageaudit20191230.models.ScanTextRequest scanTextRequest = new com.aliyun.imageaudit20191230.models.ScanTextRequest()
                .setLabels(java.util.Arrays.asList(
                        labels
                ))
                .setTasks(java.util.Arrays.asList(
                        tasks
                ));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            ScanTextResponse response = client.scanTextWithOptions(scanTextRequest, runtime);
            System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(response)));
            List<ScanTextResponseBody.ScanTextResponseBodyDataElements> elements = response.getBody().getData().getElements();
            List<ScanTextResponseBody.ScanTextResponseBodyDataElementsResults> results = elements.get(0).getResults();
            System.out.println(results.get(0).getSuggestion().equals("pass"));


        } catch (TeaException error) {
            // 获取整体报错信息
            System.out.println(com.aliyun.teautil.Common.toJSONString(error));
            // 获取单个字段
            System.out.println(error.getCode());
        }
    }

    @Test
    public void image(){

    }
}
