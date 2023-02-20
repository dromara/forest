package com.dtflys.forest.http.body;

import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.utils.ForestDataType;

import java.io.InputStream;

/**
 * 二进制请求体类型
 *
 * @author gongjun
 * @since 1.5.29
 */
public abstract class BinaryRequestBody extends ForestRequestBody {

    abstract InputStream getInputStream();

}
