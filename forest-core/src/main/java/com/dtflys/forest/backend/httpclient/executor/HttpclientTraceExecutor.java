package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.entity.HttpTraceWithBodyEntity;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.client.methods.HttpTrace;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:45
 */
public class HttpclientTraceExecutor extends AbstractHttpclientExecutor<HttpTraceWithBodyEntity> {

    @Override
    protected HttpclientRequestProvider<HttpTraceWithBodyEntity> getRequestProvider() {
        return url -> new HttpTraceWithBodyEntity(url);
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }

    public HttpclientTraceExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }

    public static int search(int[] nums, int target, int start, int end) {
        if (start > end) {
            return -1;
        }
        int mid = start + (end - start) / 2;
        int val = nums[mid];
        if (val == target) {
            return mid;
        }
        if (target < val) {
            return search(nums, target, start, mid - 1);
        }
        return search(nums, target, mid + 1, end);
    }


    public static int search(int[] nums, int target) {
        return search(nums, target, 0, nums.length - 1);
    }

    public static void main(String[] args) {
        int[] nums = new int[] {-1,0,3,5,9,12};
        System.out.println(search(nums, 9));
        System.out.println(search(nums, 2));
        System.out.println(search(nums, 11));
        System.out.println(search(nums, 13));

        nums = new int[] {5};
        System.out.println(search(nums, 5));
    }
}
