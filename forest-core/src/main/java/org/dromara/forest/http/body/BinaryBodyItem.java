package org.dromara.forest.http.body;

import org.dromara.forest.http.ForestBodyItem;

import java.io.InputStream;

/**
 * 二进制请求体类型
 *
 * @author gongjun
 * @since 1.5.29
 */
public abstract class BinaryBodyItem extends ForestBodyItem {

    abstract InputStream getInputStream();

}
