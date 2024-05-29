package org.lushplugins.followers.data;

import jline.internal.Nullable;
import org.lushplugins.followers.Followers;
import org.lushplugins.followers.entity.Follower;
import org.lushplugins.followers.entity.poses.FollowerPose;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FollowerUser {
    private static final Random random = new Random();
    private final UUID uuid;
    private String username;
    private boolean enabled;
    private String followerType;
    private boolean randomType;
    private String displayName;
    private boolean nameIsOn;
    private Follower follower;
    private boolean afk = false;
    private boolean posing = false;
    private boolean hidden = false;

    public FollowerUser(UUID uuid, String username, String followerType, String followerDisplayName, boolean followerNameEnabled, boolean followerEnabled, boolean randomFollower) {
        this.uuid = uuid;
        this.username = username;
        this.enabled = followerEnabled;
        this.followerType = followerType;
        this.displayName = followerDisplayName;
        this.nameIsOn = followerNameEnabled;
        this.randomType = randomFollower;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFollowerType() {
        return this.followerType;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isDisplayNameEnabled() {
        return this.nameIsOn;
    }

    public boolean isFollowerEnabled() {
        return this.enabled;
    }

    public boolean isRandomType() {
        return this.randomType;
    }

    public void setUsername(String username) {
        this.username = username;
        Followers.getInstance().getDataManager().saveFollowerUser(this);
    }

    public void setFollowerType(String followerType) {
        this.followerType = followerType;
        Followers.getInstance().getDataManager().saveFollowerUser(this);
    }

    public void setRandom(boolean randomize) {
        this.randomType = randomize;
        Followers.getInstance().getDataManager().saveFollowerUser(this);
    }

    public List<String> getOwnedFollowerNames() {
        List<String> followers = new ArrayList<>();
        Player player = getPlayer();
        if (player == null) {
            return followers;
        }

        for (String followerName : Followers.getInstance().getFollowerManager().getFollowerNames()) {
            if (player.hasPermission("followers." + followerName.toLowerCase().replaceAll(" ", "_"))) {
                followers.add(followerName);
            }
        }
        return followers;
    }

    public void randomizeFollowerType() {
        List<String> followerTypes = getOwnedFollowerNames();
        if (follower == null) {
            return;
        }
        follower.setType(followerTypes.get(random.nextInt(followerTypes.size())));
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        Followers.getInstance().getDataManager().saveFollowerUser(this);
    }

    public void setDisplayNameEnabled(boolean nameIsEnabled) {
        this.nameIsOn = nameIsEnabled;
        Followers.getInstance().getDataManager().saveFollowerUser(this);
    }

    public void setFollowerEnabled(boolean followerIsEnabled) {
        this.enabled = followerIsEnabled;
        Followers.getInstance().getDataManager().saveFollowerUser(this);
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean afk) {
        this.afk = afk;

        if (follower == null || posing) return;
        if (afk) follower.setPose(FollowerPose.SITTING);
        else follower.setPose(FollowerPose.DEFAULT);
    }

    public boolean isVanished() {
        Player player = getPlayer();
        if (player == null) {
            return false;
        }

        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }

        return false;
    }

    public boolean isPosing() {
        return posing;
    }

    public void setPose(FollowerPose pose) {
        this.posing = (pose != null && !pose.equals(FollowerPose.DEFAULT));

        if (follower == null) {
            return;
        }

        if (posing) {
            follower.setPose(pose);
        } else if (!afk) {
            follower.setPose(FollowerPose.DEFAULT);
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hide) {
        if (this.hidden == hide) {
            return;
        }

        if (hide) {
            if (follower != null && follower.isAlive()) {
                follower.kill();
            }
        } else if (enabled) {
            if (follower == null || !follower.isAlive()) {
                spawnFollowerEntity();
            }
        }

        this.hidden = hide;
    }

    // TODO: turn Follower into a non-nullable object
    public Follower getFollowerEntity() {
        return follower;
    }

    public void refreshFollowerEntity() {
        if (follower != null) {
            follower.reloadInventory();
        }
    }

    public void spawnFollowerEntity() {
        removeFollowerEntity();
        Player player = getPlayer();
        if (player == null || player.isDead()) {
            return;
        }

        if (follower == null) {
            follower = new Follower(player, followerType);
        }

        if (follower.spawn() && randomType) {
            randomizeFollowerType();
        }
    }

    public void respawnFollowerEntity() {
        removeFollowerEntity();
        Bukkit.getScheduler().runTaskLater(Followers.getInstance(), this::spawnFollowerEntity, 5);
    }

    public void removeFollowerEntity() {
        if (follower != null) {
            follower.kill();
        }
    }

    public void disableFollowerEntity() {
        setFollowerEnabled(false);
        removeFollowerEntity();
    }
}
