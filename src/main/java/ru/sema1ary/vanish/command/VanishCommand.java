package ru.sema1ary.vanish.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.sema1ary.vanish.model.VanishUser;
import ru.sema1ary.vanish.service.VanishUserService;
import ru.sema1ary.vedrocraftapi.player.PlayerUtil;
import ru.sema1ary.vedrocraftapi.service.ConfigService;

@RequiredArgsConstructor
@Command(name = "vanish", aliases = {"v"})
public class VanishCommand {
    private final MiniMessage miniMessage;
    private final ConfigService configService;
    private final VanishUserService userService;

    @Async
    @Execute(name = "reload")
    @Permission("vanish.reload")
    void reload(@Context CommandSender sender) {
        configService.reload();

        PlayerUtil.sendMessage(sender, (String) configService.get("reload-message"));
    }

    @Execute
    @Permission("vanish.use")
    void execute(@Context Player sender) {
        VanishUser user = userService.getUser(sender.getName());

        if(user.isVanishActive()) {
            user.setVanishActive(false);
            PlayerUtil.sendMessage(sender, (String) configService.get("vanish-disabled-message"));
            userService.showPlayer(sender);
        } else {
            user.setVanishActive(true);
            PlayerUtil.sendMessage(sender, (String) configService.get("vanish-enabled-message"));
            userService.hidePlayer(sender);
        }

        userService.save(user);
    }

    @Execute
    @Permission("vanish.use.other")
    void execute(@Context Player sender, @Arg("игрок") Player target) {
        VanishUser user = userService.getUser(target.getName());

        if(user.isVanishActive()) {
            user.setVanishActive(false);
            PlayerUtil.sendMessage(sender,
                    ((String) configService.get("vanish-target-disabled-message"))
                            .replace("{player}", target.getName())
            );
            userService.showPlayer(target);
        } else {
            user.setVanishActive(true);
            PlayerUtil.sendMessage(sender,
                    ((String) configService.get("vanish-target-enabled-message"))
                            .replace("{player}", target.getName())
            );
            userService.hidePlayer(target);
        }

        userService.save(user);
    }
}
