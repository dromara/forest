package com.dtflys.forest.example;


import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.SolonMain;

@SolonMain
public class ForestExampleApp {
    public static void main(String[] args) {
        Solon.start(ForestExampleApp.class, args, app -> {
            //加个过滤器，输出异常；方便看问题
            app.filter((ctx, chain) -> {
                try {
                    chain.doFilter(ctx);
                } catch (Throwable e) {
                    ctx.status(500);
                    ctx.output(Utils.throwableToString(e));
                    e.printStackTrace();
                }
            });
        });
    }
}
