package campionebase.erosionera.network;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CameraOccupation {
    private final UUID playerUUID;
    private final String playerName;
    public float yaw;
    public float pitch;
    private long lastUpdateTime;

    public CameraOccupation(Player player){
        this.playerName = player.getName().getString();
        this.playerUUID = player.getUUID();
        this.yaw = 0f;
        this.pitch = 0f;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void touch(){
        this.lastUpdateTime = System.currentTimeMillis();
    }
}
