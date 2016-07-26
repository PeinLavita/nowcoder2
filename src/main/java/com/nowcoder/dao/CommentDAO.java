package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yby on 2016/7/12.
 */
@Mapper
@Repository
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, " ( ", INSERT_FIELDS, " ) " ,"values ", "(#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    public int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where entity_id = #{entityId} and entity_type = #{entityType} order by id desc"})
    public List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " where entity_type=#{entityType} and entity_id=#{entityId}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update ",TABLE_NAME, "set status=#{status} where entity_id = #{entityId} and entity_type = #{entityType}"})
    void updateCommentStatus(@Param("status") int status, @Param("entityId") int entityId, @Param("entityType") int entityType);
}
