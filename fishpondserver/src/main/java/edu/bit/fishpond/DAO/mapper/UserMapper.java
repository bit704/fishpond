package edu.bit.fishpond.DAO.mapper;

import edu.bit.fishpond.DAO.DO.UserDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {

    public static final String tableName = "fishpond.user";

    public static final String sql_insertColumns =
            "(password, question, answer)";

    @Select("select * from  "
            + tableName)
    public List<UserDO> selectAll();

    @Select("Select * from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public UserDO selectBatch(@Param("columnName") String columnName,
                              @Param("columnValue") String columnValue);

    @Insert("insert into "
            + tableName
            + sql_insertColumns
            + " values (#{password}, #{question}, #{answer}) ")
    public int insertOne(@Param("password") String password,
                         @Param("question") String question,
                         @Param("answer") String answer);

    @Update("update "
            + tableName
            + " set ${columnName} = ${columnValue} where uid = #{uid}")
    public boolean updateOne(@Param("columnName") String columnName,
                             @Param("columnValue") String columnValue,
                             @Param("uid") int uid);

    @Delete("delete from "
            + tableName
            + " where ${columnName} = ${columnValue}")
    public int deleteBatch(@Param("columnName") String columnName,
                           @Param("columnValue") String columnValue);

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
    @Select("select setval('fishpond.user_uid_seq',#{start},false)")
    public int resetSeq(@Param("start") int start);

}
