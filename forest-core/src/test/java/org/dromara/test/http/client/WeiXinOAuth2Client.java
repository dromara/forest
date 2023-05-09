package org.dromara.test.http.client;

import org.dromara.forest.annotation.GetRequest;
import org.dromara.forest.extensions.OAuth2;

import java.util.Map;

/**
 * 微信公众号开发 OAuth2 测试
 *
 * @author HouKunLin
 * @since 1.5.0-BETA9
 */
@OAuth2(
        tokenUri = "https://api.weixin.qq.com/cgi-bin/token",
        clientId = "x",
        clientSecret = "yyy",
        grantType = OAuth2.GrantType.CLIENT_CREDENTIALS,
        grantTypeValue = "client_credential",
        body = {
                // 请在这里填写 微信公众号 AppID 和 Secret
                "appid: wx5e6e468c3afc28ed",
                "secret: 57bf25691d7bbe364c54a321e8cb75df"
        },
        tokenAt = OAuth2.TokenAt.URL
)
public interface WeiXinOAuth2Client {
    /**
     * 自定义菜单：<a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Querying_Custom_Menus.html">查询菜单</a>
     *
     * @return 菜单信息
     */
    @GetRequest(url = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info")
    Map<String, Object> listMenu();

    /**
     * 自定义菜单：<a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Getting_Custom_Menu_Configurations.html">获取自定义菜单配置</a>
     *
     * @return 自定义菜单配置
     */
    @GetRequest(url = "https://api.weixin.qq.com/cgi-bin/menu/get")
    Map<String, Object> listCustomMenu();

    /**
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Getting_Rules_for_Auto_Replies.html">获取公众号的自动回复规则</a>
     *
     * @return 获取公众号的自动回复规则
     */
    @GetRequest(url = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info")
    Map<String, Object> getCurrentAutoreplyInfo();
}
