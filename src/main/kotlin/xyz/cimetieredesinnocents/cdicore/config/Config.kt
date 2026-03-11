package xyz.cimetieredesinnocents.cdicore.config

import net.neoforged.neoforge.common.ModConfigSpec
import xyz.cimetieredesinnocents.cdilib.utils.getValue

object Config {
    private val BUILDER = ModConfigSpec.Builder()
        .comment("CDICore配置")
    val averageForgetTime by BUILDER
        .comment("平均科技遗忘时间")
        .define("averageForgetTime", 24000)
    val insightTechPointsLimit by BUILDER
        .comment("顿悟科技点上限")
        .define("insightTechPointsLimit", 3)
    val SPEC = BUILDER.build()
}