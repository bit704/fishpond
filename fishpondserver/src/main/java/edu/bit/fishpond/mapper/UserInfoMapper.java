package edu.bit.fishpond.mapper;

import edu.bit.fishpond.DO.UserInfoDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserInfoMapper {

    public static final String sql_insertColumns =
            "(name, sex, birthday, region, state, create_time, last_offline, real)";

    @Select("select * from userinfo")
    public List<UserInfoDO> selectAll();

    @Select("Select * from userinfo where uid = #{uid}")
    public UserInfoDO selectOne(@Param("uid") int uid);

    @Insert("insert into userinfo "
            + sql_insertColumns
            + "values (#{name}, #{sex}, #{birthday}, #{region}, #{state}, #{create_time}, #{last_offline}, #{real}) " )
    public  boolean insertOne(@Param("name") String name,
                              @Param("sex") String sex,
                              @Param("birthday") String birthday,
                              @Param("region") String region,
                              @Param("state") Boolean state,
                              @Param("create_time") String create_time,
                              @Param("last_offline") String last_offline,
                              @Param("real") Boolean real);

    @Update("update userinfo set #{columnName} = #{columnValue} where uid = #{uid}")
    public boolean updateOne(@Param("columnName") String columnName,
                             @Param("columnValue") String columnValue,
                             @Param("uid") int uid);

    @Delete("delete from userinfo where uid = #{uid}")
    public boolean deleteOne(@Param("uid") int uid);

    @Delete("truncate table userinfo")
    public boolean deleteAll();
}
