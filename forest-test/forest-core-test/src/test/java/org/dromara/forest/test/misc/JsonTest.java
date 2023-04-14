package org.dromara.forest.test.misc;

import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import junit.framework.TestCase;
import org.dromara.forest.converter.json.ForestGsonConverter;
import org.dromara.forest.converter.json.ForestJacksonConverter;
import org.dromara.forest.test.model.Contact;
import org.dromara.forest.test.model.Location;
import org.dromara.forest.test.model.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON单元测试
 * @author gongjun
 */
public class JsonTest extends TestCase {

    // TODO: 移到 forest-fastjson 去
//    private ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();

    private ForestJacksonConverter jacksonConverter = new ForestJacksonConverter();
    private ForestGsonConverter gsonConverter = new ForestGsonConverter();

    private static Map<String, Object> map = new HashMap<String, Object>();
    private static List list = new ArrayList();

    private static List<Contact> contacts = new ArrayList<Contact>();
    private static Result<List<Contact>> result = new Result<List<Contact>>();

    static {
        map.put("name", "Peter");
        map.put("age", 20);
        map.put("phone", "123456789");

        list.add("abc");
        list.add("123");
        list.add(100);
        map.put("list", list);

        Contact c1 = new Contact();
        c1.setName("Peter");
        c1.setAge(20);
        c1.setPhone("123456789");

        Contact c2 = new Contact();
        c2.setName("Marry");
        c2.setAge(18);
        c2.setPhone("987654321");

        contacts.add(c1);
        contacts.add(c2);

        result.setStatus(1);
        result.setData(contacts);
    }


/**
 * TODO: 移到 forest-fastjson 去
 *
    public void testFastjson() {
        String jsonSource = fastjsonConverter.encodeToString(map);
        assertNotNull(jsonSource);
        Map newMap = fastjsonConverter.convertToJavaObject(jsonSource, Map.class);
        assertEquals(map, newMap);

        String jsonSource2 = fastjsonConverter.encodeToString(contacts);
        assertNotNull(jsonSource2);
        List<Contact> newList = fastjsonConverter.convertToJavaObject(jsonSource2, new TypeReference<List<Contact>>() {});
        assertNotNull(newList);
        assertEquals(newList.get(0).getAge(), new Integer(20));

        String jsonSource3 = fastjsonConverter.encodeToString(result);
        assertNotNull(jsonSource3);
        Result<List<Contact>> newResult = fastjsonConverter.convertToJavaObject(jsonSource3, new TypeReference<Result<List<Contact>>>() {});
        assertNotNull(newResult);
        assertEquals(newResult.getStatus(), new Integer(1));

        String locationText = "{\"status\":\"1\",\"data\":{\"province\":\"江苏省\",\"cross_list\":[{\"distance\":\"191.482\",\"direction\":\"West\",\"name\":\"联谊路--绿溪路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0512567\",\"crossid\":\"021H51F0090093015--021H51F009009851\",\"width\":\"8, 8\",\"latitude\":\"31.31579861\"},{\"distance\":\"233.802\",\"direction\":\"NorthEast\",\"name\":\"陆家浜南路--陆家浜北路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0476761\",\"crossid\":\"021H51F00900930--021H51F0090093005\",\"width\":\"8, 8\",\"latitude\":\"31.31397833\"},{\"distance\":\"233.802\",\"direction\":\"NorthEast\",\"name\":\"陆家浜南路--绿溪路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0476761\",\"crossid\":\"021H51F00900930--021H51F009009851\",\"width\":\"8, 8\",\"latitude\":\"31.31397833\"}],\"code\":\"1\",\"tel\":\"0512\",\"cityadcode\":\"320500\",\"areacode\":\"0512\",\"timestamp\":\"1466430882.37\",\"pos\":\"在昆山市陆家镇人民政府附近, 在绿溪路旁边, 靠近联谊路--绿溪路路口\",\"road_list\":[{\"distance\":\"79\",\"direction\":\"North\",\"name\":\"绿溪路\",\"level\":\"5\",\"longitude\":\"121.05\",\"width\":\"8\",\"roadid\":\"021H51F009009851\",\"latitude\":\"31.3149\"},{\"distance\":\"152\",\"direction\":\"SouthEast\",\"name\":\"教堂路\",\"level\":\"5\",\"longitude\":\"121.048\",\"width\":\"4\",\"roadid\":\"021H51F0090092871\",\"latitude\":\"31.3163\"},{\"distance\":\"191\",\"direction\":\"West\",\"name\":\"联谊路\",\"level\":\"5\",\"longitude\":\"121.051\",\"width\":\"8\",\"roadid\":\"021H51F0090093015\",\"latitude\":\"31.3158\"}],\"result\":\"true\",\"message\":\"Successful.\",\"desc\":\"江苏省,苏州市,昆山市\",\"city\":\"苏州市\",\"districtadcode\":\"320583\",\"district\":\"昆山市\",\"country\":\"中国\",\"provinceadcode\":\"320000\",\"version\":\"2.0-3.0.6168.2019\",\"adcode\":\"320583\",\"poi_list\":[{\"distance\":\"161\",\"direction\":\"West\",\"tel\":\"0512-57877735;0512-57671209;0512-57879719\",\"name\":\"昆山市陆家人民医院\",\"weight\":\"0.0\",\"typecode\":\"090100\",\"longitude\":\"121.047556\",\"address\":\"陆家镇镇北路21号\",\"latitude\":\"31.315543\",\"type\":\"医疗保健服务;综合医院;综合医院\",\"poiid\":\"B020016BMR\"},{\"distance\":\"146\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"中共陆家镇委政法委\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048380\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314508\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B0FFF9XX2W\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"0512-57671003\",\"name\":\"昆山市陆家镇人民政府\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020007YBU\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"陆家镇人民代表大会\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020007YBT\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"中共昆山市陆家镇委员会\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020008HW1\"}]}}";
        Result<Location> locationResult = fastjsonConverter.convertToJavaObject(locationText, new TypeReference<Result<Location>>() {});
        assertNotNull(locationResult);

    }
*/

