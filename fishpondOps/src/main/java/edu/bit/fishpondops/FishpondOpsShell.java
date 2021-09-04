package edu.bit.fishpondops;

import edu.bit.fishpondops.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class FishpondOpsShell {

    @Autowired
    QueryService queryService;

    @ShellMethod("展示用户")
    public String showUsers(@ShellOption(defaultValue = "false")boolean all, int num) {
        if(all) return queryService.getAllUsers().toString();
        else {
            return queryService.getUsers(num).toString();
        }
    }



}
