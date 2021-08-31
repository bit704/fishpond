package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.HumanDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用来测试的类，无实际意义
 */
@Component
public interface HumanMapper {

    @Select("select * from human where id = #{id}")
    public HumanDO selectById(@Param("id") int id);

    @Select("select * from human")
    public List<HumanDO> selectAll();
}