    public void testJackson() {
        String jsonSource = jacksonConverter.encodeToString(map);
        assertNotNull(jsonSource);
        Map newMap = jacksonConverter.convertToJavaObject(jsonSource, Map.class);
        assertEquals(map, newMap);

        String jsonSource2 = jacksonConverter.encodeToString(contacts);
        assertNotNull(jsonSource2);
        List<Contact> newList = jacksonConverter.convertToJavaObject(jsonSource2, List.class, Contact.class);
        assertNotNull(newList);
        assertEquals(newList.get(0).getAge(), new Integer(20));


        String jsonSource3 = jacksonConverter.encodeToString(result);
        assertNotNull(jsonSource3);

        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructType(Result.class, mapper.getTypeFactory().constructType(List.class, Contact.class));
        Result<List<Contact>> newResult = jacksonConverter.convertToJavaObject(jsonSource3, javaType);
        assertNotNull(newResult);
        assertEquals(newResult.getStatus(), new Integer(1));

        String locationText = "{\"status\":\"1\",\"data\":{\"province\":\"江苏省\",\"cross_list\":[{\"distance\":\"191.482\",\"direction\":\"West\",\"name\":\"联谊路--绿溪路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0512567\",\"crossid\":\"021H51F0090093015--021H51F009009851\",\"width\":\"8, 8\",\"latitude\":\"31.31579861\"},{\"distance\":\"233.802\",\"direction\":\"NorthEast\",\"name\":\"陆家浜南路--陆家浜北路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0476761\",\"crossid\":\"021H51F00900930--021H51F0090093005\",\"width\":\"8, 8\",\"latitude\":\"31.31397833\"},{\"distance\":\"233.802\",\"direction\":\"NorthEast\",\"name\":\"陆家浜南路--绿溪路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0476761\",\"crossid\":\"021H51F00900930--021H51F009009851\",\"width\":\"8, 8\",\"latitude\":\"31.31397833\"}],\"code\":\"1\",\"tel\":\"0512\",\"cityadcode\":\"320500\",\"areacode\":\"0512\",\"timestamp\":\"1466430882.37\",\"pos\":\"在昆山市陆家镇人民政府附近, 在绿溪路旁边, 靠近联谊路--绿溪路路口\",\"road_list\":[{\"distance\":\"79\",\"direction\":\"North\",\"name\":\"绿溪路\",\"level\":\"5\",\"longitude\":\"121.05\",\"width\":\"8\",\"roadid\":\"021H51F009009851\",\"latitude\":\"31.3149\"},{\"distance\":\"152\",\"direction\":\"SouthEast\",\"name\":\"教堂路\",\"level\":\"5\",\"longitude\":\"121.048\",\"width\":\"4\",\"roadid\":\"021H51F0090092871\",\"latitude\":\"31.3163\"},{\"distance\":\"191\",\"direction\":\"West\",\"name\":\"联谊路\",\"level\":\"5\",\"longitude\":\"121.051\",\"width\":\"8\",\"roadid\":\"021H51F0090093015\",\"latitude\":\"31.3158\"}],\"result\":\"true\",\"message\":\"Successful.\",\"desc\":\"江苏省,苏州市,昆山市\",\"city\":\"苏州市\",\"districtadcode\":\"320583\",\"district\":\"昆山市\",\"country\":\"中国\",\"provinceadcode\":\"320000\",\"version\":\"2.0-3.0.6168.2019\",\"adcode\":\"320583\",\"poi_list\":[{\"distance\":\"161\",\"direction\":\"West\",\"tel\":\"0512-57877735;0512-57671209;0512-57879719\",\"name\":\"昆山市陆家人民医院\",\"weight\":\"0.0\",\"typecode\":\"090100\",\"longitude\":\"121.047556\",\"address\":\"陆家镇镇北路21号\",\"latitude\":\"31.315543\",\"type\":\"医疗保健服务;综合医院;综合医院\",\"poiid\":\"B020016BMR\"},{\"distance\":\"146\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"中共陆家镇委政法委\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048380\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314508\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B0FFF9XX2W\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"0512-57671003\",\"name\":\"昆山市陆家镇人民政府\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020007YBU\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"陆家镇人民代表大会\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020007YBT\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"中共昆山市陆家镇委员会\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020008HW1\"}]}}";
        Result<Location> locationResult = jacksonConverter.convertToJavaObject(locationText, new TypeReference<Result<Location>>() {}.getType());
        assertNotNull(locationResult);

    }

