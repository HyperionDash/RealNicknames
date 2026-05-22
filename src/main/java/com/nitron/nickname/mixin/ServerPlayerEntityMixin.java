package com.nitron.nickname.mixin;

import com.mojang.authlib.GameProfile;
import com.nitron.nickname.RealNickname;
import com.nitron.nickname.cca.PlayerNickComponent;
import com.nitron.nickname.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
    public ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "initInventoryMenu", at = @At("TAIL"))
    private void spawn(CallbackInfo ci){
        ServerPlayer player = (ServerPlayer) (Object) this;
        updateTabList(player);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci){
        ServerPlayer player = (ServerPlayer) (Object) this;
        updateTabList(player);
    }

    @Inject(method = "getTabListDisplayName", at = @At("TAIL"), cancellable = true)
    private void replaceNameOnTablist(CallbackInfoReturnable<Component> cir){
        ServerPlayer player = (ServerPlayer) (Object) this;
        PlayerNickComponent component = PlayerNickComponent.get(player);
        if(component.isHasNickname()){
            if (component.isHasColor() && Config.showColor) {
                cir.setReturnValue(Component.literal(component.getNickname()).setStyle(Style.EMPTY.withColor(RealNickname.convertToHex(component.getColor()))));
            } else {
                cir.setReturnValue(Component.literal(component.getNickname()));
            }
        }
    }

    @Unique
    private static void updateTabList(ServerPlayer player){
        ServerGamePacketListenerImpl handler = player.connection;
        if(handler != null){
            MinecraftServer server = player.level().getServer();
            if(server != null){
                ServerPlayer playerEntity = server.getPlayerList().getPlayer(player.getUUID());
                if(playerEntity != null){
                    server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, playerEntity));
                }
            }
        }
    }
}
