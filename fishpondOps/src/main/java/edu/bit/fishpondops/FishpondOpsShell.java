package edu.bit.fishpondops;

import edu.bit.fishpondops.service.ClientService;
import edu.bit.fishpondops.service.DBService;
import edu.bit.fishpondops.service.MockService;
import edu.bit.fishpondops.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;


@ShellComponent
public class FishpondOpsShell {

    @Autowired
    QueryService queryService;

    @Autowired
    MockService mockService;

    @Autowired
    ClientService clientService;

    @ShellMethod("展示用户详情")
    public String showUser(@ShellOption(value = {"-all"}, defaultValue = "false", help = "是否展示全部用户") boolean all,
                           @ShellOption(value = {"-N", "-num"}, defaultValue = "0", help = "展示用户数量") int num,
                           @ShellOption(value = {"-u"}, defaultValue = "-1", help = "指定用户ID") int uid) {
        if (all) return queryService.getAllUsers();
        else if (uid == -1) {
            return queryService.getUsers(num);
        } else {
            return queryService.getSpecifiedUser(uid);

        }
    }

    @ShellMethod("展示群详情")
    public String showGroup(@ShellOption(value = {"-all"}, defaultValue = "false", help = "是否展示全部群") boolean all,
                            @ShellOption(value = {"-N", "-num"}, defaultValue = "0", help = "展示群数量") int num,
                            @ShellOption(value = {"-u"}, defaultValue = "-1", help = "指定群ID") int gid) {
        if(all) return queryService.getAllGroups();
        else if(gid == -1) {
            return queryService.getGroups(num);
        } else {
            return queryService.getSpecifiedGroup(gid);
        }
    }

    @ShellMethod("清空数据库")
    public void clearDb() {
        queryService.clearDb();
    }


    @ShellMethod("查看数据库情况")
    public String checkDb() {
        return queryService.getDb();
    }


    @ShellMethod("查看系统负载")
    public String checkLoad() {
        queryService.getLoad();
        return "执行完毕";
    }

    @ShellMethod("执行服务器压力测试")
    public String pressureTest(@ShellOption(value = {"-N", "-num"}, help = "创建虚拟客户端数量") int num) {
        clientService.reset();
        clientService.initFakeClient(num);
        clientService.activeClient();
        clientService.reset();
        return  "执行完毕";
    }

    @ShellMethod("执行数据库性能测试")
    public String dbTest(@ShellOption(value = {"-N", "-num"}, help = "最大连接数量") int num) {
        DBService.testDBConnect(num);
        return "执行完毕";
    }


}
