package net.londonunderground.blocks;

import mtr.block.IBlock;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.Route;
import mtr.gui.ClientData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.londonunderground.Main;
import net.londonunderground.packet.PacketTrainDataGuiServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;

public class SoundTimer extends Block implements BlockEntityProvider {

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static Integer seconds = 9999;

    public SoundTimer(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return IBlock.checkHoldingBrush(world, player, () -> {
                final BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof TileEntitySoundTimer) {
                    ((TileEntitySoundTimer) entity).sync();
                    PacketTrainDataGuiServer.openSoundTimerScreenS2C((ServerPlayerEntity) player, pos);
                }
        });
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWERED, false));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new TileEntitySoundTimer();
    }

    public static class TileEntitySoundTimer extends BlockEntity implements Tickable, BlockEntityClientSerializable {
        private String message = "";
        private Boolean fire_redstone = false;
        private static final String KEY_MESSAGE = "message";
        private static final String FIRE_MESSAGE = "fire_redstone";

        public TileEntitySoundTimer() {
            super(Main.SOUND_TIMER_ENTITY);
        }

        @Environment(EnvType.CLIENT)
        public double getRenderDistance() {
            return 128.0D;
        }

        @Override
        public void tick() {
            if(!world.isClient) {

                ServerWorld serverworld = (ServerWorld) world;
                final Block block = serverworld.getBlockState(pos).getBlock();
                if(getStatus()) {
                    serverworld.setBlockState(pos, serverworld.getBlockState(pos).with(SoundTimer.POWERED, true));
                } else {
                    serverworld.getBlockTickScheduler().schedule(pos, block, 20);
                }
            } else if (world != null && world.isClient) {
                final Set<Route.ScheduleEntry> schedules;
                BlockPos pos1 = new BlockPos(pos.getX(),pos.getY(), pos.getZ());
                BlockPos pos2 = new BlockPos(pos.getX(),pos.getY()+1, pos.getZ());
                Platform myPlatform = null;
                Platform myPlatform2 = null;
                //final Platform platform = RailwayData.getClosePlatform(pos1);
                //final Platform platform2 = ClientData.getClosePlatform(pos2);
                final RailwayData railwayData = RailwayData.getInstance(world);
                if(railwayData != null) {
                    try {
                        myPlatform = railwayData.platforms.stream().filter(platform -> platform.isCloseToSavedRail(pos1)).findFirst().orElse(null);
                        myPlatform2 = railwayData.platforms.stream().filter(platform -> platform.isCloseToSavedRail(pos2)).findFirst().orElse(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                myPlatform.
//
//                if (myPlatform == null && myPlatform2 == null) {
//                    //System.out.println("No Platform");
//                    return;
//                }
//                if(myPlatform != null) {
//                    schedules = railwayData.getSchedulesForStation(myPlatform.);
//                    schedules = ClientData.SCHEDULES_FOR_PLATFORM.get(platform.id);
//                } else {
//                    schedules = ClientData.SCHEDULES_FOR_PLATFORM.get(platform2.id);
//                }

//                if (schedules == null) {
//
//                    return;
//                }

                if(seconds <= Integer.parseInt(getMessage()) && seconds > 0) {
                    setStatus(true);
                } else if(seconds <= 0) {
                    setStatus(false);
                }

                System.out.println(seconds);
//
//                final List<Route.ScheduleEntry> scheduleList = new ArrayList<>(schedules);
//                Collections.sort(scheduleList);
//
//                final Route.ScheduleEntry currentSchedule = scheduleList.get(0);
//                seconds = (int) ((currentSchedule.arrivalMillis - System.currentTimeMillis()) / 1000);

            }

        }


        public String getMessage() {
            if(message == "") {
                return "10";
            } else {
                return message;
            }

        }

        public Boolean getStatus() {
            return fire_redstone;
        }

        public void setStatus(Boolean tf) {
            this.fire_redstone = tf;
            markDirty();
            sync();
        }

        public void setMessage(String message) {
            this.message = message;
            markDirty();
            sync();
        }

        @Override
        public void fromTag(BlockState state, NbtCompound nbtCompound) {
            super.fromTag(state, nbtCompound);
            fromClientTag(nbtCompound);
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbtCompound) {
            super.writeNbt(nbtCompound);
            toClientTag(nbtCompound);
            return nbtCompound;
        }

        @Override
        public void fromClientTag(NbtCompound nbtCompound) {
            message = nbtCompound.getString(KEY_MESSAGE);
            fire_redstone = nbtCompound.getBoolean(FIRE_MESSAGE);
        }

        @Override
        public NbtCompound toClientTag(NbtCompound nbtCompound) {
            nbtCompound.putString(KEY_MESSAGE, message);
            nbtCompound.putBoolean(FIRE_MESSAGE, fire_redstone);
            return nbtCompound;
        }



    }

}