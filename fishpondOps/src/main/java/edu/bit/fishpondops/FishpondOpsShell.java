package edu.bit.fishpondops;

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

    @ShellMethod("查看系统负载")
    public String checkLoad() {
        return queryService.getLoad();
    }

    @ShellMethod("执行压力测试")
    public String pressureTest(int num) {
        return  null;
    }


}
