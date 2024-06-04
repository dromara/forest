package com.dtflys.test;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.auth.BasicAuth;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestAsyncMode;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestProxyType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorChain;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.retryer.NoneRetryer;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.forest.utils.URLUtils;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.model.Result;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.Okio;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestGenericForestClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"1\", \"data\":\"2\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    public TestGenericForestClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, Forest.config());
    }


    @Test
    public void testRequest() {
        assertThat(Forest.config().request()).isNotNull();
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    /**
     * 测试用的数据对象类型
     */
    private static class Data {
        // 属性 a
        private Integer a;
        // 属性 b
        private Integer b;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public Integer getB() {
            return b;
        }

        public void setB(Integer b) {
            this.b = b;
        }
    }

    @Test
    public void testForestVersion() {
        System.out.println(Forest.VERSION);
    }


    @Test
    public void testRequest_url() throws MalformedURLException {
        ForestRequest<?> request = Forest.request();
        assertThat(request).isNotNull();
        assertThat(request.scheme()).isEqualTo("http");
        assertThat(request.host()).isNull();
        assertThat(request.path()).isEqualTo("/");
        request.url("http://127.0.0.1:8080/test");
        assertThat(request.urlString()).isEqualTo("http://127.0.0.1:8080/test");
        request.url("/abc");
        assertThat(request.urlString()).isEqualTo("http://127.0.0.1:8080/abc");
        request.url("http://forest.dtflyx.com/111");
        assertThat(request.urlString()).isEqualTo("http://forest.dtflyx.com/111");
        request.path("/222");
        assertThat(request.urlString()).isEqualTo("http://forest.dtflyx.com/222");
        request.url(new ForestURL(new URL("http://localhost:8080/333")));
        assertThat(request.urlString()).isEqualTo("http://localhost:8080/333");
        request.address(new ForestAddress("192.168.0.1", 8881));
        assertThat(request.urlString()).isEqualTo("http://192.168.0.1:8881/333");
        request.address("192.168.0.2", 8882);
        assertThat(request.urlString()).isEqualTo("http://192.168.0.2:8882/333");
    }


    @Test
    public void testRequest_query_with_arguments() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("http://localhost:{}/?a={}&b={}&c={}", server.getPort(), "1", "2", "3").execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }

    @Test
    public void testRequest_query_with_numbered_arguments() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("http://localhost:{0}/?a={1}&b={2}&c={3}", server.getPort(), "1", "2", "3").execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_query_repeat() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
    }

    @Test
    public void testRequest_query_repeat2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", Arrays.asList(1, 2, 3))
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
    }

    @Test
    public void testRequest_query_repeat3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().setLogEnabled(true);
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", 1, 2, 3)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
    }

    @Test
    public void testRequest_query_array() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addArrayQuery("a", Arrays.asList(1, 2, 3))
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a[]=1&a[]=2&a[]=3");
    }


    @Test
    public void testRequest_query_encode() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("/")
                .host("localhost")
                .port(server.getPort())
                .addBody("key", "https://www.baidu.com#/?modeversion%3Dminiprogram%26sourceCode%3DGDT-ID-23310731%26mark%3DXZX-WXZF-0805")
                .execute(String.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertBodyEquals("key=https://www.baidu.com#/?modeversion%3Dminiprogram%26sourceCode%3DGDT-ID-23310731%26mark%3DXZX-WXZF-0805");
    }

    public static interface TestClient {

        @Post("http://172.29.231.232:4433/predictions/spell")
        String post(@Var("port") int port, @Body("text") String text);

    }

    @Test
    public void testRequest_query_encode2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("/")
                .host("localhost")
                .port(server.getPort())
                .bodyType(ForestDataType.FORM)
                .addBody("text", "(四)引文标注中版次、卷次、页码、除古籍应与所据版本一致外，一般均应使用阿拉伯数字。（作者系第十二届全国政协委员，中联部原副部长，全国政协议政参政人才库特聘专家）（作者系全国政协常委，民革中央副主席、民革黑龙江省省委会主委 谷振春）“第二个结合”是又一次的思想解放，让我们能够在更广阔的文化空间中，充分运用中华优秀传统文化的宝贵资源，探索面向未来的理论和制度创新“即将召开的全国两会，是继二十大之后，党和国家政治生活中的又一件大事。”“我们党开创的人民代表大会制度、政治协商制度，与中华文明的民本思想，天下共治理念，‘共和’、‘商量’的施政传统，‘兼容并包、求同存异’的政治智慧都有深刻关联。”“我所在的山西省铜川市照金镇就是革命老区。”“这表明一些地方对习近平书记的‘两山’理论没有领会透彻。””一箱箱疫苗从冷链车上迅速搬运至中心计免冷链室，力求保证疫苗的全程冷链。接下来，我们就要打起十二分的精神清点疫苗。”《知识青年下乡插队落户通知书》、《联产承包合同书》以及其参加学术研讨会的音视频资料等。《知识青年下乡插队落户通知书》《联产承包合同书》以及其参加学术研讨会的音视频资料等……10时10分第五讲：新媒体传播的的发展趋势。11月3日上午，黑龙江省十三届人大常委第三十六次会议表决通过了本条例。12月10日吉林纪委监委消息，高福波因涉嫌严重职务违法、职务犯罪，接受吉林省监察委员会监察调查。2012年7月28日晚上，2012年伦敦奥运会男子400米自由泳决赛中，孙杨击败卫冕冠军的韩国选手朴泰桓夺得冠军，以3分40秒14的成绩打破了奥运会纪录，这也是中国男子游泳史上的第一枚奥运会金牌，中国代表团在伦敦奥运会的第三金。2022年3月6日下午，针对益西达瓦委员在十三届全国政协五次会议农业界、社会福利和社会保障界联组会上的发言，习近平总书记语重心长地说，“对困难群众，我们要格外关注、格外关爱、格外关心，帮助他们排忧解难。”5月13日，国务院国有资产监督管理委员会党委书记张玉桌到驻沪中央企业调研，强调要认真贯彻落实党中央部署要求，以学习贯彻习近平新时代中国特色社会主义思想主题教育为契机，进一步深入学习贯彻习近平总书记关于国有企业改革发展和党的建设的重要论述。8月27日晚上23时起，G50九杜路收费站将同步启动免费。卜新兵老师向外方学员讲授中国“十四五规划”、新发展理念与精准扶贫等内容。参加人民政协的各党派团体、各族各界人士，在投身这场艰苦桌绝的抗疫斗争中，对中国共产党的初心使命和以人民为中心的执政理念，对中国特色社会主义理论、理论、制度、文化的优势，对我们国家的综合国力和坚实物质技术基础，有了更加全面深刻的感受和认识。参考价：38-323万元/套，保证金100万元以内5万元/套，100万元以上15万元/套。承接各类冷库、冰水系统及中空调的节能改造、设计安装、维护保养等工程。除了货币基金，姚航管理的管理的债券型基金同样业绩平平，刚刚卸任的东方稳健（基金吧）回报债券，今年以来回报率为4.01%，在可比的1212只基金中排名第760名。此后，王锺主要以演出电影为主，1980年更自编自导自演影片《金手指》，自此成为导演。大规模实施保障性安居工程，是党中央、国务院作出的重要战略部署，是转方式、调结构、普惠生的重大举措。当今世界正在经历百年未有之大变局，世界多极化、经济全球化、社会信息化飞速发展，特别是随着信息技术革命日新月轶，互联网发展大潮风起云涌，我国网民已超8亿，互联网深度融入社会生活。党用伟大奋斗创造了百年伟业，也一定能用新的伟大奋斗创造新伟业。到我们这个时代，一是平安稳定，一是有强烈的民族文化保护、建设的意愿，再有有这个能力。第一次银行抢案是于1933年6月10日、俄亥俄州克拉克县的国家银行，总共抢了将近10600美元左右，之后改前往于印第安纳州和俄亥俄州的各地犯案。殿外左右两侧建长廊两条，陈列与范蠡生平业绩相关的诗碑，并以彩绘璧画等形式，生动介绍范蠡卓越的军事、政治和经商才能。丁志勇在隔年首次获颁“933最受欢迎DJ”奖，并连续四次蝉联该项。二是认真对待投资者调研，同时针对中小投资者的调研需求给与积极反馈，确保中小投资者权利得到有效行使。防核闸门当时因为程序出错而开始再度关了起来，T-850跑到闸门底下将门支撑住而让约翰与凯特爬进去，但当时已经失去人形外表与双腿的T-X再度爬出残骸打算去抓约翰时，T-850拔出自己仅存的一颗氢电池放进了她的嘴中，最后引爆与她一起同归于尽。歌唱家，歌星都是指以歌唱为职业的人，然而传统意义上的歌唱家要比歌星显得更得高望重，更让人肃然起敬些。回到家，给吃了药，哄了一会儿他就睡着了，因为昨晚睡的不好，今天五点就开始折腾，他肯定是累了。会议审议通过《关于全面加强药品监管能力建设的实施意见》，并于4月31日由国务院办公厅印发实施。会议审议通过关于全面加强药品监管能力建设的实施意见，并于4月28日由国务院办公厅印发实施。几天来")
                .execute(String.class);
        mockRequest(server)
                .assertPathEquals("/");
    }

    @Test
    public void testRequest_query_array2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addArrayQuery("a", 1, 2, 3)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a[]=1&a[]=2&a[]=3");
    }


    @Test
    public void testRequest_query_replace() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", "1")
                .replaceQuery("a", 2)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=2");
    }

    @Test
    public void testRequest_query_replace_add() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .replaceOrAddQuery("a", "1")
                .replaceOrAddQuery("a", "2")
                .addQuery("url", "http://localhost/test")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=2&url=http://localhost/test");
    }

    @Test
    public void testRequest_query_replace_add2() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<?> request = Forest.get("/")
                .port(server.getPort())
                .replaceOrAddQuery("a", "1")
                .replaceOrAddQuery("a", "2")
                .addQuery("url", "http://localhost/test", true, "UTF-8");
        request.execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=2&url=http://localhost/test");
    }

    @Test
    public void testRequest_query_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        Forest.get("/")
                .port(server.getPort())
                .addQuery(map)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }


    @Test
    public void testAuth_UsernamePassword() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request =
                Forest.get("http://foo:bar@localhost:{}/xxx", server.getPort());
        assertThat(request.userInfo()).isEqualTo("foo:bar");
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Basic Zm9vOmJhcg==");
    }

    @Test
    public void testAuth_BasicAuth() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .authenticator(new BasicAuth("foo", "bar"))
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Basic Zm9vOmJhcg==");
    }

    @Test
    public void testAuth_BasicAuth2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .authenticator(new BasicAuth("foo:bar"))
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Basic Zm9vOmJhcg==");
    }


    @Test
    public void testAsyncPool() {
        ForestConfiguration config = ForestConfiguration.createConfiguration()
                .setMaxAsyncThreadSize(100)
                .setMaxAsyncQueueSize(100);
        LogConfiguration logConfiguration = new LogConfiguration();
        logConfiguration.setLogEnabled(false);
        for (int j = 0; j < 10; j++) {
            final int total = 10;
            for (int i = 0; i < total; i++) {
                MockResponse response = new MockResponse().setBody(EXPECTED);
                if (i == 9) {
                    response.setHeadersDelay(2, TimeUnit.SECONDS);
                }
                server.enqueue(response);
            }
            final CountDownLatch latch = new CountDownLatch(total);
            final AtomicInteger count = new AtomicInteger(0);
            final AtomicInteger errorCount = new AtomicInteger(0);
            for (int i = 0; i < total; i++) {
                config.get("/")
                        .host("localhost")
                        .port(server.getPort())
                        .addAttachment("num", i + "-" + j)
                        .connectTimeout(200)
                        .readTimeout(200)
                        .async()
                        .setLogConfiguration(logConfiguration)
                        .onSuccess((data, req, res) -> {
                            latch.countDown();
                            int c = count.incrementAndGet();
                            System.out.println(Thread.currentThread().getName() + " 成功: " + req.getAttachment("num"));
                            if (c == total) {
//                                System.out.println("第一阶段: 循环已完成");
                            } else {
//                                System.out.println("已成功 第一阶段: " + c);
                            }
                        })
                        .onError((ex, req, res) -> {
                            latch.countDown();
                            int c = count.incrementAndGet();
                            errorCount.incrementAndGet();
                            System.out.println(Thread.currentThread().getName() + " 失败: " + req.getAttachment("num"));
                            if (c == total) {
//                                System.out.println("第一阶段: 循环已完成");
                            } else {
//                                System.out.println("已失败 第一阶段: " + c);
                            }

                            ex.printStackTrace();
                        })
                        .execute();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
            }
            assertThat(errorCount.get()).isGreaterThan(0);
//            System.out.println("第一阶段: 全部已完成");

            for (int i = 0; i < total; i++) {
                server.enqueue(new MockResponse().setHeader("Status", "Ok"));
            }
            final CountDownLatch latch2 = new CountDownLatch(total);
            final AtomicInteger count2 = new AtomicInteger(0);
            final AtomicInteger errorCount2 = new AtomicInteger(0);
            for (int i = 0; i < total; i++) {
                config.head("/")
                        .host("localhost")
                        .port(server.getPort())
                        .async()
                        .setLogConfiguration(logConfiguration)
                        .onSuccess((data, req, res) -> {
                            latch2.countDown();
                            int c = count2.incrementAndGet();
                            if (c == total) {
//                                System.out.println("第二阶段: 循环已完成");
                            } else {
//                                System.out.println("已成功 第二阶段: " + c);
                            }
                        })
                        .onError((ex, req, res) -> {
                            latch2.countDown();
                            int c = count2.incrementAndGet();
                            if (ex != null) {
//                                System.out.println("第二阶段 异常: " + ex);
                                errorCount2.incrementAndGet();
                            }
                            if (c == total) {
//                                System.out.println("第二阶段: 循环已失败");
                            } else {
                                System.out.println("已失败 第二阶段: " + c);
                            }
                        })
                        .execute();
            }
            try {
                latch2.await();
            } catch (InterruptedException e) {
            }
            assertThat(errorCount2.get()).isEqualTo(0);
//            System.out.println("全部已完成");
        }
    }


    @Test
    public void testRequest_query_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> map = new LinkedHashMap<>();
        List<Integer> list = Arrays.asList(10, 20, 30);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.put("x", list);
        Forest.get("/")
                .port(server.getPort())
                .addQuery(map)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&x=10&x=20&x=30");
    }

    public static class MyQuery {

        @JSONField(ordinal = 0)
        private Integer a;

        @JSONField(ordinal = 1)
        private Integer b;

        @JSONField(ordinal = 2)
        private Integer c;

        @JSONField(ordinal = 3)
        private List<Integer> x;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public Integer getB() {
            return b;
        }

        public void setB(Integer b) {
            this.b = b;
        }

        public Integer getC() {
            return c;
        }

        public void setC(Integer c) {
            this.c = c;
        }

        public List<Integer> getX() {
            return x;
        }

        public void setX(List<Integer> x) {
            this.x = x;
        }
    }

    @Test
    public void testRequest_query_obj() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        MyQuery myQuery = new MyQuery();
        myQuery.setA(1);
        myQuery.setB(2);
        myQuery.setC(3);
        Forest.get("/")
                .port(server.getPort())
                .addQuery(myQuery)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_query_obj2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        MyQuery myQuery = new MyQuery();
        myQuery.setA(1);
        myQuery.setB(2);
        myQuery.setC(3);
        myQuery.setX(Arrays.asList(10, 20, 30));
        Forest.get("/")
                .port(server.getPort())
                .addQuery(myQuery)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&x=10&x=20&x=30");
    }

    @Test
    public void testRequest_query_json() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        MyQuery myQuery = new MyQuery();
        myQuery.setA(1);
        myQuery.setB(2);
        myQuery.setC(3);
        myQuery.setX(Arrays.asList(10, 20, 30));
        Forest.get("/")
                .port(server.getPort())
                .addJSONQuery("json", myQuery)
                .addQuery("x", "\"`~\"")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("json", JSON.toJSONString(myQuery));
    }


    @Test
    public void testRequest_host_port() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://xxxxx:444/path")
                .host("localhost")
                .port(server.getPort())
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/path");
    }

    @Test
    public void testRequest_address() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<?> request = Forest.get("http://xxxxx:444/path")
                .address("localhost", server.getPort());
        assertThat(request.host()).isEqualTo("localhost");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/path");
    }


    @Test
    public void testRequest_path() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/A");
    }

    @Test
    public void testRequest_change_path() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .path("/B")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/B");
    }

    @Test
    public void testRequest_change_base_path() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .basePath("1/2/3/a/b/c")
                .basePath("X/Y/Z")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/Y/Z/A");
    }

    @Test
    public void testRequest_change_base_path2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .basePath("X")
                .path("B")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/B");
    }

    @Test
    public void testRequest_change_base_path3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .path("B")
                .basePath("X")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/B");
    }

    @Test
    public void testRequest_change_base_path4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:{}/abc/A", server.getPort())
                .basePath("X")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/abc/A");
    }

    @Test
    public void testRequest_change_base_path5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = Forest.get("/A")
                .basePath("http://localhost:" + server.getPort() + "/X1/X2");
        String result = request.executeAsString();
        assertThat(request.basePath()).isEqualTo("/X1/X2");
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X1/X2/A");
    }

    @Test
    public void testRequest_change_base_path6() {
        ForestRequest request = Forest.get("/A")
                .host("baidu.com")
                .port(1234)
                .basePath("http://localhost:8080/X1/X2");
        assertThat(request).isNotNull();
        assertThat(request.host()).isEqualTo("localhost");
        assertThat(request.port()).isEqualTo(8080);
        assertThat(request.urlString()).isEqualTo("http://localhost:8080/X1/X2/A");
    }

    @Test
    public void testRequest_change_base_path7() {
        ForestRequest request = Forest.get("/A")
                .basePath("http://localhost:8080/X1/X2")
                .host("baidu.com")
                .port(1234);
        assertThat(request).isNotNull();
        assertThat(request.host()).isEqualTo("baidu.com");
        assertThat(request.port()).isEqualTo(1234);
        assertThat(request.urlString()).isEqualTo("http://baidu.com:1234/X1/X2/A");
    }


    @Test
    public void testRequest_change_url() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<?> request = Forest.get("/A")
                .host("127.0.0.1")
                .port(server.getPort())
                .url("/B");
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/B");
    }

    @Test
    public void testRequest_template_in_url() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().setVariableValue("testVar", "foo");
        Forest.get("/test/{testVar}")
                .host("127.0.0.1")
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals("/test/foo");
    }

    @Test
    public void testRequest_get_return_string() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:{}", server.getPort()).execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRequest_get_return_string2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:{}", server.getPort()).executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }



