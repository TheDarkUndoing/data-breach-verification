package net.anubis.passwordDB;
import java.util.Arrays;
import java.util.ArrayList;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import java.util.HashSet;



public class DatabaseHandlerMongo
{
  private MongoClient mongoClient = MongoClients.create();
  private MongoDatabase database;
  private MongoCollection<Document> collection;
  private ArrayList passList;
  private int entryCount;
  private HashSet<String> userIndex = new HashSet<String>();



  public DatabaseHandlerMongo(String database,String collection)
  {
    this.database = mongoClient.getDatabase(database);
    this.collection = this.database.getCollection(collection);
    System.out.println("Database: "+database+"\nCollection: "+collection+"\nCONNECTED");

  }
  public void addUserToIndex(String user)
  {
    this.userIndex.add(user);
  }
  public boolean seen(String user)
  {
    return this.userIndex.contains(user);
  }
  public void insert(String[] passCombo)
  {
    String username = passCombo[0];
    String password = passCombo[1];
    Document wd = null;


    if(!seen(username))
    {
      addUserToIndex(username);
      wd = new Document("user",username)
              .append("pass",Arrays.asList(password))
              .append("count",1);
      this.collection.insertOne(wd);
    }
    else
    {
      long startFind = System.currentTimeMillis();
      wd = collection.find(eq("user",username)).first();
      long endFind = System.currentTimeMillis();
      System.out.println((endFind - startFind) + "ms Find Doc");
      long startpassCheck = System.currentTimeMillis();

      passList = (ArrayList)wd.get("pass");

      if(!passList.contains(password))
      {
        entryCount = (int)wd.get("count");
        //System.out.println(entryCount);
        this.collection.updateOne(eq("user",username), combine(set("count",entryCount + 1),push("pass",password)));
      }
      long endpassCheck = System.currentTimeMillis();

      System.out.println((endpassCheck-startpassCheck) + "ms passcheck");

    }




  }
  public void close()
  {
    this.mongoClient.close();
  }
}
