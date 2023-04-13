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

package org.dromara.forest.http;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:35
 */
public class HttpStatus {

    public static final int CONTINUE = 100;

    public static final int SWITCHING_PROTOCOLS = 101;

    public static final int PROCESSING = 102;

    // --- 2xx Success ---

    public static final int OK = 200;

    public static final int CREATED = 201;

    public static final int ACCEPTED = 202;

    public static final int NON_AUTHORITATIVE_INFORMATION = 203;

    public static final int NO_CONTENT = 204;

    public static final int RESET_CONTENT = 205;

    public static final int PARTIAL_CONTENT = 206;

    public static final int MULTI_STATUS = 207;

    // --- 3xx Redirection ---

    public static final int MULTIPLE_CHOICES = 300;

    public static final int MOVED_PERMANENTLY = 301;

    public static final int MOVED_TEMPORARILY = 302;

    public static final int SEE_OTHER = 303;

    public static final int NOT_MODIFIED = 304;

    public static final int USE_PROXY = 305;

    public static final int TEMPORARY_REDIRECT = 307;

    // --- 4xx Client Error ---

    public static final int BAD_REQUEST = 400;

    public static final int UNAUTHORIZED = 401;

    public static final int PAYMENT_REQUIRED = 402;

    public static final int FORBIDDEN = 403;

    public static final int NOT_FOUND = 404;

    public static final int METHOD_NOT_ALLOWED = 405;

    public static final int NOT_ACCEPTABLE = 406;

    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;

    public static final int REQUEST_TIMEOUT = 408;

    public static final int CONFLICT = 409;

    public static final int GONE = 410;

    public static final int LENGTH_REQUIRED = 411;

    public static final int PRECONDITION_FAILED = 412;

    public static final int REQUEST_TOO_LONG = 413;

    public static final int REQUEST_URI_TOO_LONG = 414;

    public static final int UNSUPPORTED_MEDIA_TYPE = 415;

    public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    public static final int EXPECTATION_FAILED = 417;


    public static final int INSUFFICIENT_SPACE_ON_RESOURCE = 419;

    public static final int METHOD_FAILURE = 420;

    public static final int UNPROCESSABLE_ENTITY = 422;

    public static final int LOCKED = 423;

    public static final int FAILED_DEPENDENCY = 424;

    // --- 5xx Server Error ---

    public static final int INTERNAL_SERVER_ERROR = 500;

    public static final int NOT_IMPLEMENTED = 501;

    public static final int BAD_GATEWAY = 502;

    public static final int SERVICE_UNAVAILABLE = 503;

    public static final int GATEWAY_TIMEOUT = 504;

    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    public static final int INSUFFICIENT_STORAGE = 507;

}
