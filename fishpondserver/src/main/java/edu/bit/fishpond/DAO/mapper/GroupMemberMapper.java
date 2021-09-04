package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.GroupMemberDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupMemberMapper {

    public static final String tableName = "fishpond.groupmember";

    public static final String sql_insertColumns =
            "(gid, memberID, invitorID, intime)";

    @Select("select * from  "
            + tableName)
    public List<GroupMemberDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public GroupMemberDO selectBatch(@Param("columnName") String columnName,
                                     @Param("columnValue") String columnValue);

    @Select("select distinct(gid) from "
            + tableName
            + " where memberID = #{memberID}")
    public List<Integer> selectGroupByUser(@Param("memberID") int memberID);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{gid}, #{memberID}, #{invitorID}, #{intime}) ")
    public int insertOne(@Param("gid") int gid,
                         @Param("memberID") int memberID,
                         @Param("invitorID") int invitorID,
                         @Param("intime") String intime);

    @Delete("delete from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public int deleteBatch(@Param("columnName") String columnName,
                           @Param("columnValue") String columnValue);

    @Update("truncate table "
            + tableName)
    public int deleteAll();
}
