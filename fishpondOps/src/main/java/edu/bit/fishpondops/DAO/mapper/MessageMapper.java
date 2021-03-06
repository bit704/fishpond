package edu.bit.fishpondops.DAO.mapper;

import edu.bit.fishpondops.DAO.DO.MessageDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MessageMapper {

    public static final String tableName = "fishpond.message";

    public static final String sql_insertColumns =
            "(sender,receiver,send_time,mtype,content)";

    @Select("select * from  "
            + tableName)
    public List<MessageDO> selectAll();

    @Select("select * from "
            + tableName
            + " where receiver = #{receiver} ")
    public List<MessageDO> selectByReceiver(@Param("receiver") int receiver);

    @Select("select * from "
            + tableName
            + " where sender = #{sender} ")
    public List<MessageDO> selectBySender(@Param("sender") int sender);

    @Select("select * from "
            + tableName
            + " where receiver = #{receiver} and send_time >= #{send_time}")
    public List<MessageDO> selectByReceiverBeforeTime(@Param("receiver") int receiver,
                                                     @Param("send_time") String send_time);

    @Select("select count(*) from "
            + tableName
            + " where send_time >= #{send_time}")
    public int selectCountBeforeTime(@Param("send_time") String send_time);

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
