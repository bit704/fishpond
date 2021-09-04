package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.SysMessageDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysMessageMapper {

    public static final String tableName = "fishpond.sysmessage";

    public static final String sql_insertColumns =
            "(user,send_time,mtype,content)";

    @Select("select * from  "
            + tableName)
    public List<SysMessageDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public SysMessageDO selectBatch(@Param("columnName") String columnName,
                                    @Param("columnValue") String columnValue);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{user}, #{send_time}, #{mtype}, #{content}) ")
    public int insertOne(@Param("user") int user,
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
