package vip.potclub.core.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;

@Getter
public class Database {

    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> playerCollection;
    private final MongoCollection<Document> punishmentCollection;

    public Database() {
        this.client = new MongoClient(new MongoClientURI(CorePlugin.getInstance().getConfig().getString("mongodb.url")));
        this.database = client.getDatabase("PotClub_DB");
        this.playerCollection = this.database.getCollection("core_profiles");
        this.punishmentCollection = this.database.getCollection("punishments");
    }
}
