package ru.sema1ary.vanish.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vanish.service.VanishUserService;

@RequiredArgsConstructor
public class PreJoinListener implements Listener {
    private final VanishUserService userService;

    @EventHandler
    private void onJoin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();

        if(username.isEmpty()) {
            return;
        }

        if(userService.findByUsername(username).isEmpty()) {
            userService.save(VanishUser.builder()
                    .username(username)
                    .isVanishActive(false)
                    .build());
        }
    }
}