    public void testGson() {
        String jsonSource = gsonConverter.encodeToString(map);
        assertNotNull(jsonSource);
        Map newMap = gsonConverter.convertToJavaObject(jsonSource, Map.class);
        assertEquals(map, newMap);

        String jsonSource2 = gsonConverter.encodeToString(contacts);
        assertNotNull(jsonSource2);
        List<Contact> newList = gsonConverter.convertToJavaObject(jsonSource2, new TypeToken<List<Contact>>() {}.getType());
        assertNotNull(newList);
        assertEquals(newList.get(0).getAge(), new Integer(20));

        Type type = new TypeToken<Result<List<Contact>>>() {}.getType();
        String jsonSource3 = gsonConverter.convertToJson(result, type);
        assertNotNull(jsonSource3);
        Result<List<Contact>> newResult = gsonConverter.convertToJavaObject(jsonSource3, type);
        assertNotNull(newResult);
        assertEquals(newResult.getStatus(), new Integer(1));

        String locationText = "{\"status\":\"1\",\"data\":{\"province\":\"江苏省\",\"cross_list\":[{\"distance\":\"191.482\",\"direction\":\"West\",\"name\":\"联谊路--绿溪路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0512567\",\"crossid\":\"021H51F0090093015--021H51F009009851\",\"width\":\"8, 8\",\"latitude\":\"31.31579861\"},{\"distance\":\"233.802\",\"direction\":\"NorthEast\",\"name\":\"陆家浜南路--陆家浜北路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0476761\",\"crossid\":\"021H51F00900930--021H51F0090093005\",\"width\":\"8, 8\",\"latitude\":\"31.31397833\"},{\"distance\":\"233.802\",\"direction\":\"NorthEast\",\"name\":\"陆家浜南路--绿溪路\",\"weight\":\"120\",\"level\":\"45000, 45000\",\"longitude\":\"121.0476761\",\"crossid\":\"021H51F00900930--021H51F009009851\",\"width\":\"8, 8\",\"latitude\":\"31.31397833\"}],\"code\":\"1\",\"tel\":\"0512\",\"cityadcode\":\"320500\",\"areacode\":\"0512\",\"timestamp\":\"1466430882.37\",\"pos\":\"在昆山市陆家镇人民政府附近, 在绿溪路旁边, 靠近联谊路--绿溪路路口\",\"road_list\":[{\"distance\":\"79\",\"direction\":\"North\",\"name\":\"绿溪路\",\"level\":\"5\",\"longitude\":\"121.05\",\"width\":\"8\",\"roadid\":\"021H51F009009851\",\"latitude\":\"31.3149\"},{\"distance\":\"152\",\"direction\":\"SouthEast\",\"name\":\"教堂路\",\"level\":\"5\",\"longitude\":\"121.048\",\"width\":\"4\",\"roadid\":\"021H51F0090092871\",\"latitude\":\"31.3163\"},{\"distance\":\"191\",\"direction\":\"West\",\"name\":\"联谊路\",\"level\":\"5\",\"longitude\":\"121.051\",\"width\":\"8\",\"roadid\":\"021H51F0090093015\",\"latitude\":\"31.3158\"}],\"result\":\"true\",\"message\":\"Successful.\",\"desc\":\"江苏省,苏州市,昆山市\",\"city\":\"苏州市\",\"districtadcode\":\"320583\",\"district\":\"昆山市\",\"country\":\"中国\",\"provinceadcode\":\"320000\",\"version\":\"2.0-3.0.6168.2019\",\"adcode\":\"320583\",\"poi_list\":[{\"distance\":\"161\",\"direction\":\"West\",\"tel\":\"0512-57877735;0512-57671209;0512-57879719\",\"name\":\"昆山市陆家人民医院\",\"weight\":\"0.0\",\"typecode\":\"090100\",\"longitude\":\"121.047556\",\"address\":\"陆家镇镇北路21号\",\"latitude\":\"31.315543\",\"type\":\"医疗保健服务;综合医院;综合医院\",\"poiid\":\"B020016BMR\"},{\"distance\":\"146\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"中共陆家镇委政法委\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048380\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314508\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B0FFF9XX2W\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"0512-57671003\",\"name\":\"昆山市陆家镇人民政府\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020007YBU\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"陆家镇人民代表大会\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020007YBT\"},{\"distance\":\"141\",\"direction\":\"SouthWest\",\"tel\":\"\",\"name\":\"中共昆山市陆家镇委员会\",\"weight\":\"0.0\",\"typecode\":\"130105\",\"longitude\":\"121.048398\",\"address\":\"菉溪路22号\",\"latitude\":\"31.314555\",\"type\":\"政府机构及社会团体;政府机关;乡镇级政府及事业单位\",\"poiid\":\"B020008HW1\"}]}}";
        Result<Location> locationResult = gsonConverter.convertToJavaObject(locationText, new TypeReference<Result<Location>>() {}.getType());
        assertNotNull(locationResult);

    }

}
