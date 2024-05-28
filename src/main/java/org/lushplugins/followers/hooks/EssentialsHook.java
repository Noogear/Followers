package org.lushplugins.followers.hooks;

import com.earth2me.essentials.Essentials;
import org.lushplugins.followers.data.FollowerUser;
import org.lushplugins.followers.entity.poses.FollowerPose;
import org.lushplugins.followers.Followers;
import org.lushplugins.followers.entity.FollowerEntity;
import net.ess3.api.IUser;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.listener.EventListener;

import java.util.UUID;

public class EssentialsHook extends Hook implements EventListener {
    private Essentials essentials;

    public EssentialsHook() {
        super("Essentials");
    }

    @Override
    protected void onEnable() {
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        this.registerListeners();
    }

    @Override
    protected void onDisable() {
        this.unregisterListeners();
        essentials = null;
    }

    @EventHandler
    public void onAFK(AfkStatusChangeEvent event) {
        IUser iUser = event.getAffected();
        Player player = iUser.getBase();
        FollowerUser followerUser = Followers.getInstance().getDataManager().getFollowerUser(player);
        FollowerEntity followerEntity = followerUser.getFollowerEntity();
        if (followerEntity == null || !followerEntity.isAlive()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(Followers.getInstance(), (task) -> {
            if (iUser.isAfk()) {
                followerUser.setAfk(true);
                followerEntity.setPose(FollowerPose.SITTING);
            } else {
                followerUser.setAfk(false);
                if (!followerUser.isPosing()) {
                    followerUser.setPose(FollowerPose.DEFAULT);
                }
            }
        }, 1);
    }

    public boolean isVanished(UUID uuid) {
        IUser user = essentials.getUser(uuid);
        if (user == null) {
            return false;
        } else {
            return user.isVanished();
        }
    }
}