package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.UserInfoDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserInfoMapper {

    public static final String tableName = "fishpond.userinfo";

    public static final String sql_insertColumns =
            "(uid, name, sex, birthday, region, state, create_time, last_offline, real)";

    @Select("select * from  "
            + tableName)
    public List<UserInfoDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public UserInfoDO selectBatch(@Param("columnName") String columnName,
                                  @Param("columnValue") String columnValue);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{uid}, #{name}, #{sex}, #{birthday}, #{region}, #{state}, #{create_time}, #{last_offline}, #{real}) ")
    public int insertOne(@Param("uid") int uid,
                         @Param("name") String name,
                         @Param("sex") String sex,
                         @Param("birthday") String birthday,
                         @Param("region") String region,
                         @Param("state") Boolean state,
                         @Param("create_time") String create_time,
                         @Param("last_offline") String last_offline,
                         @Param("real") Boolean real);

    @Update("update "
            + tableName
            + " set ${columnName} = ${columnValue} where uid = #{uid}")
    public boolean updateOne(@Param("columnName") String columnName,
                             @Param("columnValue") String columnValue,
                             @Param("uid") int uid);

    @Delete("delete from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public int deleteBatch(@Param("columnName") String columnName,
                           @Param("columnValue") String columnValue);

    @Update("truncate table "
            + tableName)
    public int deleteAll();

}
