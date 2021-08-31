package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.UserInfoDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserInfoMapper {

    public static final String tableName = "fishpond.userinfo";

    public static final String sql_insertColumns =
            "(name, sex, birthday, region, state, create_time, last_offline, real)";

    @Select("select * from "
            + tableName)
    public List<UserInfoDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where uid = #{uid}")
    public UserInfoDO selectOne(@Param("uid") int uid);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{name}, #{sex}, #{birthday}, #{region}, #{state}, #{create_time}, #{last_offline}, #{real}) ")
    public int insertOne(@Param("name") String name,
                             @Param("sex") String sex,
                             @Param("birthday") String birthday,
                             @Param("region") String region,
                             @Param("state") Boolean state,
                             @Param("create_time") String create_time,
                             @Param("last_offline") String last_offline,
                             @Param("real") Boolean real);

    @Update("update "
            + tableName
            + " set #{columnName} = #{columnValue} where uid = #{uid}")
    public boolean updateOne(@Param("columnName") String columnName,
                             @Param("columnValue") String columnValue,
                             @Param("uid") int uid);

    @Delete("delete from "
            + tableName
            + " where uid = #{uid}")
    public int deleteOne(@Param("uid") int uid);

    @Update("truncate table "
            + tableName)
    public int deleteAll();

    @Update("select setval('fishpond.userinfo_uid_seq',10000000,true)")
    public int resetSeq();
}
