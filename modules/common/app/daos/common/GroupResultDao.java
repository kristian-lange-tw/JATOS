package daos.common;

import models.common.Batch;
import models.common.GroupResult;
import models.common.GroupResult.GroupState;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * DAO for GroupResult
 *
 * @author Kristian Lange
 */
@Singleton
public class GroupResultDao extends AbstractDao {

    @Inject
    GroupResultDao(JPAApi jpa) {
        super(jpa);
    }

    public GroupResult create(GroupResult groupResult) {
        persist(groupResult);
        return groupResult;
    }

    public void update(GroupResult groupResult) {
        merge(groupResult);
    }

    public void remove(GroupResult groupResult) {
        super.remove(groupResult);
    }

    public void refresh(GroupResult groupResult) {
        super.refresh(groupResult);
    }

    public GroupResult findById(Long id) {
        return jpa.em().find(GroupResult.class, id);
    }

    public List<GroupResult> findAllByBatch(Batch batch) {
        String queryStr = "SELECT gr FROM GroupResult gr WHERE gr.batch=:batch";
        TypedQuery<GroupResult> query = jpa.em().createQuery(queryStr, GroupResult.class);
        return query.setParameter("batch", batch).getResultList();
    }

    public Integer countByBatch(Batch batch) {
        String queryStr = "SELECT COUNT(*) FROM GroupResult gr WHERE gr.batch=:batch";
        Query query = jpa.em().createQuery(queryStr).setParameter("batch", batch);
        Number result = (Number) query.getSingleResult();
        return result.intValue();
    }

    public List<GroupResult> findAllStartedByBatch(Batch batch) {
        String queryStr = "SELECT gr FROM GroupResult gr WHERE gr.batch=:batch "
                + "AND gr.groupState=:groupState ";
        TypedQuery<GroupResult> query = jpa.em().createQuery(queryStr, GroupResult.class);
        return query.setParameter("batch", batch)
                .setParameter("groupState", GroupState.STARTED).getResultList();
    }

    /**
     * Searches the database for all GroupResults of this batch where the
     * maxActiveMembers size and maxTotalMembers size are not reached yet and
     * that ate in state STARTED.
     */
    public List<GroupResult> findAllMaxNotReached(Batch batch) {
        String queryStr = "SELECT gr FROM GroupResult gr, Batch b "
                + "WHERE gr.batch=:batch AND b.id=:batch "
                + "AND gr.groupState=:groupState "
                + "AND (b.maxActiveMembers is null OR size(gr.activeMemberList) < b.maxActiveMembers) "
                + "AND (b.maxTotalMembers is null OR ((size(gr.activeMemberList) + size(gr.historyMemberList)) < b.maxTotalMembers))";
        TypedQuery<GroupResult> query = jpa.em().createQuery(queryStr, GroupResult.class);
        query.setParameter("batch", batch);
        query.setParameter("groupState", GroupState.STARTED);
        return query.getResultList();
    }

}
