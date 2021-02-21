package vip.potclub.core.command.extend.grant;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.extend.grant.GrantMainMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.DateUtil;
import vip.potclub.core.util.StringUtil;
import vip.potclub.core.util.UUIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CGrantCommand extends BaseCommand {

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                sender.sendMessage(Color.translate("&cNOTE: This is not the final granting system."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                    sender.sendMessage(Color.translate("&cNOTE: This is not the final granting system."));
                }
                if (args.length == 2) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                    sender.sendMessage(Color.translate("&cNOTE: This is not the final granting system."));
                }
                if (args.length > 2) {
                    Map.Entry<UUID, String> uuid = UUIDUtil.getUUID(args[0]);

                    if ((uuid.getKey() != null)) {
                        Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(uuid.getKey());
                        if (document != null) {
                            Rank rank = Rank.getByName(args[1]);
                            ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

                            if (rank != null) {
                                if (args[2].equalsIgnoreCase("perm") || args[2].equalsIgnoreCase("permanent")) {
                                    String reason = StringUtil.buildMessage(args, 3);
                                    PotPlayer targetPotPlayer = PotPlayer.getPlayer(args[0]);
                                    Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), 2147483647L, reason, true, true);

                                    if (targetPotPlayer != null) {
                                        targetPotPlayer.getAllGrants().add(newGrant);
                                        targetPotPlayer.setupAttachment();
                                        targetPotPlayer.saveWithoutRemove();

                                        targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                        sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                    } else {
                                        List<Grant> allGrants = new ArrayList<>();

                                        if ((!((List<String>) document.get("allGrants")).isEmpty()) || ((document.get("allGrants") != null))) {
                                            List<String> allStringGrants = ((List<String>) document.get("allGrants"));
                                            allStringGrants.forEach(s -> allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                                        }

                                        allGrants.add(newGrant);

                                        List<String> grantStrings = new ArrayList<>();
                                        allGrants.forEach(grant -> grantStrings.add(grant.toJson()));

                                        document.put("allGrants", grantStrings);

                                        Map.Entry<UUID, String> uuidStringEntry = UUIDUtil.getUUID(document.getString("name"));
                                        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuidStringEntry.getKey()), document, new ReplaceOptions().upsert(true)));

                                        sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + uuidStringEntry.getValue() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                    }
                                } else {
                                    try {
                                        String reason = StringUtil.buildMessage(args, 3);
                                        PotPlayer targetPotPlayer = PotPlayer.getPlayer(args[0]);
                                        Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), System.currentTimeMillis() - DateUtil.parseDateDiff(args[2], false), reason, true, false);

                                        if (targetPotPlayer != null) {
                                            targetPotPlayer.getAllGrants().add(newGrant);
                                            targetPotPlayer.setupAttachment();
                                            targetPotPlayer.saveWithoutRemove();

                                            targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                            sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                        } else {
                                            List<Grant> allGrants = new ArrayList<>();

                                            if ((!((List<String>) document.get("allGrants")).isEmpty()) || ((document.get("allGrants") != null))) {
                                                List<String> allStringGrants = ((List<String>) document.get("allGrants"));
                                                allStringGrants.forEach(s -> allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                                            }

                                            allGrants.add(newGrant);

                                            List<String> grantStrings = new ArrayList<>();
                                            allGrants.forEach(grant -> grantStrings.add(grant.toJson()));

                                            document.put("allGrants", grantStrings);

                                            Map.Entry<UUID, String> uuidStringEntry = UUIDUtil.getUUID(document.getString("name"));
                                            CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuidStringEntry.getKey()), document, new ReplaceOptions().upsert(true)));

                                            sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + uuidStringEntry.getValue() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                        }
                                    } catch (Exception exception) {
                                        sender.sendMessage(ChatColor.RED + "Invalid duration.");
                                    }
                                }
                            } else {
                                sender.sendMessage(Color.translate("&cThat rank does not exist."));
                            }
                        } else {
                            sender.sendMessage(Color.translate("&cThat player does not exist in our databases."));
                        }
                    } else {
                        sender.sendMessage(Color.translate("&cThat minecraft profile does not exist."));
                    }
                }
            }
            return false;
        }
        return false;
    }
}
