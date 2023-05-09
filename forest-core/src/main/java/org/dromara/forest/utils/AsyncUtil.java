package org.dromara.forest.utils;

import java.util.function.Consumer;

/**
 * 异步工具类
 *
 * @author CHMing
 * @since 1.5.23
 **/
public class AsyncUtil {

    private static final Consumer<Runnable> ASYNC_EXECUTE;

    private static Boolean isEnableCache;

    static {
        if (isEnableCoroutine()) {
            ASYNC_EXECUTE = command -> CoroutineUtil.INSTANCE.launch((coroutineScope, continuation) -> {
                command.run();
                return true;
            });
        } else {
            ASYNC_EXECUTE = command -> new Thread(command).start();
        }
    }

    /**
     * 是否启用协程
     *
     * @return boolean
     */
    public static boolean isEnableCoroutine() {
        if (isEnableCache != null) {
            return isEnableCache;
        }
        try {
            Class.forName("kotlinx.coroutines.CoroutineScope");
            Class.forName("kotlinx.coroutines.Dispatchers");
            Class.forName("kotlinx.coroutines.channels.Channel");
            isEnableCache = true;
        } catch (ClassNotFoundException e) {
            isEnableCache = false;
        }
        return isEnableCache;
    }

    /**
     * 执行异步命令
     *
     * @param command 异步执行的回调过程
     */
    public static void execute(Runnable command) {
        ASYNC_EXECUTE.accept(command);
    }
}
