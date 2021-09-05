package edu.bit.fishpondops;

import edu.bit.fishpondops.service.MockService;
import edu.bit.fishpondops.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QMTest extends FishpondOpsApplicationTests{

    @Autowired
    MockService mockService;

    @Autowired
    QueryService queryService;

    @Test
    public void test() {
    }
}
