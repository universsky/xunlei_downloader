package test;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author junxian
 * @date 14-8-15
 */
public class CookieCompare {
    public static void main(String[] args) {
        String c1 = "ucid=; vip_paytype=; menu_isopen=0; check_result=0:!7A4; active=1; blogresult=0; downbyte=973867055395; downfile=4425; isspwd=0; isvip=6; jumpkey=351914D87F2A041DC7A53A1DC53591CF2F3CEA24B47ACCADCB9909327C3AF64E471846A9190F7F71B7619B16C7DAF0673D8598FBCBF7E6C138FC4DE58A88F78215796FF3AF4D4271AFAB02B2457D06B0000E6B080ED57DFF159E5301F5D42A03; logintype=1; lsessionid=6BA515B3CD57506642A2A219D602CFA6B092FAECE556EDC0141A2DE40BFA384671CDB2AAA5C447BD7AC5E2A81C888B21160110BCCC94A032B894ED3CF07C4713F3CB397C36DE92107209B1832E9A1D8B; luserid=37120570; nickname=%CD%F8%C4%E3%BB%D8%BC%D2; onlinetime=13177746; order=1920669; safe=0; score=31264; sessionid=AA833030695258EE47623FF39A575D0702AE587692BFBDDF8FEC7280CE3132F629B104C1F00F9D89C4B339ED2D63B4B9EFEA3C03314ECD6C5A01257A29670B5C; sex=m; upgrade=0; userid=37120570; usernewno=0; usernick=%E7%BD%91%E4%BD%A0%E5%9B%9E%E5%AE%B6; usertype=0; usrname=yewenjunjun; lx_login_u=yewenjunjun@qq.com; lx_login_auto=1; in_xl=0; vas_type=4; anterpriseac=0%230; lx_sessionid=AA833030695258EE47623FF39A575D0702AE587692BFBDDF8FEC7280CE3132F629B104C1F00F9D89C4B339ED2D63B4B9EFEA3C03314ECD6C5A01257A29670B5C; lx_login=37120570; last_userid=37120570; login_time=0.16130089759827; loadding_img=0; vip_isvip=1; vip_level=6; vip_paytype=5; user_type=1; vip_expiredate=2015-08-09; dl_enable=1; dl_size=3145738; dl_num=22; user_task_time=0.10303902626038; rw_list_open=1; VERIFY_KEY=; queryTime=1; bg_opentime=; dl_expire=365; gdriveid=C6D2168AEDC94E99FB86E0E5C7BDDDE3; task_nowclick=731832523491585; lx_nf_all=page_check_all%3Dcommtask%26class_check%3D0%26page_check%3Dcommtask%26fl_page_id%3D0%26class_check_new%3D0%26set_tab_status%3D4";
        String c2 = "check_result=0:!L4C; VERIFY_KEY=2A51A37304DEC32141D4E5E1B5B7B38C; usrname=yewenjunjun; usertype=0; usernick=%E7%BD%91%E4%BD%A0%E5%9B%9E%E5%AE%B6; usernewno=0; userid=37120570; upgrade=0; sex=m; sessionid=AA833030695258EE47623FF39A575D0702AE587692BFBDDF8FEC7280CE3132F629B104C1F00F9D89C4B339ED2D63B4B9EFEA3C03314ECD6C5A01257A29670B5C; score=31264; safe=0; order=1920669; onlinetime=13177746; nickname=%CD%F8%C4%E3%BB%D8%BC%D2; luserid=37120570; lsessionid=6BA515B3CD57506642A2A219D602CFA64337AD1B1A89004D13D521C9686245672DE12BED27C2C598137589BBB2CFDC194081119F99C65412BEE3B2A74D5BDB18F3CB397C36DE92107209B1832E9A1D8B; logintype=1; jumpkey=85B02A74B1F288A5C69343AEB56C8EAAD33BA13AD75C266DA76A5D3D92F41392D3FE57DDE3E2462AEBA3E4E8D0A11A37B3D31255BE97D622CFDFE16F7B5EBC3F509639A91FCC6C635C12453A144D354EDAFE15C652515062902919A343E70F10; isvip=6; isspwd=0; downfile=4425; downbyte=973867055395; blogresult=0; active=1; user_task_time=0.14697408676147; dl_expire=365; dl_num=22; dl_size=3145738; dl_enable=1; vip_expiredate=2015-08-09; user_type=1; vip_paytype=5; vip_level=6; vip_isvip=1; loadding_img=0; bg_opentime=1408112442.82; lx_nf_all=deleted; lx_sessionid=deleted; nvp37120570=1; login_time=0.60884284973145; last_userid=37120570; lx_login=37120570; anterpriseac=0%230; vas_type=4; in_xl=0";

        System.out.println(getCookieMap(c1));
        System.out.println(getCookieMap(c2));

        Map<String, String> m1 = getCookieMap(c1);
        Map<String, String> m2 = getCookieMap(c2);

        for (Map.Entry<String, String> entry : m1.entrySet()) {
            if (!m2.containsKey(entry.getKey())) {
                System.out.println(entry);
            }
        }
    }

    public static Map<String, String> getCookieMap(String s) {
        Map<String, String> result = new HashMap<String, String>();
        String[] splits = StringUtils.splitPreserveAllTokens(s, ";");
        for (String split : splits) {
            String[] cookieKV = StringUtils.splitPreserveAllTokens(split, "=");
            result.put(StringUtils.trim(cookieKV[0]), cookieKV[1]);
        }
        return result;
    }
}
