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
    private final MongoCollection<Document> prefixCollection;
    private final MongoCollection<Document> punishmentCollection;
    private final MongoCollection<Document> webCollection;
    private final MongoCollection<Document> warpCollection;
    private final MongoCollection<Document> rankCollection;
    private final MongoCollection<Document> ticketCollection;

    public Database() {
        this.client = new MongoClient(new MongoClientURI(CorePlugin.getInstance().getDatabaseConfig().getString("mongodb.url")));

        this.database = client.getDatabase("SGSoftware");

        this.playerCollection = this.database.getCollection("coreprofiles");
        this.prefixCollection = this.database.getCollection("prefix");
        this.rankCollection = this.database.getCollection("ranks");
        this.warpCollection = this.database.getCollection("warps");
        this.punishmentCollection = this.database.getCollection("punishment");
        this.webCollection = this.database.getCollection("website");
        this.ticketCollection = this.database.getCollection("tickets");
    }
}
