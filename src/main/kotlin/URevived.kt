package net.im45.bot

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregisterAll
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.info

object URevived : KotlinPlugin(
    JvmPluginDescription(
        id = "net.im45.bot.u-revived",
        name = "U Revived",
        version = "1.0-SNAPSHOT",
    ) {
        author("45gfg9")
    }
) {
    private val unixTimestamp: Long
        get() = System.currentTimeMillis() / 1000L

    override fun onEnable() {
        URCommand.register()
        URConfig.reload()
        URData.reload()

        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            URConfig.urs[sender.id]?.let {
                val last = URData.lastTime[sender.id] ?: 0

                if (unixTimestamp - last >= it.first)
                    this.group.sendMessage(it.second)

                URData.lastTime[sender.id] = unixTimestamp
            }
        }

        logger.info { "U Revived!" }
    }

    override fun onDisable() {
        unregisterAll()
    }

    object URCommand : CompositeCommand(
        URevived,
        "urevived",
        "ur"
    ) {
        @SubCommand("bind")
        suspend fun CommandSender.bind(user: User, interval: Int, vararg msg: String) {
            URConfig.urs[user.id] = Pair(interval, msg.joinToString(" "))
            URData.lastTime[user.id] = unixTimestamp
            sendMessage("Bound user $user")
        }

        @SubCommand("unbind")
        suspend fun CommandSender.bind(user: User) {
            URConfig.urs.remove(user.id)?.run { sendMessage("Unbound user $user") }
        }
    }

    object URConfig : AutoSavePluginConfig("urcnf") {
        val urs: MutableMap<Long, Pair<Int, String>> by value(mutableMapOf())
    }

    object URData : AutoSavePluginData("urdata") {
        val lastTime: MutableMap<Long, Long> by value(mutableMapOf())
    }
}
