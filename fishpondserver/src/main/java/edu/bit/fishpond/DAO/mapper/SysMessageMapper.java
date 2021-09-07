package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.SysMessageDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

    @Select("select * from  "
            + tableName
            + " where smid = #{smid}")
    public SysMessageDO selectBySmid(@Param("smid") int smid);

    @Select("select smid from "
            + tableName
            + " where uid = #{uid} and send_time > #{last_offline}")
    public List<Integer> selectBeforeTime(@Param("uid") int uid,
                                          @Param("last_offline") String last_offline);

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


    @Update("truncate table "
            + tableName)
    public int deleteAll();

    /**
     * 获取上一个序列号
     *
     * @return 上一个序列号
     */
    @Select("select last_value from fishpond.sysmessage_smid_seq")
    public int getLastSqValue();
}
