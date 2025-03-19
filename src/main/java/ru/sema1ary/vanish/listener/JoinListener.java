package ru.sema1ary.vanish.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.sema1ary.vanish.Vanish;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vanish.service.VanishUserService;
import ru.sema1ary.vedrocraftapi.player.PlayerUtil;
import ru.sema1ary.vedrocraftapi.service.ConfigService;

@RequiredArgsConstructor
public class JoinListener implements Listener {
    private final Vanish plugin;
    private final ConfigService configService;
    private final VanishUserService userService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        VanishUser user = userService.getUser(player.getName());

        if(!user.isVanishActive()) {
            return;
        }

        event.joinMessage(null);

        PlayerUtil.sendMessage(player, (String) configService.get("join-message"));
        userService.hidePlayer(player);

        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if(player.hasPermission("vanish.see")) {
                return;
            }

            if(userService.getUser(onlinePlayer.getName()).isVanishActive()) {
                player.hidePlayer(plugin, onlinePlayer);
            }
        });
    }
}
