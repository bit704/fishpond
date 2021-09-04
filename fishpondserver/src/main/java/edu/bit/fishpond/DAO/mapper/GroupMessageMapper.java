package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.GroupMessageDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupMessageMapper {

    public static final String tableName = "fishpond.groupmessage";

    public static final String sql_insertColumns =
            "(sender,receiver,send_time,mtype,content)";

    @Select("select * from  "
            + tableName)
    public List<GroupMessageDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public GroupMessageDO selectBatch(@Param("columnName") String columnName,
                                      @Param("columnValue") String columnValue);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{sender}, #{receiver}, #{send_time}, #{mtype}, #{content}) ")
    public int insertOne(@Param("sender") int sender,
                         @Param("receiver") int receiver,
                         @Param("send_time") String send_time,
                         @Param("mtype") String mtype,
                         @Param("content") String content);

    @Delete("delete from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public int deleteBatch(@Param("columnName") String columnName,
                           @Param("columnValue") String columnValue);

    @Update("truncate table "
            + tableName)
    public int deleteAll();
}
