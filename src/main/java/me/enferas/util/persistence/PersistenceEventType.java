package me.enferas.util.persistence;

/**
 *
 * Persistence event types that can be used
 *
 * @author Alaa Sarhan
 */
public enum PersistenceEventType {

    /**
     * On add entities
     */
    ADDED_ENTITIES,
    /**
     * On remove entities
     */
    REMOVED_ENTITIES,
    /**
     * On refresh entities
     */
    REFRESHED_ENTITIES,
    /**
     * On update entities
     */
    UPDATED_ENTITIES,
    /**
     * On merge entities
     */
    MERGED_ENTITIES,
    /**
     * UNSPECIFIED
     */
    UNSPECIFIED
}
