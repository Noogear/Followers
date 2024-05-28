package org.lushplugins.followers.entity.tasks;

import org.lushplugins.followers.Followers;
import org.lushplugins.followers.entity.FollowerEntity;

public abstract class FollowerTask {
    protected final FollowerEntity followerEntity;
    private final int startTick;
    private boolean cancelled = false;

    public FollowerTask(FollowerEntity followerEntity) {
        this.followerEntity = followerEntity;
        this.startTick = Followers.getInstance().getCurrentTick() + getDelay();
    }

    public abstract void tick();

    public abstract String getIdentifier();

    public int getDelay() {
        return 0;
    }

    public abstract int getPeriod();

    public int getStartTick() {
        return startTick;
    }

    public void cancel() {
        cancelled = true;
        followerEntity.stopTask(getIdentifier());
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
