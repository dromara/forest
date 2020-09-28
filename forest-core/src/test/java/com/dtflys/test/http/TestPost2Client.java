package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.Cause;
import com.dtflys.test.http.model.FormArrayParam;
import com.dtflys.test.http.model.FormListParam;
import com.dtflys.test.mock.Post2MockServer;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPost2Client extends BaseClientTest {

    @Rule
    public Post2MockServer server = new Post2MockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", Post2MockServer.port);
    }

    public TestPost2Client(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testPostFormList() {
        List<Integer> idList = Lists.newArrayList(1, 2, 3);
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        List<Cause> causes = Lists.newArrayList(cause1, cause2);
        String result = postClient.postFormList1("foo", "123456", idList, causes);
        assertEquals(Post2MockServer.EXPECTED, result);
    }


    @Test
    public void testPostFormList2() {
        FormListParam param = new FormListParam();
        List<Integer> idList = Lists.newArrayList(1, 2, 3);
        param.setUsername("foo");
        param.setPassword("123456");
        param.setIdList(idList);
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        List<Cause> causes = Lists.newArrayList(cause1, cause2);
        param.setCauses(causes);
        String result = postClient.postFormList2(param);
        assertEquals(Post2MockServer.EXPECTED, result);
    }

    @Test
    public void testPostFormArray1() {
        Integer[] idList = new Integer[] {1, 2, 3};
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        Cause[] causes = new Cause[] {cause1, cause2};
        String result = postClient.postFormArray1("foo", "123456", idList, causes);
        assertEquals(Post2MockServer.EXPECTED, result);
    }

    @Test
    public void testPostFormArray2() {
        FormArrayParam param = new FormArrayParam();
        List<Integer> idList = Lists.newArrayList(1, 2, 3);
        param.setUsername("foo");
        param.setPassword("123456");
        param.setIdList(new Integer[] {1, 2, 3});
        String result = postClient.postFormArray2(param);
        assertEquals(Post2MockServer.EXPECTED, result);
    }


}
