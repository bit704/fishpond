package edu.bit.fishpondops;

import edu.bit.fishpondops.service.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MockTest extends FishpondOpsApplicationTests{

    @Autowired
    MockService mockService;
    @Test
    public void test() {
        mockService.getAllUsers().forEach(userInfoDO -> System.out.println(userInfoDO));
    }
}
