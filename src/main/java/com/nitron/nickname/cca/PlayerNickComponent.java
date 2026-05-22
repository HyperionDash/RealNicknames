package com.nitron.nickname.cca;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class PlayerNickComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final Player player;

    private boolean hasNickname = false;
    private String nickname = "";
    private boolean hasColor = false;
    private String color = "";

    public PlayerNickComponent(Player player) {
        this.player = player;
    }
    private void sync(){NicknameComponents.NICKNAME.sync(this.player);}
    public static PlayerNickComponent get(@NotNull Player player) {return (PlayerNickComponent) NicknameComponents.NICKNAME.get(player);}


    @Override
    public void tick() {

    }

    @Override
    public void readData(ValueInput readView) {
        this.hasNickname = readView.getBooleanOr("hasNickname", false);
        this.hasColor = readView.getBooleanOr("hasColor", false);
        this.nickname = readView.getString("nickname").orElse("");
        this.color = readView.getString("color").orElse("");
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.putBoolean("hasNickname", this.hasNickname);
        writeView.putBoolean("hasColor", this.hasColor);
        writeView.putString("nickname", this.nickname);
        writeView.putString("color", this.color);
    }

    public boolean isHasNickname() {
        return hasNickname;
    }

    public void setHasNickname(boolean hasNickname) {
        this.hasNickname = hasNickname;
        this.sync();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.sync();
    }

    public boolean isHasColor() {
        return hasColor;
    }

    public void setHasColor(boolean hasColor) {
        this.hasColor = hasColor;
        this.sync();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        this.sync();
    }
}
