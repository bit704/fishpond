package edu.bit.fishpond;

import edu.bit.fishpond.mapper.HumanMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DataTest extends FishpondApplicationTests{

    @Autowired
    HumanMapper humanMapper;

    @Test
    public void testMapper() {
        System.out.println(humanMapper.selectAll().toString());
    }
}
