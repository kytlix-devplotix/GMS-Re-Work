package com.kytlix_devplotix.gms_rw.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItemSpeckledMelon {

    @Inject(method = "getMaxItemUseDuration", at = @At("HEAD"), cancellable = true)
    private void setMelonEatDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() == Items.SPECKLED_MELON) {
            cir.setReturnValue(32);
        }
    }

    @Inject(method = "getItemUseAction", at = @At("HEAD"), cancellable = true)
    private void setMelonEatAction(ItemStack stack, CallbackInfoReturnable<EnumAction> cir) {
        if (stack.getItem() == Items.SPECKLED_MELON) {
            cir.setReturnValue(EnumAction.EAT);
        }
    }

    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true)
    private void allowEatingSpeckledMelon(World worldIn, EntityPlayer playerIn, EnumHand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (itemstack.getItem() == Items.SPECKLED_MELON) {
            if (playerIn.getFoodStats().needFood()) {
                playerIn.setActiveHand(handIn);
                cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, itemstack));
            } else {
                cir.setReturnValue(new ActionResult<>(EnumActionResult.FAIL, itemstack));
            }
        }
    }

    @Inject(method = "onItemUseFinish", at = @At("HEAD"), cancellable = true)
    private void applySpeckledMelonEffects(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.getItem() == Items.SPECKLED_MELON && entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;

            if (!worldIn.isRemote) {
                player.getFoodStats().addStats(2, 0.2F);

                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));

                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 0));
            }

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            cir.setReturnValue(stack);
        }
    }
}