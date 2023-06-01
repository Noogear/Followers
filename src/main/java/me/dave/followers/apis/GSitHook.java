package me.dave.followers.apis;

import dev.geco.gsit.api.event.*;
import me.dave.followers.entity.pose.FollowerPose;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.dave.followers.Followers;
import me.dave.followers.entity.FollowerEntity;

public class GSitHook implements Listener {

    @EventHandler
    public void onPlayerSit(EntitySitEvent event) {
        if (event.getEntity() instanceof Player player) {
            FollowerEntity follower = Followers.dataManager.getPlayerFollowerMap().get(player.getUniqueId());
            if (follower == null) return;
            follower.setPose(FollowerPose.SITTING);
            follower.startParticles(Particle.CLOUD);
        }
    }

    @EventHandler
    public void onPlayerGetUpFromSeat(EntityGetUpSitEvent event) {
        if (event.getEntity() instanceof Player player) {
            FollowerEntity follower = Followers.dataManager.getPlayerFollowerMap().get(player.getUniqueId());
            if (follower == null) return;
            follower.setPose(FollowerPose.DEFAULT);
            follower.stopParticles();
        }
    }

    @EventHandler
    public void onPlayerPose(PlayerPoseEvent event) {
        Player player = event.getPlayer();
        FollowerEntity follower = Followers.dataManager.getPlayerFollowerMap().get(player.getUniqueId());
        if (follower == null) return;
        Pose pose = event.getPoseSeat().getPose();
        if (pose == Pose.SPIN_ATTACK) {
            follower.setPose(FollowerPose.SPINNING);
            follower.startParticles(Particle.CLOUD);
        }
    }

    @EventHandler
    public void onPlayerUnpose(PlayerGetUpPoseEvent event) {
        Player player = event.getPlayer();
        FollowerEntity follower = Followers.dataManager.getPlayerFollowerMap().get(player.getUniqueId());
        if (follower == null) return;
        follower.setPose(FollowerPose.DEFAULT);
        follower.stopParticles();
    }
}