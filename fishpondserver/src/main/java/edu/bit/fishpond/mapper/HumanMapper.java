package edu.bit.fishpond.mapper;

import edu.bit.fishpond.DO.HumanDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface HumanMapper {

    @Select("select * from human where id = #{id}")
    public HumanDO selectById(@Param("id") int id);
}
