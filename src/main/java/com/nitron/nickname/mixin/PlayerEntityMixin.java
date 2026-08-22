package com.nitron.nickname.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.nitron.nickname.RealNickname;
import com.nitron.nickname.cca.PlayerNickComponent;
import com.nitron.nickname.config.NicknameConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    private Component changeNickname(Component original){
        Player self = ((Player) (Object) this);
        PlayerNickComponent nickname = PlayerNickComponent.get(self);

        if(nickname.isHasNickname()){
            if (nickname.isHasColor() && NicknameConfig.showColor) {
                return Component.literal(nickname.getNickname()).setStyle(Style.EMPTY.withColor(RealNickname.convertToHex(nickname.getColor())));
            } else {
                return Component.literal(nickname.getNickname());
            }
        }
        return original;
    }
}
