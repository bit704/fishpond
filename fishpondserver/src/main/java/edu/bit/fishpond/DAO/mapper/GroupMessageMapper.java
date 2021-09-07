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

    @Select("select gmid from  "
            + tableName
            + " where receiver = #{gid}")
    public List<Integer> selectAllGmid(@Param("gid") int gid);

    @Select("select * from  "
            + tableName
            + " where gmid = #{gmid}")
    public GroupMessageDO selectByGmid(@Param("gmid") int gmid);

    // todo: 这个sql需要优化
    @Select("select gmid from "
            + tableName
            + " where ((receiver = #{receiver} and sender = #{sender}) or (receiver = #{sender} and sender = #{receiver}))"
            + " and send_time = (select max(send_time) from "
            + tableName
            + " where (receiver = #{receiver} and sender = #{sender}) or (receiver = #{sender} and sender = #{receiver}) )")
    public int selectGmidByPartnerLatest(@Param("sender") int sender, @Param("receiver") int receiver);


    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public GroupMessageDO selectBatch(@Param("columnName") String columnName,
                                      @Param("columnValue") String columnValue);

    @Select("select gmid from "
            + tableName
            + " where receiver = #{receiver} and send_time > #{last_offline}")
    public List<Integer> selectBeforeTime(@Param("receiver") int receiver,
                                          @Param("last_offline") String last_offline);


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
            + " where gmid = #{gmid}")
    public int deleteByGmid(@Param("gmid") int gmid);

    @Update("truncate table "
            + tableName)
    public int deleteAll();

    /**
     * 获取上一个序列号
     *
     * @return 上一个序列号
     */
    @Select("select last_value from fishpond.groupmessage_gmid_seq")
    public int getLastSqValue();
}
