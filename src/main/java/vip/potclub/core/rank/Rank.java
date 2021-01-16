package vip.potclub.core.rank;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import vip.potclub.core.manager.RankManager;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Rank implements Comparable<Rank> {

    @SerializedName("inheritance")
    private List<String> inherits = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    @NonNull
    private String id;

    private int weight = 0;

    private String name;
    private String prefix = "§7";
    private String color = "§7";

    private boolean staff = false;
    private boolean developer = false;
    private boolean donator = false;
    private boolean normal = true;

    public List<String> getInheritances() {
        List<String> inheritances = new ArrayList<>(this.inherits);
        this.inherits.forEach((inheritedRankId) -> {
            inheritances.addAll(RankManager.getById(inheritedRankId).getInheritances());
        });
        return inheritances;
    }

    public boolean hasPermission(String requiredPermission) {
        return this.permissions.stream().anyMatch((permission) -> permission.equalsIgnoreCase(requiredPermission));
    }

    public int compareTo(Rank rank) {
        return Integer.compare(this.weight, rank.weight);
    }

    @ConstructorProperties({"id"})
    public Rank(String id) {
        if (id == null) {
            throw new NullPointerException("id");
        } else {
            this.id = id;
        }
    }
}
