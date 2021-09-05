package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.FriendRequestDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FriendRequestMapper {

    public static final String tableName = "fishpond.friendrequest";

    public static final String sql_insertColumns =
            "(requester, receiver, request_time, explanation)";

    @Select("select * from  "
            + tableName)
    public List<FriendRequestDO> selectAll();

    @Select("select * from  "
            + tableName
            + " where receiver = #{receiver}")
    public List<FriendRequestDO> selectByReceiver(@Param("receiver") int receiver);

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

    @Select("select count(*) from "
            + tableName
            + " where requester = #{requester} and receiver = #{receiver}")
    public int selectByPK(@Param("requester") int requester,
                          @Param("receiver") int receiver);


    @Delete("delete from "
            + tableName
            + " where requester = #{requester} and receiver = #{receiver}")
    public int deleteByPK(@Param("requester") int requester,
                          @Param("receiver") int receiver);

    @Update("truncate table "
            + tableName)
    public int deleteAll();
}
