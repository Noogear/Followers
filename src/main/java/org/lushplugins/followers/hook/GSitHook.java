package org.lushplugins.followers.hook;

import dev.geco.gsit.api.event.*;
import org.lushplugins.followers.data.FollowerUser;
import org.lushplugins.followers.entity.Follower;
import org.lushplugins.followers.entity.poses.FollowerPose;
import org.lushplugins.followers.Followers;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.lushplugins.followers.entity.tasks.TaskId;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.listener.EventListener;

public class GSitHook extends Hook implements EventListener {

    public GSitHook() {
        super("GSit");
    }

    @Override
    protected void onEnable() {
        this.registerListeners();
    }

    @Override
    protected void onDisable() {
        this.unregisterListeners();
    }

    @EventHandler
    public void onPlayerSit(EntitySitEvent event) {
        if (event.getEntity() instanceof Player player) {
            FollowerUser followerUser = Followers.getInstance().getDataManager().getFollowerUser(player);
            followerUser.setPose(FollowerPose.SITTING);
            Follower follower = followerUser.getFollower();
            if (follower == null || !follower.isSpawned()) {
                return;
            }

            follower.addTask(TaskId.PARTICLE_CLOUD);
        }
    }

    @EventHandler
    public void onPlayerGetUpFromSeat(EntityGetUpSitEvent event) {
        if (event.getEntity() instanceof Player player) {
            FollowerUser followerUser = Followers.getInstance().getDataManager().getFollowerUser(player);
            followerUser.setPose(FollowerPose.DEFAULT);
            Follower follower = followerUser.getFollower();
            if (follower == null || !follower.isSpawned()) {
                return;
            }

            follower.removeTask(TaskId.PARTICLE_CLOUD);
        }
    }

    @EventHandler
    public void onPlayerPose(PlayerPoseEvent event) {
        Player player = event.getPlayer();
        Follower follower = Followers.getInstance().getDataManager().getFollowerUser(player).getFollower();
        if (follower == null || !follower.isSpawned()) {
            return;
        }

        Pose pose = event.getPoseSeat().getPose();
        if (pose == Pose.SPIN_ATTACK) {
            follower.setPose(FollowerPose.SPINNING);
            follower.addTask(TaskId.PARTICLE_CLOUD);
        }
    }

    @EventHandler
    public void onPlayerEndPose(PlayerGetUpPoseEvent event) {
        Player player = event.getPlayer();
        Follower follower = Followers.getInstance().getDataManager().getFollowerUser(player).getFollower();
        if (follower == null || !follower.isSpawned()) {
            return;
        }
        
        follower.setPose(FollowerPose.DEFAULT);
        follower.removeTask(TaskId.PARTICLE_CLOUD);
    }
}