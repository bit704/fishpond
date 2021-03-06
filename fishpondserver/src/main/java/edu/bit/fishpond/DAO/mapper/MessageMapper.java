package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.MessageDO;
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

    @Select("select * from  "
            + tableName
            + " where mid = #{mid}")
    public MessageDO selectByMid(@Param("mid") int mid);

    @Select("select * from "
            + tableName
            + " where receiver = #{receiver} ")
    public List<MessageDO> selectByReceiver(@Param("receiver") int receiver);

    // todo: 这个sql需要优化
    @Select("select mid from "
            + tableName
            + " where ((receiver = #{receiver} and sender = #{sender}) or (receiver = #{sender} and sender = #{receiver}))"
            + " and send_time = (select max(send_time) from "
            + tableName
            + " where (receiver = #{receiver} and sender = #{sender}) or (receiver = #{sender} and sender = #{receiver}) )")
    public List<Integer> selectMidByPartnerLatest(@Param("sender") int sender, @Param("receiver") int receiver);

    @Select("select * from "
            + tableName
            + " where sender = #{sender} ")
    public List<MessageDO> selectBySender(@Param("sender") int sender);

    @Select("select * from "
            + tableName
            + " where ((receiver = #{receiver} and sender = #{sender}) or (receiver = #{sender} and sender = #{receiver}))")
    public List<MessageDO> selectByPartner(@Param("sender") int sender, @Param("receiver") int receiver);

    @Select("select mid from "
            + tableName
            + " where ((receiver = #{receiver} and sender = #{sender}) or (receiver = #{sender} and sender = #{receiver}))")
    public List<Integer> selectMidByPartner(@Param("sender") int sender, @Param("receiver") int receiver);

    @Select("select mid from "
            + tableName
            + " where receiver = #{receiver} and send_time >= #{send_time}")
    public List<Integer> selectByReceiverBeforeTime(@Param("receiver") int receiver,
                                                    @Param("send_time") String send_time);

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
            + " where mid = #{mid}")
    public int deleteByMid(@Param("mid") int mid);

    @Update("truncate table "
            + tableName)
    public int deleteAll();

    /**
     * 获取上一个序列号
     *
     * @return 上一个序列号
     */
    @Select("select last_value from fishpond.message_mid_seq")
    public int getLastSqValue();

}
