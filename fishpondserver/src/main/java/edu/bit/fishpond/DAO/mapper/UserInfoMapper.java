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

    @Select("select name from "
            + tableName
            + " where uid = ${uid}")
    public String selectName(@Param("uid") int uid);

    @Select("select state from "
            + tableName
            + " where uid = ${uid}")
    public boolean selectState(@Param("uid") int uid);

    @Select("select last_offline from "
            + tableName
            + " where uid = #{uid}")
    public String selectLast_offlineByUid(@Param("uid") int uid);

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public List<UserInfoDO> selectBatch(@Param("columnName") String columnName,
                                        @Param("columnValue") String columnValue);

    @Select("Select * from "
            + tableName
            + " where uid = ${uid}")
    public UserInfoDO selectOneById(@Param("uid") int uid);

    @Select("select * from "
            + tableName
    + " where name like '%${subName}%'")
    public List<UserInfoDO> selectBySubName(@Param("subName") String subName);


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
    public int updateOne(@Param("columnName") String columnName,
                         @Param("columnValue") String columnValue,
                         @Param("uid") int uid);

    @Update("update "
            + tableName
            + " set name = ${name} "
            + " , sex = #{sex} "
            + " , birthday = #{birthday} "
            + " , region = #{region} "
            + " where uid = #{uid}")
    public int updateInfo(@Param("uid") int uid,
                         @Param("name") String name,
                         @Param("sex") String sex,
                         @Param("birthday") String birthday,
                         @Param("region") String region);

    @Delete("delete from "
            + tableName
            + " where uid = #{uid}")
    public int deleteOne(@Param("uid") int uid);

    @Delete("delete from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public int deleteBatch(@Param("columnName") String columnName,
                           @Param("columnValue") String columnValue);

    @Update("truncate table "
            + tableName)
    public int deleteAll();

}
