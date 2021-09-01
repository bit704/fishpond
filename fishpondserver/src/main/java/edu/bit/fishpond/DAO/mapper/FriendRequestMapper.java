package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.FriendRequestDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FriendRequestMapper {

    public static final String tableName = "fishpond.friendrequest";

    public static final String sql_insertColumns =
            "(requester, receiver, request_time, explanation)";

    @Select("select * from  "
            + tableName)
    public List<FriendRequestDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public FriendRequestDO selectBatch(@Param("columnName") String columnName,
                                       @Param("columnValue") String columnValue);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{requester}, #{receiver}, #{request_time}, #{explanation}) ")
    public int insertOne(@Param("requester") int requester,
                         @Param("receiver") int receiver,
                         @Param("request_time") String request_time,
                         @Param("explanation") String explanation);

    @Delete("delete from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public int deleteBatch(@Param("columnName") String columnName,
                           @Param("columnValue") String columnValue);

    @Update("truncate table "
            + tableName)
    public int deleteAll();
}
