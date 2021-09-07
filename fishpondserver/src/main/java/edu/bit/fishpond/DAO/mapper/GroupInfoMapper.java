package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.GroupInfoDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupInfoMapper {

    public static final String tableName = "fishpond.groupinfo";

    public static final String sql_insertColumns =
            "(name, creator, create_time, manager, real)";

    @Select("select * from  "
            + tableName)
    public List<GroupInfoDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public GroupInfoDO selectBatch(@Param("columnName") String columnName,
                                   @Param("columnValue") String columnValue);


    @Select("select * from "
            + tableName
            + " where gid = #{gid}")
    public GroupInfoDO selectById(@Param("gid") int gid);

    @Select("select name from "
            + tableName
            + " where gid = #{gid}")
    public String selectNameById(@Param("gid") int gid);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{name}, #{creator}, #{create_time}, #{manager}, #{real}) ")
    public int insertOne(@Param("name") String name,
                         @Param("creator") int creator,
                         @Param("create_time") String create_time,
                         @Param("manager") int manager,
                         @Param("real") Boolean real);

    @Update("update "
            + tableName
            + " set ${columnName} = ${columnValue} where gid = #{gid}")
    public boolean updateOne(@Param("columnName") String columnName,
                             @Param("columnValue") String columnValue,
                             @Param("uid") int gid);

    @Delete("delete from "
            + tableName
            + " where gid = #{gid}")
    public int deleteByGid(@Param("gid") int gid);

    @Update("truncate table "
            + tableName +
            " cascade ")
    public int deleteAll();

    /**
     * 设置自增序列的起始序号
     *
     * @param start 起始序号
     * @return 起始序号
     */
    @Select("select setval('fishpond.groupinfo_uid_seq',#{start},false)")
    public int resetSeq(@Param("start") int start);

    /**
     * 获取下一个序列号
     *
     * @return 下一个序列号
     */
    @Select("select last_value from fishpond.groupinfo_uid_seq")
    public int getLastSqValue();
}
