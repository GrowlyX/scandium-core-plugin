package vip.potclub.kotlin.test

import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Moose(val name: String) {

    companion object UTILITY {
        fun sendPlayerMooseName(moose: Moose, player: Player) {
            player.sendMessage(moose.name)
        }
    }

    /*
      This is a public void method in kotlin
     */
    fun hello(string: String) {
        println(string)
    }

    /*
  This is a private void method in kotlin
 */
    private fun hello(string: String, player: Player) {
        player.sendMessage("This is a normal string right?" + " ${ChatColor.AQUA}Here is a special colored string!")
    }

    /*
     This is a public method that returns a player
*/
    fun retusn(string: String, player: Player) : Player {
        return player
    }

    /*
 This is a private method that returns a player
*/
    private fun privateee(string: String, player: Player) : Player {
        return player
    }
}
