package org.forest.interceptor;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-06-26
 */
public class ForestInterceptorMapping {

    private final String path;

    private final List<String> typeList = new ArrayList<String>();

    private final Map<String, String> headers = new HashMap<String, String>();

    private final Map<String, Object> data = new HashMap<String, Object>();

    private final List<Class<? extends ForestInterceptor>> interceptorClassList = new ArrayList<Class<? extends ForestInterceptor>>();

    public ForestInterceptorMapping(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void addInterceptorClass(Class<? extends ForestInterceptor> interceptorClass) {
        this.interceptorClassList.add(interceptorClass);
    }

    public boolean isMatch(String urlPath) {
        char c;
        for (int i = 0; i < urlPath.length(); i++) {
            c = urlPath.charAt(i);

        }
        String regex = path.replaceAll("\\*", ".");
        boolean flag = Pattern.matches(regex, urlPath);

        return flag;
    }

}
