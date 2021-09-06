package edu.bit.fishpondops.DAO.mapper;

import edu.bit.fishpondops.DAO.DO.GroupMemberDO;
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

    @Select("select count(distinct(gid)) from  "
            + tableName
            + " where memberID = #{uid}")
    public int selectGroupNumByUid(@Param("uid") int uid);


    @Select("select count(*) from "
            + tableName
            + " where gid = #{gid}")
    public int selectUserNumByGid(@Param("gid") int gid);

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public GroupMemberDO selectBatch(@Param("columnName") String columnName,
                                     @Param("columnValue") String columnValue);

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
