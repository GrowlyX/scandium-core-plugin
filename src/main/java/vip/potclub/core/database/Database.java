package vip.potclub.core.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import vip.potclub.core.CorePlugin;

@Getter
public class Database {

    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection playerCollection;
    private final MongoCollection ranksCollection;

    public Database() {
        this.client = new MongoClient(new MongoClientURI(CorePlugin.getInstance().getConfig().getString("mongodb.url")));
        this.database = client.getDatabase("core");
        this.playerCollection = this.database.getCollection("profiles");
        this.ranksCollection = this.database.getCollection("ranks");
    }
}