/*
    @Test
    public void testRequest_get_http_1_0() {
        server.setProtocols(Lists.newArrayList(Protocol.HTTP_2));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest
                .get("http://localhost:" + server.getPort())
                .setProtocol(ForestProtocol.HTTP_2)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }
*/


    @Test
    public void testRequest_get_return_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .executeAsMap();
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("1");
        assertThat(result.get("data")).isEqualTo("2");
    }

    @Test
    public void testRequest_get_return_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .execute(new TypeReference<Map<String, String>>() {
                }.getType());
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("1");
        assertThat(result.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_get_return_list() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3]"));
        List<Integer> result = Forest.get("http://localhost:{}", server.getPort()).executeAsList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testRequest_get_return_map_list() {
        server.enqueue(new MockResponse().setBody("[{\"a\": 1}, {\"b\": 2}, {\"c\": 3}]"));
        List<Map<String, Object>> result = Forest.get("http://localhost:{}", server.getPort()).executeAsList();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).get("a")).isEqualTo(1);
        assertThat(result.get(1).get("b")).isEqualTo(2);
        assertThat(result.get(2).get("c")).isEqualTo(3);
    }

    @Test
    public void testRequest_get_return_response() {
        server.enqueue(new MockResponse().setBody("{\"a\": 1, \"b\": 2, \"c\": 3}"));
        ForestResponse<Map<String, Object>> response = Forest.get("http://localhost:{}", server.getPort())
                .execute(new TypeReference<ForestResponse<Map<String, Object>>>() {
                });
        assertThat(response).isNotNull();
        Map<String, Object> result = response.getResult();
        assertThat(result).isNotNull();
        assertThat(result.get("a")).isEqualTo(1);
        assertThat(result.get("b")).isEqualTo(2);
        assertThat(result.get("c")).isEqualTo(3);
        assertThat(response.isClosed()).isTrue();
        assertThat(response.getResult()).isNotNull();
    }


    @Test
    public void testRequest_get_return_list2() {
        server.enqueue(new MockResponse().setBody("[\"1\", \"2\", \"3\"]"));
        List<String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .executeAsList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList("1", "2", "3"));
    }


    @Test
    public void testRequest_get_return_type() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Type type = new TypeReference<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> result = Forest.get("http://localhost:{}", server.getPort()).execute(type);
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo(1);
        assertThat(result.get("data")).isEqualTo(2);
    }


    @Test
    public void testRequest_get_return_JavaObject() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Result result = Forest.get("http://localhost:{}", server.getPort()).execute(Result.class);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo("2");
    }

    @Test
    public void testRequest_get_return_JavaObject_with_genericType() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort()).execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
    }

    @Test
    public void testRequest_get_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_get_query_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery(map)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }


    @Test
    public void testRequest_get_query_obj() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Data data = new Data();
        data.setA(1);
        data.setB(2);
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery(data)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }


    @Test
    public void testRequest_post_text_without_content_type() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:{}", server.getPort())
                .addBody("xxxxxxxyyyyyyy")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }

    @Test
    public void testRequest_post_form_without_content_type() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("value", "bar");
        map.put("name", "foo");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:{}", server.getPort())
                .addBody(map)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("value=bar&name=foo");
    }


    @Test
    public void testRequest_post_invalid_json() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:{}", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addBody("xxxxxxxyyyyyyy")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }

    @Test
    public void testRequest_content_type_with_charset() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .contentTypeJson()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("name", "Forest.backend = okhttp3")
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .executeAsString();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_query() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", "1")
                .addQuery("b", "2")
                .addQuery("c", req -> "3")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_lazy_query2() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", 1)
                .addQuery("b", 2)
                .addQuery("c", req -> 3)
                .addQuery("token", req -> Base64.encode(req.getQueryString()))
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&token=" + URLUtils.encode(Base64.encode("a=1&b=2&c=3"), "UTF-8"));
    }


    @Test
    public void testRequest_lazy_query3() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        LazyData data = new LazyData();
        data.setId("foo");
        data.setName("bar");
        data.setToken(req -> Base64.encode(req.getQueryString()));

        Forest.get("/")
                .port(server.getPort())
                .addQuery(data)
                .execute();

        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("id=foo&name=bar&token=" + URLUtils.encode(Base64.encode("id=foo&name=bar"), "UTF-8"));
    }

    @Test
    public void testRequest_lazy_query4() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("a", "1");
        data.put("b", "2");
        data.put("c", (Lazy<Object>) (req -> "3"));
        data.put("token", (Lazy<Object>) (req -> Base64.encode(req.getQueryString())));

        Forest.get("/")
                .port(server.getPort())
                .addQuery(data)
                .execute();

        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&token=" + URLUtils.encode(Base64.encode("a=1&b=2&c=3"), "UTF-8"));
    }


    @Test
    public void testRequest_body_encode_null_value() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final UserParam param = new UserParam();
        param.setUsername("xxx");
        final ForestRequest request = Forest.post("/")
                .port(server.getPort())
                .bodyType(ForestDataType.JSON)
                .addBody(param);
        System.out.println(request.body()
                .encodeToString(
                        ConvertOptions.defaultOptions()
                                .nullValuePolicy(ConvertOptions.NullValuePolicy.WRITE_EMPTY_STRING)));
    }

    @Test
    public void testRequest_blank_header() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .contentTypeJson()
                .addHeader("name", " x xx ")
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("name", "x xx");
    }

    @Test
    public void testRequest_lazy_header() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .contentTypeJson()
                .addHeader("Content-Type", req -> "application/json; charset=UTF-8")
                .addHeader("name", req -> "Forest.backend = " + req.getBackend().getName())
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("name", "Forest.backend = " + Forest.config().getBackend().getName())
                .assertBodyEquals("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_header2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Authorization", req -> Base64.encode("Token=" + req.body().encodeToString()))
                .addBody("id", "1972664191")
                .addBody("name", "XieYu20011008")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization",
                        Base64.encode("Token={\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}"))
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_header3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .contentFormUrlEncoded()
                .addHeader("Authorization", req -> Base64.encode("Token=" + req.body().encodeToString()))
                .addBody("id", "1972664191")
                .addBody("name", "XieYu20011008")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization",
                        Base64.encode("Token=id=1972664191&name=XieYu20011008"))
                .assertBodyEquals("id=1972664191&name=XieYu20011008")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded");
    }


    @Test
    public void testRequest_lazy_body() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(
                        req.body().encodeToString(
                                ConvertOptions.defaultOptions().exclude("token"))))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_body3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(req.body().encode()))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_body4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(req.body().encode(ForestDataType.FORM)))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("id=1972664191&name=XieYu20011008") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", "1972664191");
        data.put("name", "XieYu20011008");

        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .addBody("token", req -> Base64.encode(req.body().encode()))
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body6() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", "1972664191");
        data.put("name", "XieYu20011008");
        data.put("token", (Lazy<Object>) (req -> Base64.encode(req.body().encode())));

        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    public static class LazyData {

        @JSONField(ordinal = 0)
        private String id;

        @JSONField(ordinal = 1)
        private String name;

        @JSONField(ordinal = 2)
        private Lazy<Object> token;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Lazy<Object> getToken() {
            return token;
        }

        public void setToken(Lazy<Object> token) {
            this.token = token;
        }
    }

    @Test
    public void testRequest_lazy_body7() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        LazyData data = new LazyData();
        data.setId("1972664191");
        data.setName("XieYu20011008");
        data.setToken(req -> Base64.encode(req.body().encode()));

        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_post_invalid_json_byte_array() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String body = "xxxxxxxyyyyyyy";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String result = Forest.post("http://localhost:{}", server.getPort())
                .contentTypeJson()
                .addBody(bytes)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }


    @Test
    public void testRequest_post_form_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        ForestRequest request = Forest.post("http://localhost:{}/post", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2);
        assertThat(request.body().nameValuesMapWithObject()).extracting("a", "b").contains(1, 2);
        Result<Integer> result = (Result<Integer>) request.execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2");
    }

    @Test
    public void testRequest_post_form_body_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentFormUrlEncoded()
                .addBody(map)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2");
    }

    @Test
    public void testRequest_post_form_body_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentFormUrlEncoded()
                .addBody(map)
                .addBody("c", 3)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_post_json_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody("a", 1)
                .addBody("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2}");
    }


    @Test
    public void testRequest_post_json_body_string() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody("{\"a\":1,\"b\":2}")
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2}");
    }

    @Test
    public void testRequest_post_json_body_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody(map)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2}");
    }

    @Test
    public void testRequest_post_json_body_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        ForestRequest request = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody(map)
                .addBody("c", 3);
        assertThat(request.body().nameValuesMapWithObject())
                .extracting("a", "b", "c")
                .contains(1, 2, 3);
        Result<Integer> result = (Result<Integer>) request.execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2,\"c\":3}");
    }

    @Test
    public void testRequest_post_json_body_obj() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Data data = new Data();
        data.setA(1);
        data.setB(2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody(data)
                .addBody("c", 3)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2,\"c\":3}");
    }

    @Test
    public void testRequest_post_xml_body_text() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<misc>\n" +
                "    <a>1</a>\n" +
                "    <b>2</b>\n" +
                "</misc>\n";
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final String result = Forest.post("http://localhost:{}/post", server.getPort())
                .addHeader("Content-Type", "application/xml")
                .addBody(xml)
                .executeAsString();
        assertThat(result).isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(xml);
    }


    @Test
    public void testRequest_post_xml_body_gzip() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<misc>\n" +
                "    <a>1</a>\n" +
                "    <b>2</b>\n" +
                "</misc>\n";
        final byte[] compress = GzipUtils.compressGzip(xml);
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final String result = Forest.post("http://localhost:{}/post", server.getPort())
                .addHeader("Content-Encoding", "gzip")
                .addHeader("Content-Type", "application/xml")
                .addBody(compress)
                .executeAsString();
        assertThat(result).isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(compress);
    }


    @Test
    public void testRequest_put_form_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.put("http://localhost:{}/put", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/put")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2");
    }

    @Test
    public void testRequest_on_body_encode() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/encoded", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2)
                .addInterceptor(new Interceptor() {
                    @Override
                    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
                        String str = new String(encodedData);
                        byte[] bytes = (str + "&c=3").getBytes();
                        return bytes;
                    }
                })
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/encoded")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2&c=3");
    }

    @Test
    public void testRequest_on_body_encode_multi() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/encoded", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2)
                .addInterceptor(new Interceptor() {
                    @Override
                    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
                        String str = new String(encodedData);
                        return (str + "&c=3").getBytes();
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
                        String str = new String(encodedData);
                        return (str + "&d=4").getBytes();
                    }
                })
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/encoded")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2&c=3&d=4");
    }


    @Test
    public void testRequest_delete_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.delete("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("DELETE")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }


    @Test
    public void testRequest_head_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.head("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute();
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_options_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.options("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("OPTIONS")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_patch_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.patch("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "httpclient")
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_trace_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.trace("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("TRACE")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_upload_file() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        Result<Integer> result = Forest.post("http://localhost:{}", server.getPort())
                .contentTypeMultipartFormData()
                .addFile("file", file)
                .executeAsResponse()
                .get(new TypeReference<Result<Integer>>() {
                });
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/");
    }


    @Test
    public void testRequest_sync_retryWhen_success() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicInteger count = new AtomicInteger(0);
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .maxRetryInterval(2)
                .retryWhen(((req, res) -> res.statusIs(203)))
                .onRetry((req, res) -> count.incrementAndGet());
        request.execute();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
        assertThat(count.get()).isEqualTo(3);
    }

    @Test
    public void testRequest_noneRetryer() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        AtomicInteger count = new AtomicInteger(0);
        ForestResponse response = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .maxRetryInterval(0)
                .retryer(NoneRetryer.class)
                .onRetry((req, res) -> count.incrementAndGet())
                .execute(ForestResponse.class);
        ForestRetryer retryer = response.getRequest().getRetryer();
        assertThat(retryer).isNotNull().isInstanceOf(NoneRetryer.class);
        assertThat(response.getRequest().getCurrentRetryCount()).isEqualTo(0);
        assertThat(count.get()).isEqualTo(0);
    }


    @Test
    public void testRequest_async_mode() {
        assertThat(Forest.get("/").asyncMode())
                .isNotNull()
                .isEqualTo(ForestAsyncMode.PLATFORM);

        Forest.config().setAsyncMode(ForestAsyncMode.KOTLIN_COROUTINE);
        assertThat(Forest.get("/").asyncMode())
                .isNotNull()
                .isEqualTo(ForestAsyncMode.KOTLIN_COROUTINE);

        Forest.config().setAsyncMode(ForestAsyncMode.PLATFORM);
        assertThat(Forest.get("/").asyncMode())
                .isNotNull()
                .isEqualTo(ForestAsyncMode.PLATFORM);
    }

    @Test
    public void testRequest_async_future() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Future<String> future = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .execute(new TypeReference<Future<String>>() {
                });
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
        String result = future.get();
        assertThat(result).isEqualTo(EXPECTED);
    }


    @Test
    public void testRequest_async_future2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .get(String.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testRequest_async_await() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map map = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .await()
                .get(Map.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(map.get("status")).isEqualTo("1");
        assertThat(map.get("data")).isEqualTo("2");
    }

    @Test
    public void testRequest_async_await2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> map = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .await()
                .get(new TypeReference<Map<String, Object>>() {
                });
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(map.get("status")).isEqualTo("1");
        assertThat(map.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_async_await3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map map = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .await(1, TimeUnit.SECONDS)
                .get(Map.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(map.get("status")).isEqualTo("1");
        assertThat(map.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_async_await4() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setBodyDelay(1200, TimeUnit.MILLISECONDS));
        Throwable err = null;
        try {
            Forest.get("/")
                    .port(server.getPort())
                    .async()
                    .addQuery("a", "1")
                    .executeAsFuture()
                    .await(800, TimeUnit.MILLISECONDS);
        } catch (Throwable th) {
            err = th;
        }
        assertThat(err).isNotNull();
    }

    @Test
    public void testRequest_async_await_list() {
        int count = 20;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        List<ForestFuture> futures = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            futures.add(Forest.get("/")
                    .port(server.getPort())
                    .async()
                    .addQuery("a", i)
                    .executeAsFuture());
        }
        Forest.await(futures, res -> {
            String result = res.get(String.class);
            assertThat(result).isNotNull().isEqualTo(EXPECTED);
        });
    }



    @Test
    public void testRequest_async_await_list2() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        List<ForestFuture> futures = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            futures.add(Forest.get("/")
                    .port(server.getPort())
                    .async()
                    .addQuery("a", i)
                    .executeAsFuture());
        }
        Forest.await(futures).forEach(res -> {
            String result = res.get(String.class);
            assertThat(result).isNotNull().isEqualTo(EXPECTED);
        });
    }

    @Test
    public void testRequest_async_await_array() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        server.enqueue(new MockResponse().setBody(EXPECTED));

        ForestFuture future1 = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", 0)
                .executeAsFuture();

        ForestFuture future2 = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", 1)
                .executeAsFuture();

        Forest.await(future1, future2).forEach(res -> {
            String result = res.get(String.class);
            assertThat(result).isNotNull().isEqualTo(EXPECTED);
        });
    }


    @Test
    public void testRequest_async_retryWhen_success() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        AtomicInteger atomicRetryCount = new AtomicInteger(0);
        ForestRequest<?> request = Forest.get("http://localhost")
                .port(server.getPort())
                .async()
                .maxRetryCount(3)
                .retryWhen(((req, res) -> res.statusIs(203)))
                .onRetry(((req, res) -> {
                    atomicRetryCount.incrementAndGet();
                }))
                .onSuccess(((data, req, res) -> {
                    isSuccess.set(true);
                    latch.countDown();
                }));
        request.execute();
        latch.await();
        assertThat(atomicRetryCount.get()).isEqualTo(3);
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
        assertThat(isSuccess.get()).isTrue();
    }

    @Test
    public void testRequest_sync_retryWhen_error_not_retry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost")
                .port(server.getPort())
                .maxRetryCount(3)
                .retryWhen(((req, res) -> res.statusIs(200)))
                .onError(((ex, req, res) -> isError.set(true)));
        request.execute();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(0);
    }


    @Test
    public void testRequest_sync_error_retry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("/")
                .host("localhost")
                .port(server.getPort())
                .maxRetryCount(3)
                .onError(((ex, req, res) -> isError.set(true)));
        request.execute();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
    }


    @Test
    public void testRequest_async_retryWhen_error_not_retry() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .retryWhen(((req, res) -> res.getStatusCode() == 200))
                .setOnError(((ex, req, res) -> {
                    isError.set(true);
                    latch.countDown();
                }));
        request.execute(InputStream.class);
        latch.await();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(0);
    }


    @Test
    public void testRequest_async_error_retry() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .maxRetryInterval(10L)
                .setOnError(((ex, req, res) -> {
                    isError.set(true);
                    latch.countDown();
                }));
        request.execute();
        latch.await();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
    }

    @Test
    public void testRequest_post_add_interceptor() {
        InterceptorChain interceptorChain = Forest.post("http://localhost:{}", server.getPort() + "/post")
                .addInterceptor(TestInterceptor.class)
                .getInterceptorChain();
        assertThat(interceptorChain).isNotNull();
        assertThat(interceptorChain.getInterceptorSize()).isEqualTo(2);
        assertFalse(interceptorChain.beforeExecute(null));
        assertTrue(inter3Before.get());
    }

    static final AtomicBoolean inter3Before = new AtomicBoolean(false);

    public static class TestInterceptor implements Interceptor {

        public TestInterceptor() {
        }

        @Override
        public boolean beforeExecute(ForestRequest request) {
            inter3Before.set(true);
            return false;
        }

        @Override
        public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        }

        @Override
        public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        }

        @Override
        public void afterExecute(ForestRequest request, ForestResponse response) {
        }
    }

    @Test
    public void testRequest_upload() {
        URL url = this.getClass().getResource("/test-img.jpg");
        byte[] byteArray = new byte[0];
        try {
            byteArray = IOUtils.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.put("http://localhost:{}", server.getPort() + "/")
                .contentTypeOctetStream()
                .addBody(byteArray)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(byteArray);
    }

    public Buffer getImageBuffer() {
        URL url = this.getClass().getResource("/test-img.jpg");
        byte[] byteArray = new byte[0];
        try {
            byteArray = IOUtils.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Buffer buffer = new Buffer();
        try {
            buffer.readFrom(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }


    @Test
    public void testDownloadFile() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        AtomicReference<ForestProgress> atomicProgress = new AtomicReference<>(null);
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .setDownloadFile(dir, "")
                .setOnProgress(progress -> {
                    System.out.println("------------------------------------------");
                    System.out.println("total bytes: " + progress.getTotalBytes());
                    System.out.println("current bytes: " + progress.getCurrentBytes());
                    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
                    if (progress.isDone()) {
                        atomicProgress.set(progress);
                    }
                });

        ForestResponse<File> response = request.execute(new TypeReference<ForestResponse<File>>() {
        });

        Assertions.assertThat(response)
                .isNotNull()
                .extracting(ForestResponse::getStatusCode)
                .isEqualTo(200);

        File file = response.getResult();

        Assertions.assertThat(file)
                .isNotNull()
                .isFile();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        byte[] fileBytes = IOUtils.toByteArray(new FileInputStream(file));
        Assertions.assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);
        Assertions.assertThat(atomicProgress.get())
                .isNotNull()
                .extracting(
                        ForestProgress::isDone,
                        ForestProgress::getRate,
                        ForestProgress::getRequest)
                .contains(true, 1D, response.getRequest());
    }

    @Test
    public void testProxyFromHTTPUrl() {
        final ForestRequest req = Forest.get("https://www.google.com")
                .proxy("http://root:123456@localhost:1082")
                .connectTimeout(20000);
        final ForestProxy proxy = req.getProxy();
        assertThat(proxy).isNotNull();
        assertThat(proxy.getType()).isEqualTo(ForestProxyType.HTTP);
        assertThat(proxy.getHost()).isEqualTo("localhost");
        assertThat(proxy.getPort()).isEqualTo(1082);
        assertThat(proxy.getUsername()).isEqualTo("root");
        assertThat(proxy.getPassword()).isEqualTo("123456");
    }



    @Test
    public void testIfThen() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));

        int a = 1;
        Forest.get("/test")
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(a > 0, q -> q.addHeader("A", a + 1))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "2");


        int b = 0;
        Forest.get("/test")
                .port(server.getPort())
                .host("localhost")
                .addHeader("B", -1)
                .ifThen(b > 0, q -> q.addHeader("B", b + 1))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("B", "-1");
    }


    @Test
    public void testIfThenElse() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));

        int a = -1;
        Forest.get("/test")
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(a > 0, q -> q.addHeader("A", a + 1))
                .elseThen(q -> q.addHeader("A", 10))
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "10");
    }


    @Test
    public void testElseIfThen() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        int a = -1;
        int b = 110;
        int c = 220;
        Forest.get("/test")
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .cond(b > 100, q -> q.addHeader("B", 100))
                .cond(c > 200, q -> q.addHeader("C", 200))
                .ifThen(a > 0, q -> q.addHeader("A", a + 1))
                .elseIfThen(a == 0, q -> q.addHeader("A", 0))
                .elseIfThen(a == -1, q -> q.addHeader("A", -1))
                .elseIfThen(a == -2, q -> q.addHeader("A", -2))
                .elseThen(q -> q.addHeader("A", 10))
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "-1")
                .assertHeaderEquals("B", "100")
                .assertHeaderEquals("C", "200");;
    }

    @Test
    public void testIfThenLambda() {{
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        int a = -1;
        Forest.get("/test")
                .addAttachment("a", -2)
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(a < 0, q -> q.addHeader("A", q.getAttachment("a", Integer.class)))
                .elseIfThen(a == -1, q -> q.addHeader("A", a))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "-2");
    }
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        int a = -1;
        Forest.get("/test")
                .addAttachment("a", a)
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(q -> q.getAttachment("a", Integer.class) < 0,
                        q -> q.addHeader("A", a))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "-1");
    }


/*
    @Test
    public void testSocksHttpProxy() {
        final String ret = Forest.get("http://www.google.com")
                .proxy(ForestProxy.socks("localhost", 1089))
                .connectTimeout(20000)
                .executeAsString();
        System.out.println(ret);
    }

    @Test
    public void testSocksHttpsProxy() {
        final String ret = Forest.get("https://www.google.com")
                .proxy(ForestProxy.socks("localhost", 1089)
                        .username("dtflys")
                        .password("123456"))
                .connectTimeout(20000)
                .executeAsString();
        System.out.println(ret);
    }
*/


}
