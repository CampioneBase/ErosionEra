package lunastic.erosion_era.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ErosionEraBlockSoundGroup extends BlockSoundGroup {
    public static final BlockSoundGroup ERODED_BLOCK = new ErosionEraBlockSoundGroup(
            1.0f, 1.0f,
            SoundEvents.BLOCK_GLASS_BREAK,
            SoundEvents.BLOCK_GLASS_STEP,
            SoundEvents.BLOCK_GLASS_PLACE,
            SoundEvents.BLOCK_GLASS_HIT,
            SoundEvents.BLOCK_GLASS_FALL
    );;

    public ErosionEraBlockSoundGroup(
            // 音量
            float volume,
            // 音高
            float pitch,
            // 破坏音效
            SoundEvent breakSound,
            // 踩踏音效
            SoundEvent stepSound,
            // 放置音效
            SoundEvent placeSound,
            // 击打音效
            SoundEvent hitSound,
            // 坠落音效
            SoundEvent fallSound
    ) {
        super(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }
}
