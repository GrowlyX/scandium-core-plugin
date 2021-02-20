package vip.potclub.kotlin.clickable

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import java.util.*

class ChatClickable {

    private val components: MutableList<TextComponent> = ArrayList()

    constructor(msg: String?) {
        val message = TextComponent(msg)
        components.add(message)
    }

    constructor(msg: String?, hoverMsg: String?, clickString: String?) {
        this.add(msg, hoverMsg, clickString, ClickEvent.Action.RUN_COMMAND)
    }

    constructor(msg: String?, hoverMsg: String?, clickString: String?, action: ClickEvent.Action?) {
        this.add(msg, hoverMsg, clickString, action)
    }

    fun add(msg: String?, hoverMsg: String?): TextComponent {
        val message = TextComponent(msg)

        if (hoverMsg != null) message.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(hoverMsg).create())

        components.add(message).also {
            return message
        }
    }

    fun add(msg: String?, hoverMsg: String?, clickString: String?, action: ClickEvent.Action?): TextComponent {
        val message = TextComponent(msg)

        if (hoverMsg != null) message.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder(hoverMsg).create()
        )

        if (clickString != null) message.clickEvent = ClickEvent(action, clickString)

        components.add(message).also {
            return message
        }
    }

    fun add(message: String?) {
        components.add(TextComponent(message))
    }

    fun asComponents(): Array<TextComponent> {
        return components.toTypedArray()
    }
}

