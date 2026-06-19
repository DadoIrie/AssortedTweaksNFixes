    package com.dadoirie.assortedtweaksnfixes.client;

    import com.mojang.blaze3d.platform.InputConstants;
    import net.minecraft.client.KeyMapping;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.Camera;
    import net.minecraft.core.BlockPos;
    import net.minecraft.world.phys.AABB;
    import net.neoforged.api.distmarker.Dist;
    import net.neoforged.bus.api.SubscribeEvent;
    import net.neoforged.fml.common.EventBusSubscriber;
    import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
    import net.neoforged.neoforge.client.event.ClientTickEvent;
    import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
    import net.neoforged.neoforge.client.event.RenderHandEvent;
    import net.neoforged.neoforge.client.settings.KeyConflictContext;

    @EventBusSubscriber(value = Dist.CLIENT)
    public class Freecam {

        public static final KeyMapping TOGGLE_KEY = new KeyMapping(
                "key.assortedtweaksnfixes.toggle_freecam",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_F8,
                "key.categories.assortedtweaksnfixes"
        );

        private static boolean active = false;
        public static double x, y, z;
        public static double prevX, prevY, prevZ;
        public static float yaw, pitch;

        public static boolean isActive() { return active; }

        public static void toggle(Camera camera) {
            active = !active;
            if (active && camera != null) {
                x = prevX = camera.getPosition().x;
                y = prevY = camera.getPosition().y;
                z = prevZ = camera.getPosition().z;
                yaw = camera.getYRot();
                pitch = camera.getXRot();
            }
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            while (TOGGLE_KEY.consumeClick()) {
                toggle(mc.gameRenderer.getMainCamera());
            }

            if (active && mc.player != null) {
                if (mc.player.hurtTime > 0 && mc.player.hurtTime == mc.player.hurtDuration - 1) {
                    active = false;
                    mc.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("§cFreecam disabled: Damage taken!"),
                            true
                    );
                }
            }

            if (active && mc.player != null && mc.level != null) {
                prevX = x; prevY = y; prevZ = z;

                double nextX = x, nextY = y, nextZ = z;
                double forward = 0, strafe = 0;

                if (mc.options.keyUp.isDown()) forward += 1;
                if (mc.options.keyDown.isDown()) forward -= 1;
                if (mc.options.keyLeft.isDown()) strafe += 1;
                if (mc.options.keyRight.isDown()) strafe -= 1;

                if (forward != 0 || strafe != 0) {
                    double radYaw = Math.toRadians(yaw);
                    nextX += (strafe * Math.cos(radYaw) - forward * Math.sin(radYaw)) * 0.5;
                    nextZ += (forward * Math.cos(radYaw) + strafe * Math.sin(radYaw)) * 0.5;
                }
                if (mc.options.keyJump.isDown()) nextY += 0.5;
                if (mc.options.keyShift.isDown()) nextY -= 0.5;

                AABB cameraBox = new AABB(
                        nextX - 0.15, nextY - 0.15, nextZ - 0.15,
                        nextX + 0.15, nextY + 0.15, nextZ + 0.15
                );

                boolean hasSolidCollision = !mc.level.noCollision(cameraBox);

                BlockPos targetPos = new BlockPos((int) nextX, (int) nextY, (int) nextZ);
                boolean hasFluidCollision = !mc.level.getFluidState(targetPos).isEmpty();

                if (!hasSolidCollision && !hasFluidCollision) {
                    x = nextX;
                    y = nextY;
                    z = nextZ;
                }
            }
        }

        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            if (active) {
                event.getInput().forwardImpulse = 0;
                event.getInput().leftImpulse = 0;
                event.getInput().up = false;
                event.getInput().down = false;
                event.getInput().left = false;
                event.getInput().right = false;
                event.getInput().jumping = false;
                event.getInput().shiftKeyDown = false;
            }
        }

        @SubscribeEvent
        public static void onRenderHand(RenderHandEvent event) {
            if (active) event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
            if (active) {
                active = false;
            }
        }
    }