/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dtflys.forest.exceptions;

import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.StringUtils;

/**
 * @author gongjun
 * @since 2016-05-30
 */
public class ForestNetworkException extends ForestRuntimeException {

    private Integer statusCode;

    private ForestResponse response;

    public ForestNetworkException(String message, Integer statusCode, ForestResponse response) {
        super(errorMessage(message, statusCode, response));
        this.statusCode = statusCode;
        this.response = response;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public ForestResponse getResponse() {
        return response;
    }

    private static String errorMessage(String message, Integer statusCode, ForestResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP ").append(statusCode).append(" Error");
        if (StringUtils.isNotEmpty(message)) {
            builder.append(": ").append(message);
        }
        if (response != null) {
            String content = response.getContent();
            if (content != null) {
                if (StringUtils.isNotEmpty(message)) {
                    builder.append("; ");
                }
                builder.append("Content=").append(content);
            }
        }
        return builder.toString();
    }
}
