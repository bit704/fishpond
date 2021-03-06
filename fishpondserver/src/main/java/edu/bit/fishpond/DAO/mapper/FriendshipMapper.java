package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.FriendshipDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FriendshipMapper {

    public static final String tableName = "fishpond.friendship";

    public static final String sql_insertColumns =
            "(uid1, uid2, friend_time)";

    @Select("select * from  "
            + tableName)
    public List<FriendshipDO> selectAll();

    @Select("select * from "
            + tableName
            + " where uid1 = #{uid} or uid2 = #{uid}")
    public List<FriendshipDO> selectById(@Param("uid") int uid);

    @Select("select count(*) from "
            + tableName
            + " where  (uid1 = #{uid1} and uid2 = #{uid2}) or (uid1 = #{uid2} and uid2 = #{uid1})")
    public int selectByFriendship(@Param("uid1") int uid1,
                                  @Param("uid2") int uid2);

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public FriendshipDO selectBatch(@Param("columnName") String columnName,
                                    @Param("columnValue") String columnValue);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{uid1}, #{uid2}, #{friend_time}) ")
    public int insertOne(@Param("uid1") int uid1,
                         @Param("uid2") int uid2,
                         @Param("friend_time") String friend_time);

    @Delete("delete from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public int deleteBatch(@Param("columnName") String columnName,
                           @Param("columnValue") String columnValue);

    @Delete("delete from "
            + tableName
            + " where  (uid1 = #{uid1} and uid2 = #{uid2}) or (uid1 = #{uid2} and uid2 = #{uid1})")
    public int deleteByFriendship(@Param("uid1") int uid1,
                                  @Param("uid2") int uid2);

    @Update("truncate table "
            + tableName)
    public int deleteAll();
}
