package com.colaclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author colaclub
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ColaClubApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ColaClubApplication.class, args);
        System.out.println(
                "┴┬┴┬／￣＼＿／￣＼\n" +
                        "┬┴┬┴▏　　▏▔▔▔▔＼\n" +
                        "┴┬┴／＼　／　　　　　　﹨\n" +
                        "┬┴∕　　　　　　　／　　　）\n" +
                        "┴┬▏　　　　　　　　●　　▏\n" +
                        "┬┴▏　　　　　　　　　　　▔█◤ " + "(♥◠‿◠)ﾉﾞ  ColaClub启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                        "┴◢██◣　　　　　　 ＼＿＿／\n" +
                        "┬█████◣　　　　　　　／　　　　\n" +
                        "┴█████████████◣\n" +
                        "◢██████████████▆▄\n" +
                        "◢██████████████▆▄\n" +
                        "█◤◢██◣◥█████████◤\n" +
                        "————————————————\n");
    }
}
