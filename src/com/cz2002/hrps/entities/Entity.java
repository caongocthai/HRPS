package com.cz2002.hrps.entities;

import com.cz2002.hrps.controls.DatabaseManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Super class of all entities
 */
public abstract class Entity {

  private DatabaseManager databaseManager;
  private static HashMap<Class, ArrayList<Entity>> entities = new HashMap<>();;

  /**
   * Constructor
   * @param database is the name of database file
   */
  Entity(String database) {
    this.databaseManager = new DatabaseManager(database);
    loadEntitiesIfNeeded();
  }

  public abstract Entity newInstance();

  /**
   * Load database for current class and put data in entities
   */
  private void loadEntitiesIfNeeded() {
    Class C = this.getClass();

    if (entities.get(C) != null) {
      return;
    }

    entities.put(C, new ArrayList());

    ArrayList entityList;
    try {
      entityList = databaseManager.load(this);
    } catch (Exception fe) {
      entityList = new ArrayList();
    }

    entities.put(C, entityList);
  }

  /**
   * Save current entity to the database
   * @return write success status
   */
  private boolean writeToDatabase() {
    ArrayList<Entity> entity = entities.get(this.getClass());
    return databaseManager.write(entity);
  }

  /**
   * Find a list of entities
   * @param queries is the queries to search
   * @return null if not found, Object Array of size results if found
   */
  public Entity[] findList(HashMap<String, String> queries) {

    // Get Objects of Entities and create a resultList
    ArrayList<Entity> resultList = entities.get(this.getClass());
    ArrayList<Entity> bufferList = new ArrayList<>();

    for (Map.Entry<String, String> query : queries.entrySet()) {
      String queryKey = query.getKey();
      String queryValue = query.getValue();

      for (Entity entity : resultList) {
        String value = entity.toHashMap().get(queryKey).toLowerCase();

        if (value.equals(queryValue)) {
          bufferList.add(entity);
        }
      }

      resultList = bufferList;
      bufferList = new ArrayList<>();
    }

    // Nothing was found
    if (resultList.size() == 0)
      return (Entity[]) Array.newInstance(this.getClass(), 0);

    // Convert ArrayList to Array
    Entity[] entityArray  = (Entity[]) Array.newInstance(this.getClass(), resultList.size());
    entityArray = resultList.toArray(entityArray);

    return entityArray;
  }

  /**
   * Creates a new object and save to database
   * @return true if success, false otherwise
   */
  public boolean create() {
    ArrayList<Entity> entity =  entities.get(this.getClass());
    entity.add(this);
    return writeToDatabase();
  }

  /**
   * Find the first entity match queries
   * @param queries is the queries to search
   * @return null if not found, Object Array of size results if found
   */
  public Entity find(HashMap<String, String> queries) {
    return findList(queries)[0];
  }

  /**
   * Creates a new object and save to database
   * @return true if success, false otherwise
   */
  public boolean update() {
    return writeToDatabase();
  }

  /**
   * Delete this Object from the Database
   * @return true if success, false otherwise
   */
  public boolean delete() {
    ArrayList<Entity> entity = entities.get(this.getClass());
    entity.remove(this);
    return databaseManager.write(entity);
  }

  /**
   * Converts an Entity object to a HashMap
   * @return HashMap Strng of Guest Data
   */
  public abstract HashMap<String, String> toHashMap();

  /**
   * Convert a HashMap to an Entity
   * @param guestData String of guestData
   * @return Guest Returns a Guest object
   */
  public abstract void fromHashMap(HashMap<String, String> guestData);

}
