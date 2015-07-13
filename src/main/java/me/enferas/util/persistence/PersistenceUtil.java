package me.enferas.util.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Holds an instance of EntityManagerFactory and encapsulates general
 * EntityManager methods adding collections support, exception handling and
 * events publishing.
 *
 */
public class PersistenceUtil {

    private final EntityManagerFactory factory;
    private String connectionStringProperty;
    private boolean threadedNotifications = true;

    public PersistenceUtil(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public PersistenceUtil(EntityManagerFactory factory, String connectionStringProperty) {
        this.factory = factory;
        this.connectionStringProperty = connectionStringProperty;
    }

    /**
     * Gets a new instance of EntityManager created by the EntityManagerFactory.
     *
     * @return new instance of EntityManager
     */
    public EntityManager getEntityManager() {

        return factory.createEntityManager();
    }

    /**
     * Persists entities. This will fire the
     * {@link #genericEntitiesPersistenceEvent} event.
     *
     * @param entities entities to persist. this array will be updated with
     * merged instances.
     * @throws PersistenceException
     */
    public void persist(Object[] entities) throws PersistenceException {
        persist(null, true, entities);
    }

    /**
     * Updates or persists entities.
     *
     * @param <T> Type of entities
     * @param source the source of the published event object
     * @param event the event to fire for this operation
     * @param notifyGenericListeners whether to also fire
     * {@link #genericEntitiesPersistenceEvent} event
     * @param entities entities to persist
     * @throws PersistenceException
     */
    public <T> void persist(Object source, boolean notifyGenericListeners, T... entities) throws PersistenceException {
        EntityManager entityManager = getEntityManager();

        Map<Object, Throwable> exceptions = new HashMap<Object, Throwable>();

        for (Object o : entities) {
            try {
                beginTransaction(entityManager);
                entityManager.persist(o);
                commitTransaction(entityManager);
            } catch (Exception ex) {
                exceptions.put(o, ex);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new CollectionPersistenceException(exceptions);
        }

    }

    /**
     * Updates an entity. This will fire the
     * {@link #genericEntitiesPersistenceEvent} event.
     *
     * @param <T> Type of entity
     * @param t the entity to merge
     * @return the merged instance of t
     * @throws me.enferas.util.persistence.PersistenceException
     */
    public <T> T merge(T t) throws PersistenceException {
        return merge(null, t, true);
    }

    /**
     * Updates or persists an entity.
     *
     * @param <T> type of entity
     * @param source the source of the published event object
     * @param event the event to fire for this operation
     * @param t the entity to merge
     * @param notifyGenericListeners whether to also fire
     * {@link #genericEntitiesPersistenceEvent} event
     * @return the merged instance of t
     * @throws PersistenceException
     */
    public <T> T merge(Object source, T t, boolean notifyGenericListeners) throws PersistenceException {
        EntityManager entityManager = getEntityManager();

        Object id = factory.getPersistenceUnitUtil().getIdentifier(t);

        PersistenceEventType eventType;

        if (id == null || (id instanceof Number && ((Number) id).equals(0))) {
            eventType = PersistenceEventType.ADDED_ENTITIES;
        } else {
            eventType = PersistenceEventType.UPDATED_ENTITIES;
        }

        try {
            beginTransaction(entityManager);

            t = entityManager.merge(t);

            commitTransaction(entityManager);

        } catch (Exception ex) {
            throw new PersistenceException(ex);
        }

        return t;
    }

    /**
     * Updates or persists entities.
     *
     * @param entities entities to merge
     * @throws PersistenceException
     */
    public void merge(Object[] entities) throws PersistenceException {
        merge(null, true, entities);
    }

    /**
     * Updates or persists entities.
     *
     * @param <T> type of entity
     * @param source the source of the published event object
     * @param event the event to fire for this operation
     * @param notifyGenericListeners whether to also fire
     * {@link #genericEntitiesPersistenceEvent} event
     * @param entities entities to merge
     * @throws PersistenceException
     */
    public <T> void merge(Object source, boolean notifyGenericListeners, T... entities) throws PersistenceException {
        EntityManager entityManager = getEntityManager();

        Map<Object, Throwable> exceptions = new HashMap<Object, Throwable>();

        PersistenceEventType eventType = PersistenceEventType.UNSPECIFIED;

        for (int i = 0; i < entities.length; i++) {
            try {

                Object id = factory.getPersistenceUnitUtil().getIdentifier(entities[i]);

                switch (eventType) {
                    case ADDED_ENTITIES:
                        if (!(id == null || (id instanceof Number && ((Number) id).equals(0)))) {
                            eventType = PersistenceEventType.MERGED_ENTITIES;
                        }
                        break;
                    case UPDATED_ENTITIES:
                        if (id == null || (id instanceof Number && ((Number) id).equals(0))) {
                            eventType = PersistenceEventType.MERGED_ENTITIES;
                        }
                        break;
                    case UNSPECIFIED:
                        eventType = !(id == null || (id instanceof Number && ((Number) id).equals(0))) ? PersistenceEventType.UPDATED_ENTITIES : PersistenceEventType.ADDED_ENTITIES;
                        break;
                    default:
                }
                beginTransaction(entityManager);
                entities[i] = entityManager.merge(entities[i]);
                commitTransaction(entityManager);
            } catch (Exception ex) {
                exceptions.put(entities[i], ex);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new CollectionPersistenceException(exceptions);
        }

    }

    /**
     * Removes entities.
     *
     * @param entities entities to remove
     * @throws PersistenceException
     */
    public void remove(Object[] entities) throws PersistenceException {
        remove(null, true, entities);
    }

    /**
     * Removes entities.
     *
     * @param <T> type of entity
     * @param source the source of the published event object
     * @param event the event to fire for this operation
     * @param notifyGenericListeners whether to also fire
     * {@link #genericEntitiesPersistenceEvent} event
     * @param entities entities to remove
     * @throws PersistenceException
     */
    public <T> void remove(Object source, boolean notifyGenericListeners, T... entities) throws PersistenceException {
        EntityManager entityManager = getEntityManager();

        Map<Object, Throwable> exceptions = new HashMap<Object, Throwable>();

        for (T e : entities) {
            try {
                beginTransaction(entityManager);
                e = entityManager.merge(e);
                entityManager.remove(e);
                commitTransaction(entityManager);
            } catch (Exception ex) {
                exceptions.put(e, ex);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new CollectionPersistenceException(exceptions);
        }

    }

    /**
     * Finds entity with given type with given id.
     *
     * @param <T> type of entity
     * @param type class of entity
     * @param id entity's id
     * @return found entity or null if not found
     */
    public <T> T find(Class<T> type, Object id) {

        return getEntityManager().find(type, id);
    }

    /**
     * Finds all entities of given type
     *
     * @param <T> type of entities
     * @param type class of entities
     * @return all entities of given type
     */
    public <T> List<T> find(Class<T> type) {
        EntityManager entityManager = getEntityManager();
        if (type != null) {
            String entityName = entityManager.getMetamodel().entity(type).getName();
            return entityManager.createQuery("select t from " + entityName + " t ", type).getResultList();
        } else {
            return null;
        }
    }

    /**
     * Finds entities of given type with given property-values constrain
     *
     * @param <T> type of entities
     * @param type class of entities
     * @param property property name
     * @param values accepted property values
     * @return all entities of given type having one property-value constrain
     * met at least
     */
    public <T> List<T> find(Class<T> type, String property, Object... values) {
        EntityManager entityManager = getEntityManager();
        String entityName = entityManager.getMetamodel().entity(type).getName();
        if (values.length > 1) {
            List<Object> valuesList = Arrays.asList(values);
            return entityManager.createQuery("select t from " + entityName + " t where " + property + " in :values", type).setParameter("values", valuesList).getResultList();
        } else if (values.length == 1) {

            return entityManager
                    .createQuery("select t from " + entityName + " t where " + property + " = :values", type)
                    .setParameter("values", values[0]).getResultList();
        } else {
            return null;
        }
    }

    /**
     * Refreshes an entity
     *
     * @param <T> type of entity
     * @param t entity to refresh
     * @return refreshed instance of t
     * @throws PersistenceException
     */
    public <T> T refresh(T t) throws PersistenceException {
        return refresh(null, t, true);
    }

    /**
     * Refreshes an entity
     *
     * @param <T> type of entity
     * @param source the source of the published event object
     * @param event the event to fire for this operation
     * @param t entity to refresh
     * @param notifyGenericListeners whether to also fire
     * {@link #genericEntitiesPersistenceEvent} event
     * @return refreshed instance of t
     * @throws PersistenceException
     */
    public <T> T refresh(Object source, T t, boolean notifyGenericListeners) throws PersistenceException {

        EntityManager entityManager = getEntityManager();

        try {
            t = entityManager.merge(t);

            entityManager.refresh(t);

            return t;
        } catch (Exception ex) {
            throw new PersistenceException(ex);
        }
    }

    /**
     * Refreshes entities.
     *
     * @param entities entities to refresh
     * @throws CollectionPersistenceException
     */
    public void refresh(Object[] entities) throws CollectionPersistenceException {
        refresh(null, true, entities);
    }

    /**
     * Refreshes entities.
     *
     * @param <T> type of entities
     * @param source the source of the published event object
     * @param event the event to fire for this operation
     * @param notifyGenericListeners whether to also fire
     * {@link #genericEntitiesPersistenceEvent} event
     * @param entities entities to refresh
     * @throws CollectionPersistenceException
     */
    public <T> void refresh(Object source, boolean notifyGenericListeners, T... entities) throws CollectionPersistenceException {
        EntityManager entityManager = getEntityManager();

        Map<Object, Throwable> exceptions = new HashMap<Object, Throwable>();

        for (int i = 0; i < entities.length; i++) {

            try {

                entities[i] = entityManager.merge(entities[i]);

                entityManager.refresh(entities[i]);

            } catch (Exception ex) {
                exceptions.put(entities[i], ex);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new CollectionPersistenceException(exceptions);
        }

    }

    private synchronized void beginTransaction(EntityManager entityManager) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }

    private synchronized void commitTransaction(EntityManager entityManager) {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
            entityManager.close();
        }
    }

    class NotificationProcess implements Runnable {

        private final boolean notifyGenericListeners;
        private final Object source;
        private final PersistenceEventType eventType;
        private final Object[] entities;

        public NotificationProcess(boolean notifyGenericListeners, Object source, PersistenceEventType eventType, Object[] entities) {

            this.notifyGenericListeners = notifyGenericListeners;
            this.source = source;
            this.eventType = eventType;
            this.entities = entities;
        }

        @Override
        public void run() {

        }
    };

    /**
     * Gets the connection string bound to this persistence util
     *
     * @return the connection string
     */
    public Object getConnectionString() {
        if (this.connectionStringProperty == null) {
            return factory.getProperties().get("javax.persistence.jdbc.url");

        } else {
            return factory.getProperties().get(connectionStringProperty);
        }
    }

    /**
     * @return the threadedNotifications
     */
    public boolean isThreadedNotifications() {
        return threadedNotifications;
    }

    /**
     * @param threadedNotifications the threadedNotifications to set
     */
    public void setThreadedNotifications(boolean threadedNotifications) {
        this.threadedNotifications = threadedNotifications;
    }
}
