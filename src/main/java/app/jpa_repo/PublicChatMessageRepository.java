package app.jpa_repo;

import app.model.chat.ChatMessage;
import app.model.chat.PublicChatMessage;
import app.model.chat.TextChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

public interface PublicChatMessageRepository extends JpaRepository<PublicChatMessage, Long> {

    ArrayList<PublicChatMessage> findAllByParentId(long parentId);
    ArrayList<ChatMessage> findAllByChannelid(long channelId);


    ArrayList<PublicChatMessage> findAllBySenderAndDeletedFalse(long id);

    @Modifying
    @Transactional
    @Query(value = "SELECT courseid FROM channels WHERE id = :id", nativeQuery = true)
    ArrayList<TextChannel>findCourseByChannelId(@Param(value = "id") long id);

    @Modifying
    @Transactional
    @Query(value =" DELETE FROM posts WHERE id = :idparam ",nativeQuery = true)
    void deletePublicChatMessageById(@Param("idparam") long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE posts set MESSAGE = :msg WHERE ID = :idpost")
    void updateMessageById(@Param("idpost") long id, @Param("msg") String messageText);

    @Modifying
    @Transactional
    @Query(value = "UPDATE posts set DELETED = 1")
    void updateDeletedAll();

    @Modifying
    @Transactional
    @Query(value = "UPDATE posts SET DELETED = 1 WHERE channelid = :idchannel ORDER BY ID DESC LIMIT :limitation", nativeQuery = true)
    void updateDeleted(@Param("idchannel") long channel, @Param("limitation") int limit);

    @Modifying
    @Transactional
    @Query(value = "UPDATE posts SET userid = 1, deleted = 1 WHERE userid = :idparam", nativeQuery = true)
    void deleteByUserId(@Param("idparam") long id);

    @Modifying
    @Transactional
    @Query(value =" DELETE FROM posts WHERE id = :idparam ORDER BY ID DESC",nativeQuery = true)
    void deleteById(@Param("idparam") long id);

    @Modifying
    @Transactional
    @Query(value =" SET FOREIGN_KEY_CHECKS=0;",nativeQuery = true)
    void dropConstraint();

    @Modifying
    @Transactional
    @Query(value =" SET FOREIGN_KEY_CHECKS=1;",nativeQuery = true)
    void addConstraint();

    ArrayList<PublicChatMessage> findAllByChannelidOrderByIdDesc(long id);

    PublicChatMessage findById(long id);
}
